/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.customs.declarations.stub.util

import scala.xml.Elem

object TestData {

  val validCancellationXml: Elem = <md:MetaData xmlns="urn:wco:datamodel:WCO:DEC-DMS:2" xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">
      <md:WCODataModelVersionCode>3.6</md:WCODataModelVersionCode>
      <md:WCOTypeName>DEC</md:WCOTypeName>
      <md:ResponsibleCountryCode>NL</md:ResponsibleCountryCode>
      <md:ResponsibleAgencyName>Duane</md:ResponsibleAgencyName>
      <md:AgencyAssignedCustomizationVersionCode>v2.1</md:AgencyAssignedCustomizationVersionCode>
      <Declaration>
        <FunctionCode>13</FunctionCode>
        <FunctionalReferenceID>Danielle_20180404_1154</FunctionalReferenceID>
        <ID>18GBJFKYDPAB34VGO7</ID>
        <TypeCode>INV</TypeCode>
        <Submitter>
          <ID>NL025115165432</ID>
        </Submitter>
        <AdditionalInformation>
          <StatementDescription>This is a duplicate, please cancel</StatementDescription>
          <StatementTypeCode>AES</StatementTypeCode>
          <Pointer>
            <SequenceNumeric>300</SequenceNumeric>
            <DocumentSectionCode>100</DocumentSectionCode>
          </Pointer>
        </AdditionalInformation>
        <Amendment>
          <ChangeReasonCode>1</ChangeReasonCode>
        </Amendment>
      </Declaration>
    </md:MetaData>

