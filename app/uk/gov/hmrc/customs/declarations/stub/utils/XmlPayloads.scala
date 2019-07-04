/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.customs.declarations.stub.utils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object XmlPayloads {

  val firstDate = LocalDateTime.now()
  val secondDate = firstDate.plusMinutes(5)
  val thirdDate = firstDate.plusMinutes(10)

  def adjustTime(time: LocalDateTime): String = DateTimeFormatter.ofPattern("yyyyMMddhhmmss").format(time) + "Z"
  
  def acceptedExportNotification(mrn: String = "18GBJCM3USAFD2WD51") = <md:MetaData xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">
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
