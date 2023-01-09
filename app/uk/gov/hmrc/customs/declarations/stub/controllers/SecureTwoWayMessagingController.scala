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

package uk.gov.hmrc.customs.declarations.stub.controllers

import play.api.mvc.{Action, AnyContent, ControllerComponents, Cookie, Request}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class SecureTwoWayMessagingController @Inject() (cc: ControllerComponents) extends BackendController(cc) {
  import SecureTwoWayMessagingController._

  private def langCookieValue(implicit request: Request[AnyContent]) = request.cookies.get("PLAY_LANG").getOrElse(Cookie("", "")).value

  def messages(): Action[AnyContent] = Action { implicit request =>
    Ok(messagesResponse(langCookieValue))
  }

  def conversation(client: String, conversationId: String): Action[AnyContent] = Action { implicit request =>
    Ok(conversationResponse(langCookieValue))
  }

  def reply(client: String, conversationId: String): Action[AnyContent] = Action { _ =>
    Ok("")
  }

  def replyResult(client: String, conversationId: String): Action[AnyContent] = Action { implicit request =>
    Ok(replySubmissionResult(langCookieValue))
  }
}

object SecureTwoWayMessagingController {
  def messagesResponse(language: String) = {
    val lang = if (language == "cy") language else ""
    s"""<style>
      |@media screen and (max-width: 414px) {
      |  .mob-align-right {
      |    text-align: right
      |  }
      |}
      |@media (forced-colors: active) {
      |  .prefs__palette {
      |    forced-color-adjust: none;
      |    background-color: #ff0 !important
      |  }
      |}
      |.conversation-status-marker {
      |  display:inline-block;
      |  background-color:#1d70b8;
      |}
      |.black-text {
      |  color:#0b0c0c;
      |}
      |.dot-unread {
      |  height: 10px;
      |  width: 10px;
      |  background-color: #1d70b8;
      |  border-radius: 50%;
      |  display: inline-block;
      |  overflow: hidden;
      |  text-indent: -99999px;
      |}
      |.underline {
      |  text-decoration: underline !important;
      |}
      |.no-underline {
      |  text-decoration: none !important;
      |}
      |.message-counter {
      |  font-size:90%;
      |  color:#6F777B;
      |  padding-left:1ex;
      |}
      |.non-breaking {
      |    font-size: 90%;
      |    color: #6F777B;
      |}
      |.break-long {
      |  word-break: break-word;
      |}
      |.grid-header-row {
      |  border-bottom:1px solid rgba(177, 180, 182,0.75);
      |  padding:10px;
      |}
      |.message {margin-bottom: 10px; }
      |.message-row:hover { background-color:#f3f2f1; }
      |.message-row a:focus {  background-color: #f3f2f1;}
      |.message-row:focus-within {  background-color:#f3f2f1;}
      |.message-row a:focus {
      |    outline: 0 !important;
      |}
      |a.underline-fix { text-decoration: none; }
      |.conversation-status { text-align:center; }
      |.status-align {
      |  text-align: right;
      |}
      |</style>
      |
      |<h1 class="govuk-heading-xl">${lang}Messages between you and HMRC</h1>
      |<div class="govuk-body">
      |<table class="govuk-table">
      |  <caption class="govuk-table__caption govuk-table__caption--m">
      |    <span class="govuk-visually-hidden">0 unread, 0 in total. Each message in the list includes its status (either unread or previously viewed), and its sender name, subject, and send time or date. If a message includes replies, then its subject says the number of messages in that conversation.</span>
      |  </caption>
      |  <thead class="govuk-table__head">
      |  <tr class="govuk-table__row">
      |      <th scope="col" class="govuk-table__header"><span class="govuk-visually-hidden">Status</span></th>
      |      <th scope="col" class="govuk-table__header">Message</th>
      |      <th scope="col" class="govuk-table__header mob-align-right">Date</th>
      |  </tr>
      |  </thead>
      |  <tbody class="govuk-table__body">
      |  <tr class="govuk-table__row message-row" >
      |
      |  <!-- status -->
      |  <td class="govuk-table__cell status-align">
      |      <span class="govuk-visually-hidden">Previously viewed message.&nbsp;</span>
      |  </td>
      |  <td class="govuk-table__cell ">
      |    <a href=/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzYyN2UzNTM3M2Q0ZDY3MDQ4NWI5YTg0Yg== target="_self" data-sso="false" class="underline-fix no--underline" >
      |      <div><span class="govuk-visually-hidden">from:</span><span class="black-text break-long ">You<span class="govuk-visually-hidden">.&nbsp;</span></span></div>
      |      <div>
      |        <span><span class="govuk-visually-hidden">Subject:</span><span class="underline black-text break-long ">MRN 22GB597RCND4DHZAA0 case number D-155118</span><span class="govuk-visually-hidden">,&nbsp;</span><span aria-hidden="true" class="message-counter non-breaking">(3)</span><span class="govuk-visually-hidden">3
      |              messages in this conversation.</span></span>
      |      </div>
      |    </a>
      | </td>
      | <td class="govuk-table__cell mob-align-right">
      |  <a aria-hidden="true" href=/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzYyN2UzNTM3M2Q0ZDY3MDQ4NWI5YTg0Yg== target="_self" data-sso="false" class="underline-fix no--underline" >
      |    <span class=' black-text'>7 July 2022</span>
      |  </a>
      |  <span class="govuk-visually-hidden">7 July 2022</span>
      | </td>
      |</tr>
      |</tbody>
      |</table>
      |</div>""".stripMargin
  }

