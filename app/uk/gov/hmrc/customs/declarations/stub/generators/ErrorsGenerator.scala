/*
 * Copyright 2024 HM Revenue & Customs
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

import uk.gov.hmrc.customs.declarations.stub.generators.ErrorsForAcceptanceTests.{errorsSet1, errorsSet2}

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
        <_2_1:SequenceNumeric>2</_2_1:SequenceNumeric>
        <_2_1:DocumentSectionCode>70A</_2_1:DocumentSectionCode>
        <_2_1:TagID>166</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val CDS12075_1 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12075</_2_1:ValidationCode>
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
      <_2_1:TagID>D006</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS12075</_2_1:ValidationCode>
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
        <_2_1:TagID>D031</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val CDS12075_2 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12075</_2_1:ValidationCode>
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
      <_2_1:ValidationCode>CDS12075</_2_1:ValidationCode>
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

  val CDS12077_1 = <_2_1:Error>
      <_2_1:ValidationCode>CDS12077</_2_1:ValidationCode>
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
        <_2_1:DocumentSectionCode>70A</_2_1:DocumentSectionCode>
        <_2_1:TagID>166</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>
  val CDS12077_2 = <_2_1:Error>
      <_2_1:ValidationCode>CDS12077</_2_1:ValidationCode>
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
        <_2_1:TagID>D006</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>
  val CDS12077_3 = <_2_1:Error>
      <_2_1:ValidationCode>CDS12077</_2_1:ValidationCode>
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
        <_2_1:TagID>D031</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val CDS12120 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12120</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>06A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
  </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS12120</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>17C</_2_1:DocumentSectionCode>
        <_2_1:TagID>R145</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val CDS10010 = <_2_1:Error>
    <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
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
      <_2_1:DocumentSectionCode>23A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:SequenceNumeric>2</_2_1:SequenceNumeric>
      <_2_1:DocumentSectionCode>21A</_2_1:DocumentSectionCode>
      <_2_1:TagID>145</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>

  val CDS12070 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12070</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>28A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>81A</_2_1:DocumentSectionCode>
      <_2_1:TagID>064</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>

  val CDS12046: NodeSeq = <_2_1:Error>
    <_2_1:ValidationCode>CDS12046</_2_1:ValidationCode>
  </_2_1:Error>

  // Non-existant error codes used to test specific pointer combinations
  val CDS90001 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12120</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>97B</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
  </_2_1:Error>

  val CDS90002 = <_2_1:Error>
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
      <_2_1:DocumentSectionCode>70A</_2_1:DocumentSectionCode>
      <_2_1:TagID>166</_2_1:TagID>
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
        <_2_1:DocumentSectionCode>70A</_2_1:DocumentSectionCode>
        <_2_1:TagID>161</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val CDS90003 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12077</_2_1:ValidationCode>
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
      <_2_1:DocumentSectionCode>03A</_2_1:DocumentSectionCode>
      <_2_1:TagID>226</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>

  // a list of all the suppressed pointers (ones that we can not directly link to a field in our model)
  val CDS90004 = <_2_1:Error>
    <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
      <_2_1:DocumentSectionCode>06A</_2_1:DocumentSectionCode>
      <_2_1:TagID>97A</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
    </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>06A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
    </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>D014</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
    </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
        <_2_1:DocumentSectionCode>03A</_2_1:DocumentSectionCode>
        <_2_1:TagID>226</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>48A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>68A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>02A</_2_1:DocumentSectionCode>
        <_2_1:TagID>D031</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS10010</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>48A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>68A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>02A</_2_1:DocumentSectionCode>
        <_2_1:TagID>D006</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val CDS10001 = <_2_1:Error>
    <_2_1:ValidationCode>CDS10001</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      <_2_1:TagID>146</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
  <_2_1:Error>
    <_2_1:ValidationCode>CDS12056</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      <_2_1:TagID>05A</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
  <_2_1:Error>
    <_2_1:ValidationCode>CDS10001</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      <_2_1:TagID>57A</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
  <_2_1:Error>
    <_2_1:ValidationCode>CDS12070</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>28A</_2_1:DocumentSectionCode>
      <_2_1:TagID>46A</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
  <_2_1:Error>
    <_2_1:ValidationCode>CDS12077</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>28A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>18A</_2_1:DocumentSectionCode>
      <_2_1:TagID>04A</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
  <_2_1:Error>
    <_2_1:ValidationCode>CDS10001</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
      <_2_1:DocumentSectionCode>68A</_2_1:DocumentSectionCode>
      <_2_1:TagID>93A</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
  <_2_1:Error>
    <_2_1:ValidationCode>CDS70761</_2_1:ValidationCode>
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
    </_2_1:Pointer>
  </_2_1:Error>
  <_2_1:Error>
    <_2_1:ValidationCode>CDS12022</_2_1:ValidationCode>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
    </_2_1:Pointer>
    <_2_1:Pointer>
      <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
      <_2_1:DocumentSectionCode>68A</_2_1:DocumentSectionCode>
      <_2_1:TagID>006</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>

  val CDS12171 = <_2_1:Error>
    <_2_1:ValidationCode>CDS12171</_2_1:ValidationCode>
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
      <_2_1:DocumentSectionCode>70A</_2_1:DocumentSectionCode>
      <_2_1:TagID>166</_2_1:TagID>
    </_2_1:Pointer>
  </_2_1:Error>
    <_2_1:Error>
      <_2_1:ValidationCode>CDS12171</_2_1:ValidationCode>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
      </_2_1:Pointer>
      <_2_1:Pointer>
        <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
        <_2_1:DocumentSectionCode>68A</_2_1:DocumentSectionCode>
        <_2_1:TagID>114</_2_1:TagID>
      </_2_1:Pointer>
    </_2_1:Error>

  val errors: Map[String, NodeSeq] = Map(
    "CDS10010" -> CDS10010,
    "CDS10001" -> CDS10001,
    "CDSSEALS" -> CDSSEALS,
    "CDS10020" -> CDS10020,
    "CDS12046" -> CDS12046,
    "CDS12056" -> CDS12056,
    "CDS12062" -> CDS12062,
    "CDS12070" -> CDS12070,
    "CDS12075" -> CDS12075_1,
    "CDSADDOC" -> CDS12075_2,
    "CDS12077" -> (CDS12077_1 ++ CDS12077_2 ++ CDS12077_3),
    "CDS12119" -> CDS12119,
    "CDS12120" -> CDS12120,
    "CDS90001" -> CDS90001,
    "CDS90002" -> CDS90002,
    "CDS90003" -> CDS90003,
    "CDS90004" -> CDS90004,
    "CDSCOM01" -> (CDS10020 ++ CDS12056 ++ CDS12062 ++ CDS12119),
    "CDSCOM02" -> errorsSet1,
    "CDSCOM03" -> errorsSet2,
    "CDS12171" -> CDS12171
  )
}
