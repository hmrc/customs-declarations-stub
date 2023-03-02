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

import uk.gov.hmrc.customs.declarations.stub.generators.NotificationGenerator.FunctionCode

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import scala.xml._

class NotificationGenerator @Inject() (notificationValueGenerator: NotificationValueGenerator) {
  private def adjustTime(time: ZonedDateTime): String =
    time.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssX"))

  val firstDate = ZonedDateTime.now(ZoneId.of("Europe/London"))
  val secondDate = firstDate.plusMinutes(5)
  val thirdDate = firstDate.plusMinutes(10)

  val format304: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX")

  def generate(lrn: String, mrn: String, statuses: Seq[FunctionCode]): String = {
    val issueAt = ZonedDateTime.now(ZoneId.of("Europe/London"))

    val declaration = {
      val acceptanceDateTime: NodeSeq = if (!lrn.startsWith("Q")) {
        <p:AcceptanceDateTime>
          <p2:DateTimeString xmlns:p2="urn:wco:datamodel:WCO:Response_DS:DMS:2" formatCode="304">{format304.format(issueAt)}</p2:DateTimeString>
        </p:AcceptanceDateTime>
      } else NodeSeq.Empty

      <p:Declaration>
        {acceptanceDateTime}
        <p:FunctionalReferenceID>{lrn}</p:FunctionalReferenceID>
        <p:ID>{mrn}</p:ID>
        <p:VersionID>1</p:VersionID>
      </p:Declaration>
    }

    val responses = statuses.zipWithIndex.map { case (status, index) =>
      notificationResponse(status, declaration, issueAt.plusSeconds(index), lrn)
    }

// scalastyle:off
    <urn:MetaData xmlns:urn="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xs:schemaLocation="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2 ../DocumentMetaData_2_DMS.xsd" xmlns:xs="http://www.w3.org/2001/XMLSchema">
      <md:WCODataModelVersionCode xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">3.6</md:WCODataModelVersionCode>
      <md:WCOTypeName xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">DEC</md:WCOTypeName>
      <md:ResponsibleCountryCode xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">NL</md:ResponsibleCountryCode>
      <md:ResponsibleAgencyName xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">Duane</md:ResponsibleAgencyName>
      <md:AgencyAssignedCustomizationVersionCode xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">v2.1</md:AgencyAssignedCustomizationVersionCode>
      {responses}
    </urn:MetaData>.toString
  }
// scalastyle:on

// scalastyle:off
  private def notificationResponse(code: FunctionCode, declaration: NodeSeq, issuedAt: ZonedDateTime, lrn: String): Elem = {

    val functionalReference = UUID.randomUUID().toString.replace("-", "")

    val additionalInformation: NodeSeq = if (lrn.startsWith("Q")) {
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
    } else NodeSeq.Empty

    val errorNode = if (code.isError) {
      <_2_1:Error>
        <_2_1:ValidationCode>CDS10020</_2_1:ValidationCode>
        <_2_1:Pointer>
          <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
          <_2_1:DocumentSectionCode>68A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:SequenceNumeric>2</_2_1:SequenceNumeric>
          <_2_1:DocumentSectionCode>02A</_2_1:DocumentSectionCode>
          <_2_1:TagID>360</_2_1:TagID>
        </_2_1:Pointer>
      </_2_1:Error>
    } else {
      NodeSeq.Empty
    }

    val maybeNameCode: NodeSeq = if (code.nameCode.isDefined) {
      <resp:Status>
        <resp:NameCode>
          {code.nameCode.get}
        </resp:NameCode>
      </resp:Status>
    } else {
      NodeSeq.Empty
    }

    <p:Response xmlns:p="urn:wco:datamodel:WCO:RES-DMS:2" xsi:schemaLocation="urn:wco:datamodel:WCO:RES-DMS:2 ../WCO_RES_2_DMS.xsd ">
      <p:FunctionCode>{code.functionCode}</p:FunctionCode>
      <p:FunctionalReferenceID>{functionalReference}</p:FunctionalReferenceID>
      <p:IssueDateTime>
        <p2:DateTimeString xmlns:p2="urn:wco:datamodel:WCO:Response_DS:DMS:2" formatCode="304">{format304.format(issuedAt)}</p2:DateTimeString>
      </p:IssueDateTime>
      {additionalInformation}
      {maybeNameCode}
      {declaration}
      {errorNode}
    </p:Response>
  }
// scalastyle:on

