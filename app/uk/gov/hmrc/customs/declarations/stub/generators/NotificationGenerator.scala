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

package uk.gov.hmrc.customs.declarations.stub.generators

import uk.gov.hmrc.customs.declarations.stub.generators.NotificationGenerator.{additionalInformation, externalAmendment, Amended, FunctionCode}

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import scala.xml._

class NotificationGenerator @Inject() (notificationValueGenerator: NotificationValueGenerator) {

  private def adjustTime(time: ZonedDateTime): String =
    time.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssX"))

  private val format304: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX")

  def generate(lrn: String, mrn: String, statuses: Seq[FunctionCode]): String = {
    val issueAt =
      if (statuses == List(Amended)) ZonedDateTime.now(ZoneId.of("Europe/London")).plusMinutes(5)
      else ZonedDateTime.now(ZoneId.of("Europe/London"))

    val acceptanceDateTime: NodeSeq = if (!lrn.startsWith("Q")) {
      <p:AcceptanceDateTime>
        <p2:DateTimeString xmlns:p2="urn:wco:datamodel:WCO:Response_DS:DMS:2" formatCode="304">{format304.format(issueAt)}</p2:DateTimeString>
      </p:AcceptanceDateTime>
    } else NodeSeq.Empty

    val responses = statuses.zipWithIndex.map { case (status, index) =>
      notificationResponse(status, mrn, acceptanceDateTime, issueAt.plusSeconds(index.toLong), lrn)
    }

    if (statuses == List(Amended)) genExternalAmendmentNotifications(responses) else genNotification(responses)
  }

  // scalastyle:off
  private def genExternalAmendmentNotifications(responses: Seq[Elem]): String =
    <urn:MetaData xmlns:urn="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xs:schemaLocation="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2 ../DocumentMetaData_2_DMS.xsd" xmlns:xs="http://www.w3.org/2001/XMLSchema">
      <md:WCODataModelVersionCode xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">3.6</md:WCODataModelVersionCode>
      <md:WCOTypeName xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">RES</md:WCOTypeName>
      <md:ResponsibleCountryCode/>
      <md:ResponsibleAgencyName/>
      <md:AgencyAssignedCustomizationCode/>
      <md:AgencyAssignedCustomizationVersionCode/>
      {responses}
    </urn:MetaData>.toString
  // scalastyle:on

  // scalastyle:off
  private def genNotification(responses: Seq[Elem]): String =
    <urn:MetaData xmlns:urn="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xs:schemaLocation="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2 ../DocumentMetaData_2_DMS.xsd" xmlns:xs="http://www.w3.org/2001/XMLSchema">
      <md:WCODataModelVersionCode xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">3.6</md:WCODataModelVersionCode>
      <md:WCOTypeName xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">DEC</md:WCOTypeName>
      <md:ResponsibleCountryCode xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">NL</md:ResponsibleCountryCode>
      <md:ResponsibleAgencyName xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">Duane</md:ResponsibleAgencyName>
      <md:AgencyAssignedCustomizationVersionCode xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">v2.1</md:AgencyAssignedCustomizationVersionCode>{
      responses
    }
    </urn:MetaData>.toString
  // scalastyle:on

  // scalastyle:off
  private def notificationResponse(code: FunctionCode, mrn: String, acceptanceDateTime: NodeSeq, issuedAt: ZonedDateTime, lrn: String): Elem = {
    val functionalReference = UUID.randomUUID().toString.replace("-", "")
    val hasAdditionalInformationNode = lrn.startsWith("Q") && code != Amended

    val errorNode = if (lrn.startsWith("BCDS")) {
      val CDSErrorCode = lrn.substring(1, 9)
      ErrorsGenerator.errors.get(CDSErrorCode).getOrElse(ErrorsGenerator.errors("CDS10020"))
    } else ErrorsGenerator.errors("CDS10020")

    <p:Response xmlns:p="urn:wco:datamodel:WCO:RES-DMS:2" xsi:schemaLocation="urn:wco:datamodel:WCO:RES-DMS:2 ../WCO_RES_2_DMS.xsd ">
      <p:FunctionCode>{code.functionCode}</p:FunctionCode>
      <p:FunctionalReferenceID>{functionalReference}</p:FunctionalReferenceID>
      <p:IssueDateTime>
        <p2:DateTimeString xmlns:p2="urn:wco:datamodel:WCO:Response_DS:DMS:2" formatCode="304">{format304.format(issuedAt)}</p2:DateTimeString>
      </p:IssueDateTime>
      {if (hasAdditionalInformationNode) additionalInformation else NodeSeq.Empty}
      {nameCode(code)}
      <p:Declaration>
        {acceptanceDateTime}
        <p:FunctionalReferenceID>{lrn}</p:FunctionalReferenceID>
        <p:ID>{mrn}</p:ID>
        <p:VersionID>{if (code == Amended) 2 else 1}</p:VersionID>
      </p:Declaration>
      {if (code == Amended) externalAmendment else NodeSeq.Empty}
      {if (code.isError) errorNode else NodeSeq.Empty}
    </p:Response>
  }
