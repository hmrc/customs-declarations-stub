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

import scala.xml.NodeSeq

object ErrorsGenerator {

  val CDS10020: NodeSeq = <_2_1:Error>
    <_2_1:Description>Weight appears too high per item</_2_1:Description>
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

  val CDSSEALS: NodeSeq = <_2_1:Error>
    <_2_1:Description>Invalid seal</_2_1:Description>
    <_2_1:ValidationCode>CDS10020</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>28A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
      <_2_1:DocumentSectionCode>31B</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
      <_2_1:DocumentSectionCode>44B</_2_1:DocumentSectionCode>
      <_2_1:TagID>165</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>

  val CDS12056: NodeSeq = <_2_1:Error>
    <_2_1:ValidationCode>CDS12056</_2_1:ValidationCode>
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
      <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
      <_2_1:DocumentSectionCode>02A</_2_1:DocumentSectionCode>
      <_2_1:TagID>D006</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS12056</_2_1:ValidationCode>
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
        <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
        <_2_1:DocumentSectionCode>02A</_2_1:DocumentSectionCode>
        <_2_1:TagID>D031</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS12056</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
        <_2_1:TagID>D013</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val CDS12062 = <_2_1:Error>
        <_2_1:ValidationCode>CDS12062</_2_1:ValidationCode>
      </_2_1:Error>

  val CDS12119 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12119</_2_1:ValidationCode>
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
      <_2_1:DocumentSectionCode>02A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
  </_2_1:Error>

  val errors: Map[String, NodeSeq] = Map(
    "CDSSEALS" -> CDSSEALS,
    "CDS10020" -> CDS10020,
    "CDS12056" -> CDS12056,
    "CDS12062" -> CDS12062,
    "CDS12119" -> CDS12119,
    "CDSCOM01" -> (CDS10020 ++ CDS12056 ++ CDS12062 ++ CDS12119)
  )
}