  def generateAcceptNotificationWithRandomMRN(): Elem =
    generateAcceptNotification(notificationValueGenerator.generateMRN())

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
          <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">{adjustTime(thirdDate)}</_2_2:DateTimeString>
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
        <resp:FunctionCode>16</resp:FunctionCode>
        <resp:FunctionalReferenceID>d5710483848740849ce7415470c2886a</resp:FunctionalReferenceID>
        <resp:IssueDateTime>
          <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">{adjustTime(firstDate)}</_2_2:DateTimeString>
        </resp:IssueDateTime>
        <resp:AppealOffice>
          <resp:ID>GBLBA001</resp:ID>
        </resp:AppealOffice>
        <resp:Bank>
          <resp:ReferenceID>CITIGB2LLON</resp:ReferenceID>
          <resp:ID>GB25CITI08320011963155</resp:ID>
        </resp:Bank>
        <resp:ContactOffice>
          <resp:ID>GBLBA001</resp:ID>
          <resp:Communication>
            <resp:ID>See Developer Hub</resp:ID>
            <resp:TypeCode>EM</resp:TypeCode>
          </resp:Communication>
          <resp:Communication>
            <resp:ID>+441234567891</resp:ID>
            <resp:TypeCode>FX</resp:TypeCode>
          </resp:Communication>
        </resp:ContactOffice>
        <resp:Status>
          <resp:NameCode>4</resp:NameCode>
        </resp:Status>
        <resp:Declaration>
          <resp:FunctionalReferenceID>Export_Accepted</resp:FunctionalReferenceID>
          <resp:ID>{mrn}</resp:ID>
          <resp:VersionID>1</resp:VersionID>
          <resp:GoodsShipment>
            <resp:GovernmentAgencyGoodsItem>
              <resp:SequenceNumeric>1</resp:SequenceNumeric>
              <resp:Commodity>
                <resp:DutyTaxFee>
                  <resp:DeductAmount>0.00</resp:DeductAmount>
                  <resp:DutyRegimeCode>100</resp:DutyRegimeCode>
                  <resp:TaxRateNumeric>9.70</resp:TaxRateNumeric>
                  <resp:TypeCode>A00</resp:TypeCode>
                  <resp:Payment>
                    <resp:TaxAssessedAmount>97.00</resp:TaxAssessedAmount>
                    <resp:PaymentAmount>97.00</resp:PaymentAmount>
                  </resp:Payment>
                </resp:DutyTaxFee>
                <resp:DutyTaxFee>
                  <resp:DeductAmount>0.00</resp:DeductAmount>
                  <resp:DutyRegimeCode>100</resp:DutyRegimeCode>
                  <resp:TaxRateNumeric>20.00</resp:TaxRateNumeric>
                  <resp:TypeCode>B00</resp:TypeCode>
                  <resp:Payment>
                    <resp:TaxAssessedAmount>219.40</resp:TaxAssessedAmount>
                    <resp:PaymentAmount>219.40</resp:PaymentAmount>
                  </resp:Payment>
                </resp:DutyTaxFee>
              </resp:Commodity>
            </resp:GovernmentAgencyGoodsItem>
          </resp:GoodsShipment>
        </resp:Declaration>
      </resp:Response>
      <resp:Response xmlns:resp="urn:wco:datamodel:WCO:RES-DMS:2">
        <resp:FunctionCode>09</resp:FunctionCode>
        <resp:FunctionalReferenceID>f634f77719d546a7b0eb83092bb3a015</resp:FunctionalReferenceID>
        <resp:IssueDateTime>
          <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">{adjustTime(secondDate)}</_2_2:DateTimeString>
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