  def conversationResponse(language: String) = {
    val lang = if (language == "cy") language else ""
    s"""
       |<style>
       |@media screen and (max-width: 414px) {
       | .custom-caption {
       |    font-size: 14px !important;
       | }
       |}
       |.govuk-heading-l {
       |     margin-bottom: -15px;
       |}
       |.custom-caption {
       |    font-size: 16px;
       |    color: #505a5f;
       |    display: block;
       |    padding-top: 2px;
       |}
       |.break-long  {
       |    word-break: break-word
       |}
       |</style>
       |<div class="govuk-body-l">
       |    <h1 class="govuk-heading-l margin-top-small margin-bottom-small">${lang}MRN 22GB597RCND4DHZAA0 case number D-155118</h1>
       |    <p>
       |    <span aria-hidden="true" class="custom-caption">
       |        You sent this on 7 July 2022 at 11:40am
       |    </span>
       |    <span class="govuk-visually-hidden">
       |        You sent this on 7 July 2022 at 11:40am
       |    </span>
       |    </p>
       |    <div class="govuk-body break-long">
       |       <p>asdd</p>
       |    </div>
       |    <style>
       |    .govuk-character-count {
       |        margin-bottom: 5px;
       |    }
       |    span#reply-form-error + textarea {
       |        border: 2px solid #d4351c;
       |    }
       |    </style>
       |    <hr aria-hidden="true" class="govuk-section-break govuk-section-break--m govuk-section-break--visible"/>
       |    <form method="POST" novalidate action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzYyN2UzNTM3M2Q0ZDY3MDQ4NWI5YTg0Yg==">
       |    <input type="hidden" name="csrfToken" value="a6e5b763d625a6bb3b843cd78306011933f42b62-1664793963148-22b95ba204237e1116b65d7c"/>
       |    <div class="govuk-character-count" data-module="govuk-character-count"
       |       data-maxlength="4000"
       |       data-threshold="75">
       |        <div class="govuk-form-group">
       |          <label class="govuk-label govuk-label--s " for="reply-form">
       |            Reply to this message
       |          </label>
       |          <textarea class="govuk-textarea govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info" spellcheck="true"></textarea>
       |        </div>
       |        <div id="reply-form-info" class="govuk-hint govuk-character-count__message" aria-live="polite">
       |          You can enter up to 4000 characters
       |        </div>
       |    </div>
       |    <button  class="govuk-button" data-module="govuk-button">
       |        Send
       |    </button>
       |    </form>
       |    <hr class="govuk-section-break govuk-section-break--l govuk-section-break--visible">
       |    <p>
       |    <span aria-hidden="true" class="custom-caption">
       |        You sent this on 16 May 2022 at 2:13pm
       |    </span>
       |    <span class="govuk-visually-hidden">
       |        You sent this on 16 May 2022 at 2:13pm
       |    </span>
       |    </p>
       |    <div class="govuk-body break-long">
       |        <p>dfgdfgdfgd</p>
       |    </div>
       |    <hr class="govuk-section-break govuk-section-break--l govuk-section-break--visible">
       |    <p>
       |    <span aria-hidden="true" class="custom-caption">
       |        Border Force sent this on 13 May 2022 at 11:38am
       |    </span>
       |    <span class="govuk-visually-hidden">
       |        Border Force sent this on 13 May 2022 at 11:38am
       |    </span>
       |    <span aria-hidden="true" class="custom-caption">
       |            First read on 16 May 2022 at 11:46am
       |    </span>
       |    <span class="govuk-visually-hidden">First viewed on 16 May 2022 at 11:46am</span>
       |    </p>
       |    <div class="govuk-body break-long">
       |        Testing CDSP_9819 TC05
       |    </div>
       |</div>""".stripMargin
  }

  def replySubmissionResult(language: String) = {
    val lang = if (language == "cy") language else ""
    s"""
       |<style>
       |   .govuk-panel--confirmation > * {
       |        word-break: break-word;
       |      }
       | </style>
       |
       |<div class="govuk-panel govuk-panel--confirmation">
       | <h1 class="govuk-panel__title">
       |   ${lang}Message sent
       | </h1>
       |   <div class="govuk-panel__body">
       |     We received your message
       |   </div>
       |</div>
       |
       |<h2 class="govuk-heading-m">What happens next</h2>
       |<p class="govuk-body">You do not need to do anything now.</p>
       |<p class="govuk-body">We will contact you if we need more information.</p>
       |<form action=/cds-file-upload-service/messages>
       | <input type="hidden" name="sent" value="true" />
       | <button class="govuk-button" data-module="govuk-button">Back to your messages</button>
       |</form>
       |""".stripMargin
  }
}