// scalastyle:on

  def nameCode(code: FunctionCode): NodeSeq =
    if (code.nameCode.isDefined)
      <resp:Status>
        <resp:NameCode>
          {code.nameCode.get}
        </resp:NameCode>
      </resp:Status>
    else NodeSeq.Empty

  def generateAcceptNotificationWithRandomMRN(): Elem =
    generateAcceptNotification(notificationValueGenerator.generateMRN())

  private val firstDate = ZonedDateTime.now(ZoneId.of("Europe/London"))
  private val secondDate = firstDate.plusMinutes(5)

  // scalastyle:off
  def generateAcceptNotification(mrn: String = "18GBJCM3USAFD2WD51"): Elem =
    <md:MetaData xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">
      <md:WCODataModelVersionCode>3.6</md:WCODataModelVersionCode>
      <md:WCOTypeName>RES</md:WCOTypeName>
      <md:ResponsibleCountryCode/>
      <md:ResponsibleAgencyName/>
      <md:AgencyAssignedCustomizationCode/>
      <md:AgencyAssignedCustomizationVersionCode/>
      <resp:Response xmlns:resp="urn:wco:datamodel:WCO:RES-DMS:2">
        <resp:FunctionCode>01</resp:FunctionCode>
        <resp:FunctionalReferenceID>a5b4aeb03a384a5faf1b9afeff5dac97</resp:FunctionalReferenceID>
        <resp:IssueDateTime>
          <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">{adjustTime(secondDate)}</_2_2:DateTimeString>
        </resp:IssueDateTime>
        <resp:Declaration>
          <resp:AcceptanceDateTime>
            <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">20181001000000Z</_2_2:DateTimeString>
          </resp:AcceptanceDateTime>
          <resp:FunctionalReferenceID>Export_Accepted</resp:FunctionalReferenceID>
          <resp:ID>{mrn}</resp:ID>
          <resp:VersionID>1</resp:VersionID>
        </resp:Declaration>
      </resp:Response>
      <resp:Response xmlns:resp="urn:wco:datamodel:WCO:RES-DMS:2">
        <resp:FunctionCode>09</resp:FunctionCode>
        <resp:FunctionalReferenceID>f634f77719d546a7b0eb83092bb3a015</resp:FunctionalReferenceID>
        <resp:IssueDateTime>
          <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">{adjustTime(firstDate)}</_2_2:DateTimeString>
        </resp:IssueDateTime>
        <resp:AdditionalInformation>
          <resp:StatementCode>A2</resp:StatementCode>
          <resp:StatementTypeCode>AFB</resp:StatementTypeCode>
        </resp:AdditionalInformation>
        <resp:Declaration>
          <resp:FunctionalReferenceID>Export_Accepted</resp:FunctionalReferenceID>
          <resp:ID>{mrn}</resp:ID>
          <resp:VersionID>1</resp:VersionID>
        </resp:Declaration>
      </resp:Response>
    </md:MetaData>
}
// scalastyle:on

object NotificationGenerator {

  val additionalInformation: NodeSeq =
    <AdditionalInformation>
      <StatementCode>Q01</StatementCode>
      <StatementDescription>Urgent: Your Declaration or Goods are being queried. Customs authorities have
        raised a query, or they have a further query for you. You must sign in to view and act on the query
        message each time in order for your declaration to progress. Step 1. Search online for ‘Upload documents
        and get messages for the Customs Declaration Service’ on the GOV.UK website. Step 2. Sign in using the
        Government Gateway account that is linked to your EORI number. Step 3. Select the View messages option.
      </StatementDescription>
      <StatementTypeCode>QRY</StatementTypeCode>
    </AdditionalInformation>