  val validSubmissionXml: Elem =
    <MetaData xmlns="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2" xmlns:ns2="urn:wco:datamodel:WCO:Declaration_DS:DMS:2" xmlns:ns3="urn:wco:datamodel:WCO:DEC-DMS:2">
      <WCODataModelVersionCode>3.6</WCODataModelVersionCode>
      <WCOTypeName>DEC</WCOTypeName>
      <ResponsibleCountryCode>GB</ResponsibleCountryCode>
      <ResponsibleAgencyName>HMRC</ResponsibleAgencyName>
      <AgencyAssignedCustomizationCode>v2.1</AgencyAssignedCustomizationCode>
      <ns3:Declaration>
        <ns3:FunctionCode>9</ns3:FunctionCode>
        <ns3:FunctionalReferenceID>1234567890</ns3:FunctionalReferenceID>
        <ns3:TypeCode>EXA</ns3:TypeCode>
        <ns3:GoodsItemQuantity>1</ns3:GoodsItemQuantity>
        <ns3:InvoiceAmount currencyID="GBP">2905</ns3:InvoiceAmount>
        <ns3:TotalPackageQuantity>1</ns3:TotalPackageQuantity>
        <ns3:Agent>
          <ns3:Name>Test</ns3:Name>
          <ns3:FunctionCode>2</ns3:FunctionCode>
          <ns3:Address>
            <ns3:CityName>Shipley</ns3:CityName>
            <ns3:CountryCode>GB</ns3:CountryCode>
            <ns3:Line>10 Downing street</ns3:Line>
            <ns3:PostcodeID>L1 2Ee</ns3:PostcodeID>
          </ns3:Address>
        </ns3:Agent>
        <ns3:AuthorisationHolder>
          <ns3:ID>GB123450</ns3:ID>
          <ns3:CategoryCode>1294</ns3:CategoryCode>
        </ns3:AuthorisationHolder>
        <ns3:BorderTransportMeans>
          <ns3:IdentificationTypeCode>20</ns3:IdentificationTypeCode>
          <ns3:ModeCode>1</ns3:ModeCode>
        </ns3:BorderTransportMeans>
        <ns3:Consignment>
          <ns3:Freight>
            <ns3:PaymentMethodCode>A</ns3:PaymentMethodCode>
          </ns3:Freight>
          <ns3:Itinerary>
            <ns3:SequenceNumeric>0</ns3:SequenceNumeric>
            <ns3:RoutingCountryCode>PL</ns3:RoutingCountryCode>
          </ns3:Itinerary>
        </ns3:Consignment>
        <ns3:CurrencyExchange>
          <ns3:RateNumeric>2905</ns3:RateNumeric>
        </ns3:CurrencyExchange>
        <ns3:Declarant>
          <ns3:Name>Parcel force</ns3:Name>
          <ns3:ID>GB1</ns3:ID>
          <ns3:Address>
            <ns3:CityName>London</ns3:CityName>
            <ns3:CountryCode>GB</ns3:CountryCode>
            <ns3:Line>10 Downing street</ns3:Line>
            <ns3:PostcodeID>L1 1EE</ns3:PostcodeID>
          </ns3:Address>
        </ns3:Declarant>
        <ns3:ExitOffice>
          <ns3:ID>29052018</ns3:ID>
        </ns3:ExitOffice>
        <ns3:Exporter>
          <ns3:Name>test</ns3:Name>
          <ns3:ID>FR1</ns3:ID>
          <ns3:Address>
            <ns3:CityName>test</ns3:CityName>
            <ns3:CountryCode>GB</ns3:CountryCode>
            <ns3:Line>test</ns3:Line>
            <ns3:PostcodeID>L1</ns3:PostcodeID>
          </ns3:Address>
        </ns3:Exporter>
        <ns3:GoodsShipment>
          <ns3:TransactionNatureCode>12</ns3:TransactionNatureCode>
          <ns3:AEOMutualRecognitionParty>
            <ns3:ID>GB1245</ns3:ID>
            <ns3:RoleCode>CS</ns3:RoleCode>
          </ns3:AEOMutualRecognitionParty>
          <ns3:Consignee>
            <ns3:Name>Test</ns3:Name>
            <ns3:ID>GB1234567890</ns3:ID>
            <ns3:Address>
              <ns3:CityName>test</ns3:CityName>
              <ns3:CountryCode>GB</ns3:CountryCode>
              <ns3:Line>test</ns3:Line>
              <ns3:PostcodeID>L1</ns3:PostcodeID>
            </ns3:Address>
          </ns3:Consignee>
          <ns3:Consignment>
            <ns3:ContainerCode>1</ns3:ContainerCode>
            <ns3:ArrivalTransportMeans>
              <ns3:ModeCode>1</ns3:ModeCode>
            </ns3:ArrivalTransportMeans>
            <ns3:DepartureTransportMeans>
              <ns3:IdentificationTypeCode>20</ns3:IdentificationTypeCode>
            </ns3:DepartureTransportMeans>
            <ns3:GoodsLocation>
              <ns3:Name>123</ns3:Name>
              <ns3:ID>1234</ns3:ID>
              <ns3:TypeCode>F</ns3:TypeCode>
              <ns3:Address>
                <ns3:CityName>test</ns3:CityName>
                <ns3:CountryCode>FR</ns3:CountryCode>
                <ns3:PostcodeID>L1</ns3:PostcodeID>
              </ns3:Address>
            </ns3:GoodsLocation>
          </ns3:Consignment>
          <ns3:Destination>
            <ns3:CountryCode>ES</ns3:CountryCode>
          </ns3:Destination>
          <ns3:ExportCountry>
            <ns3:ID>FR</ns3:ID>
          </ns3:ExportCountry>
          <ns3:GovernmentAgencyGoodsItem>
            <ns3:SequenceNumeric>1</ns3:SequenceNumeric>
            <ns3:StatisticalValueAmount currencyID="GBP">32</ns3:StatisticalValueAmount>
            <ns3:AdditionalInformation>
              <ns3:StatementCode>29058</ns3:StatementCode>
              <ns3:StatementDescription>Automation Acceptance Test</ns3:StatementDescription>
            </ns3:AdditionalInformation>
            <ns3:Commodity>
              <ns3:Description>Automation Test for the fields</ns3:Description>
              <ns3:Classification>
                <ns3:ID>12345678</ns3:ID>
                <ns3:IdentificationTypeCode>TSP</ns3:IdentificationTypeCode>
              </ns3:Classification>
              <ns3:Classification>
                <ns3:ID>29052018</ns3:ID>
                <ns3:IdentificationTypeCode>CV</ns3:IdentificationTypeCode>
              </ns3:Classification>
              <ns3:Classification>
                <ns3:ID>2905</ns3:ID>
                <ns3:IdentificationTypeCode>GN</ns3:IdentificationTypeCode>
              </ns3:Classification>
              <ns3:Classification>
                <ns3:ID>9900</ns3:ID>
                <ns3:IdentificationTypeCode>TRA</ns3:IdentificationTypeCode>
              </ns3:Classification>
              <ns3:GoodsMeasure>
                <ns3:GrossMassMeasure unitCode="KGM">5</ns3:GrossMassMeasure>
                <ns3:NetNetWeightMeasure unitCode="KGM">9</ns3:NetNetWeightMeasure>
                <ns3:TariffQuantity unitCode="KGM">2</ns3:TariffQuantity>
              </ns3:GoodsMeasure>
            </ns3:Commodity>
            <ns3:GovernmentProcedure>
              <ns3:CurrentCode>10</ns3:CurrentCode>
              <ns3:PreviousCode>34</ns3:PreviousCode>
            </ns3:GovernmentProcedure>
            <ns3:GovernmentProcedure>
              <ns3:CurrentCode>108</ns3:CurrentCode>
            </ns3:GovernmentProcedure>
            <ns3:Packaging>
              <ns3:SequenceNumeric>0</ns3:SequenceNumeric>
              <ns3:MarksNumbersID>143</ns3:MarksNumbersID>
              <ns3:QuantityQuantity>5</ns3:QuantityQuantity>
              <ns3:TypeCode>PA</ns3:TypeCode>
            </ns3:Packaging>
          </ns3:GovernmentAgencyGoodsItem>
          <ns3:PreviousDocument>
            <ns3:CategoryCode>X</ns3:CategoryCode>
            <ns3:ID>1</ns3:ID>
            <ns3:TypeCode>2</ns3:TypeCode>
            <ns3:LineNumeric>1</ns3:LineNumeric>
          </ns3:PreviousDocument>
          <ns3:UCR>
            <ns3:TraderAssignedReferenceID>1GB123456789000-123ABC456DEFHARRY</ns3:TraderAssignedReferenceID>
          </ns3:UCR>
          <ns3:Warehouse>
            <ns3:ID>BC</ns3:ID>
            <ns3:TypeCode>A</ns3:TypeCode>
          </ns3:Warehouse>
        </ns3:GoodsShipment>
        <ns3:PresentationOffice>
          <ns3:ID>20180529</ns3:ID>
        </ns3:PresentationOffice>
        <ns3:SupervisingOffice>
          <ns3:ID>12345678</ns3:ID>
        </ns3:SupervisingOffice>
      </ns3:Declaration>
    </MetaData>

}
