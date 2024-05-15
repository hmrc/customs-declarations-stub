/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.customs.declarations.stub.connector

import org.apache.pekko.actor.ActorSystem
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import uk.gov.hmrc.customs.declarations.stub.config.AppConfig
import uk.gov.hmrc.customs.declarations.stub.generators.NotificationGenerator
import uk.gov.hmrc.customs.declarations.stub.generators.NotificationGenerator._
import uk.gov.hmrc.customs.declarations.stub.models.Client
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.wco.dec.{Declaration, MetaData}

import java.time.{ZoneId, ZonedDateTime}
import java.util.{Timer, TimerTask}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, _}
import scala.util.Random

@Singleton
class NotificationConnector @Inject() (http: HttpClient, generator: NotificationGenerator)(
  implicit val appConfig: AppConfig,
  ec: ExecutionContext,
  actorSystem: ActorSystem
) {
  private val logger = Logger(this.getClass)
  private val defaultDelay = 0.seconds

  def notifyInDueCourse(
    operation: String,
    conversationId: String,
    maybeSubmissionConversationId: Option[String],
    client: Client,
    meta: MetaData,
    duration: FiniteDuration = Duration(2, "secs")
  ): Unit =
    actorSystem.scheduler.scheduleOnce(duration) {
      logger.info("Entering async request notification")

      meta.declaration.fold(throw new Exception("No declaration found in metadata")) { declaration =>
        if (declaration.borderTransportMeans.flatMap(_.id) != Some("NONOTIFY")) {

          val (delay, primaryXml, maybeAuxiliaryXml) = {
            lazy val default = generator.generateAcceptNotificationWithRandomMRN().toString
            generate(default, operation, declaration)
          }

          if (primaryXml.trim.length > 0) {
            logger.debug(s"Scheduling transmission of primary notifications:\n$primaryXml")
            sendNotificationWithDelay(client, conversationId, primaryXml, delay)
          } else logger.debug(s"No notification will be sent (probably due to the 'PENDING' flag)")

          maybeAuxiliaryXml.map { auxiliaryXml =>
            logger.info("Sending auxiliary notification with original X-Conversation-ID")
            logger.debug(s"Scheduling transmission of auxiliary notifications:\n$auxiliaryXml")
            val id = maybeSubmissionConversationId.fold(conversationId)(identity)
            sendNotificationWithDelay(client, id, auxiliaryXml, delay + 5.seconds)
          }
        }
      }
    }

  private val validPrompts = List('B', 'C', 'D', 'G', 'Q', 'I', 'J', 'K', 'L', 'P', 'R', 'U', 'X')

  // scalastyle:off
  private def generate(default: String, operation: String, declaration: Declaration): (FiniteDuration, String, Option[String]) = {
    val mrn = extractMrn(declaration)
    val maybeLrn = declaration.functionalReferenceId
    val maybeBorderTransportId = declaration.borderTransportMeans.flatMap(_.id)

    val (primaryNotifications, maybeAuxiliaryNotifications) =
      maybeLrn.fold(throw new Exception("No LRN found in declaration")) { lrn =>
        operation match {
          case "cancel" =>
            val cancellationActionNotificationsSequence = getCancellationNotificationSequence(lrn.charAt(2))
            val cancellationActionNotifications = generator.generate(lrn, mrn, cancellationActionNotificationsSequence)
            val submissionActionNotification =
              if (!cancellationActionNotificationsSequence.last.equals(CustomsPositionGranted)) None
              else Some(generator.generate(lrn, mrn, List(Rejected(isError = false))))

            (cancellationActionNotifications, submissionActionNotification)

          case "submit" =>
            lrn.headOption match {
              case Some(notificationPrompt) if validPrompts.contains(notificationPrompt) =>
                val notificationForSubmission =
                  generator.generate(lrn, mrn, getSubmissionNotificationSequence(declaration.typeCode, notificationPrompt))

                val maybeNotificationForExternalAmendment =
                  if (!maybeBorderTransportId.exists(_ == "EXTERNAL AMEND")) None
                  else Some(generator.generate(lrn, mrn, List(Amended)))

                (notificationForSubmission, maybeNotificationForExternalAmendment)

              case _ => (importsSpecificErrors(lrn, mrn, default), None)
            }

          case "amend" =>
            val notificationSequence = maybeBorderTransportId match {
              case Some("DENIED")   => List(CustomsPositionDenied)
              case Some("PENDING")  => List.empty
              case Some("REJECTED") => List(Rejected(isError = true))
              case _                => List(CustomsPositionGranted)
            }
            (if (notificationSequence.isEmpty) "" else generator.generate(lrn, mrn, notificationSequence), None)
        }
      }
    (extractDelay(declaration), primaryNotifications, maybeAuxiliaryNotifications)
  }
  // scalastyle:on

  private def extractMrn(declaration: Declaration): String =
    declaration.id.getOrElse {
      val year = ZonedDateTime.now(ZoneId.of("Europe/London")).getYear % 100
      val country = "GB"
      val random = new Random()
      val code = random.nextInt(8999) + 1000
      val charSection = random.shuffle("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toSeq).take(2).mkString
      val secondCode = random.nextInt(8999999) + 1000000
      val withoutCheck = s"${year}${country}${code}${charSection}${secondCode}"
      val check = withoutCheck.zipWithIndex.foldLeft(0) { case (input, (char, index)) =>
        input + (NotificationGenerator.characterValue(char) * (1 << index))
      }
      val controlDigit = ((check % 11) % 10).toString
      withoutCheck + controlDigit
    }

  private val onDelay = "([BDGQRUX])([0-9])".r

  private def getCancellationNotificationSequence(notificationPrompt: Char): List[FunctionCode] = {
    val preliminaryNotification = Received
    val finalNotification = if (notificationPrompt == 'S') CustomsPositionGranted else CustomsPositionDenied
    List(preliminaryNotification, finalNotification)
  }

  // scalastyle:off
  private def getSubmissionNotificationSequence(decType: Option[String], notificationPrompt: Char): List[FunctionCode] = {
    val preliminaryNotifications = decType match {
      case Some(typeCode) if isAccepted(typeCode)            => List(Accepted)
      case Some(typeCode) if isReceivedAndAccepted(typeCode) => List(Received, Accepted)
      case _                                                 => List.empty
    }

    notificationPrompt match {
      case 'B' => List(Rejected(isError = true))
      case 'C' => preliminaryNotifications :+ Cleared
      case 'D' => preliminaryNotifications :+ AdditionalDocumentsRequired
      case 'G' => preliminaryNotifications
      case 'I' => preliminaryNotifications :+ AwaitingExitResults
      case 'J' => preliminaryNotifications :+ DeclarationHandledExternally
      case 'K' => preliminaryNotifications :+ Rejected(true) // Specific error code required
      case 'L' => preliminaryNotifications :+ Cancelled
      case 'P' => List.empty[FunctionCode]
      case 'Q' => preliminaryNotifications :+ QueryNotificationMessage
      case 'R' => List(Received)
      case 'U' => preliminaryNotifications :+ UndergoingPhysicalCheck
      case 'X' => preliminaryNotifications ++ List(Cleared, GoodsHaveExitedTheCommunity)
    }
  }
  // scalastyle:on

  private lazy val arrivedDeclarationCodes = List('A', 'C', 'B', 'J')
  private lazy val prelodgedDeclarationCodes = List('D', 'F', 'E', 'K')
  private lazy val supplementaryDeclarationCodes = List('X', 'Y')

  private def isAccepted(typeCode: String): Boolean =
    arrivedDeclarationCodes.contains(typeCode.last) || supplementaryDeclarationCodes.contains(typeCode.last)

  private def isReceivedAndAccepted(typeCode: String): Boolean =
    prelodgedDeclarationCodes.contains(typeCode.last)

  private def extractDelay(declaration: Declaration): FiniteDuration =
    declaration.functionalReferenceId.take(2).toSeq match {
      case Seq(onDelay(_, delay)) => Duration(delay.toLong, "secs")
      case _                      => defaultDelay
    }

  private def importsSpecificErrors(lrn: String, mrn: String, default: String): String =
    lrn match {
      case lrnForTaxLiability if lrnForTaxLiability.startsWith("TAX_LIABILITY") =>
        generator.generate(lrnForTaxLiability, mrn, List(NotificationGenerator.taxLiability))

      case lrnForBalance if lrnForBalance.startsWith("INSUFFICIENT") =>
        generator.generate(lrnForBalance, mrn, List(NotificationGenerator.insufficientBalanceInDan))

      case lrnForBalanceReminder if lrnForBalanceReminder.startsWith("REMINDER") =>
        generator.generate(lrnForBalanceReminder, mrn, List(NotificationGenerator.insufficientBalanceInDanReminder))

      case _ => default
    }

  private def sendNotificationWithDelay(client: Client, conversationId: String, xml: String, delay: Duration)(
    implicit rds: HttpReads[HttpResponse],
    ec: ExecutionContext
  ): Unit =
    (new Timer()).schedule(
      new TimerTask() {
        val payload = List(
          HeaderNames.AUTHORIZATION -> s"Bearer ${client.token}",
          HeaderNames.CONTENT_TYPE -> ContentTypes.XML,
          "X-Conversation-ID" -> conversationId
        )

        def run: Unit =
          http
            .POSTString(client.callbackUrl, xml, payload)(rds, HeaderCarrier(), ec)
            .onComplete(_ => logger.info("Exiting async request notification"))

      },
      delay.toMillis
    )
}