  val externalAmendment: NodeSeq =
    <_2_1:AdditionalInformation>
        <_2_1:StatementDescription>Test amending item price</_2_1:StatementDescription>
        <_2_1:StatementTypeCode>AES</_2_1:StatementTypeCode>
        <_2_1:Pointer>
          <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
          <_2_1:DocumentSectionCode>07B</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
          <_2_1:DocumentSectionCode>06A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
      </_2_1:AdditionalInformation>
      <_2_1:Amendment>
        <_2_1:ChangeReasonCode>32</_2_1:ChangeReasonCode>
        <_2_1:Pointer>
          <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:DocumentSectionCode>99B</_2_1:DocumentSectionCode>
          <_2_1:TagID>465</_2_1:TagID>
        </_2_1:Pointer>
      </_2_1:Amendment>

  val characterValue: Map[Char, Int] = Map(
    '0' -> 0,
    '1' -> 1,
    '2' -> 2,
    '3' -> 3,
    '4' -> 4,
    '5' -> 5,
    '6' -> 6,
    '7' -> 7,
    '8' -> 8,
    '9' -> 9,
    'A' -> 10,
    'B' -> 12,
    'C' -> 13,
    'D' -> 14,
    'E' -> 15,
    'F' -> 16,
    'G' -> 17,
    'H' -> 18,
    'I' -> 19,
    'J' -> 20,
    'K' -> 21,
    'L' -> 23,
    'M' -> 24,
    'N' -> 25,
    'O' -> 26,
    'P' -> 27,
    'Q' -> 28,
    'R' -> 20,
    'S' -> 30,
    'T' -> 31,
    'U' -> 32,
    'V' -> 34,
    'W' -> 35,
    'X' -> 36,
    'Y' -> 37,
    'Z' -> 38
  )

  sealed trait FunctionCode {
    def fullCode: String

    def functionCode: String = fullCode.take(2)

    def nameCode: Option[String] = None

    def isError: Boolean
  }

  case object Accepted extends FunctionCode {
    val fullCode: String = "01"

    val isError = false
  }

  case object Received extends FunctionCode {
    val fullCode: String = "02"

    val isError = false
  }

  case class Rejected(isError: Boolean) extends FunctionCode {
    val fullCode: String = "03"
  }

  case object UndergoingPhysicalCheck extends FunctionCode {
    val fullCode: String = "05"

    override def toString(): String = "Undergoing Physical Check"

    val isError = false
  }

  case object AdditionalDocumentsRequired extends FunctionCode {
    val fullCode: String = "06"

    override def toString(): String = "Additional Documents Required"

    val isError = false
  }

  case object Amended extends FunctionCode {
    val fullCode: String = "07"
    val isError = false
  }

  case object Released extends FunctionCode {
    val fullCode: String = "08"
    val isError = false
  }

  case object Cleared extends FunctionCode {
    val fullCode: String = "09"
    val isError = false
  }

  case object Cancelled extends FunctionCode {
    val fullCode: String = "10"
    val isError = false
  }

  case object CustomsPositionGranted extends FunctionCode {
    val fullCode: String = "1139"
    val isError = false

    override val nameCode = Some("39")

    override def toString(): String = "Customs Position Granted"
  }

  case object CustomsPositionDenied extends FunctionCode {
    val fullCode: String = "1141"
    val isError = false

    override val nameCode = Some("41")

    override def toString(): String = "Customs Position Denied"
  }

  case object taxLiability extends FunctionCode {
    val fullCode: String = "13"
    val isError = false

    override def toString(): String = "Please make a payment to cover taxes"
  }

  case object insufficientBalanceInDan extends FunctionCode {
    val fullCode: String = "14"
    val isError = false

    override def toString(): String =
      "Your account does not have enough money in it. Please make a payment to release your goods"
  }

  case object insufficientBalanceInDanReminder extends FunctionCode {
    val fullCode: String = "15"
    val isError = false

    override def toString(): String = "Your goods are waiting for you. Please make an immediate payment to release them"
  }

  case object GoodsHaveExitedTheCommunity extends FunctionCode {
    val fullCode: String = "16"
    val isError = false

    override def toString(): String = "Goods Have Exited The Community"
  }

  case object DeclarationHandledExternally extends FunctionCode {
    val fullCode: String = "17"
    val isError = false

    override def toString(): String = "Declaration Handled Externally"
  }

  case object AwaitingExitResults extends FunctionCode {
    val fullCode: String = "18"
    val isError = false

    override def toString(): String = "Awaiting Exit Results"
  }

  case object QueryNotificationMessage extends FunctionCode {
    val fullCode: String = "51"
    val isError = false

    override def toString(): String = "Query Notification Message"
  }
}
