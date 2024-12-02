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

package uk.gov.hmrc.customs.declarations.stub.controllers

import play.api.mvc.{Action, AnyContent, AnyContentAsFormUrlEncoded, ControllerComponents, Cookie, Request}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class SecureTwoWayMessagingController @Inject() (cc: ControllerComponents) extends BackendController(cc) {
  import SecureTwoWayMessagingController._

  private def langCookieValue(implicit request: Request[AnyContent]) = request.cookies.get("PLAY_LANG").getOrElse(Cookie("", "")).value

  val messages: Action[AnyContent] = Action { implicit request =>
    Ok(messagesResponse(langCookieValue))
  }

  def conversation(client: String, conversationId: String): Action[AnyContent] = Action { implicit request =>
    Ok(conversation200Response(langCookieValue))
  }

  def reply(client: String, conversationId: String): Action[AnyContent] = Action { implicit request =>
    val formUrlEncodedBody = request.body.asInstanceOf[AnyContentAsFormUrlEncoded]
    if (formUrlEncodedBody.data.get("content").flatMap(_.headOption).map(_.isBlank).getOrElse(true))
      BadRequest(conversation400Response(langCookieValue))
    else
      Ok("")
  }

  def replyResult(client: String, conversationId: String): Action[AnyContent] = Action { implicit request =>
    Ok(replySubmissionResult(langCookieValue))
  }
}

// scalastyle:off
object SecureTwoWayMessagingController {
  def messagesResponse(language: String) = {
    if (language == "cy") {
      """<style>
        |   @media screen and (max-width: 414px) {
        |   .mob-align-right {
        |   text-align: right
        |   }
        |   }
        |   @media (forced-colors: active) {
        |   .prefs__palette {
        |   forced-color-adjust: none;
        |   background-color: #ff0 !important
        |   }
        |   }
        |   .govuk-table__cell {
        |   padding: 10px 20px 10px 0;
        |   }
        |   .govuk-table__cell:last-child, .govuk-table__header:last-child {
        |   padding-right: 0;
        |   }
        |   .conversation-status-marker {
        |   display:inline-block;
        |   background-color:#1d70b8;
        |   }
        |   .black-text {
        |   color:#0b0c0c;
        |   }
        |   .dot-unread {
        |   height: 10px;
        |   width: 10px;
        |   background-color: #1d70b8;
        |   border-radius: 50%;
        |   display: inline-block;
        |   overflow: hidden;
        |   text-indent: -99999px;
        |   }
        |   .underline {
        |   text-decoration: underline !important;
        |   }
        |   .no-underline {
        |   text-decoration: none !important;
        |   }
        |   .message-counter {
        |   font-size:90%;
        |   color:#6F777B;
        |   padding-left:1ex;
        |   }
        |   .non-breaking {
        |   font-size: 90%;
        |   color: #6F777B;
        |   }
        |   .grid-header-row {
        |   border-bottom:1px solid rgba(177, 180, 182,0.75);
        |   padding:10px;
        |   }
        |   .black-text {
        |   color: #0b0c0c;
        |   }
        |   .black-text:hover {
        |   color: #003078;
        |   border-bottom-color: #003078;
        |   }
        |   a.underline-fix { text-decoration: none; }
        |   .conversation-status { text-align:center; }
        |   .status-align {
        |   text-align: right;
        |   }
        |   .hover-effect {
        |   color: #0b0c0c;
        |   }
        |   .hover-effect:hover {
        |   color: #003078;
        |   }
        |   .default-color{
        |   color:  #0b0c0c;
        |   }
        |</style>
        |<h1 class="govuk-heading-xl">Negeseuon rhyngoch chi a CThEM</h1>
        |<div class="govuk-body">
        |   <table class="govuk-table">
        |      <caption class="govuk-table__caption govuk-table__caption--m">
        |         <span class="govuk-visually-hidden">0 o negeseuon heb eu darllen, 19 mewn cyfanswm. Mae pob neges yn y rhestr yn cynnwys ei statws (naill ai heb ei darllen neu gwelwyd eisoes), ac enw’r anfonwr, y pwnc, a’r amser neu ddyddiad anfon. Os yw neges yn cynnwys atebion, yna mae ei phwnc yn nodi nifer y negeseuon yn y sgwrs honno.</span>
        |      </caption>
        |      <thead class="govuk-table__head">
        |         <tr class="govuk-table__row">
        |            <th scope="col" class="govuk-table__header"><span class="govuk-visually-hidden">Status</span></th>
        |            <th scope="col" class="govuk-table__header">Neges</th>
        |            <th scope="col" class="govuk-table__header mob-align-right">Dyddiad</th>
        |         </tr>
        |      </thead>
        |      <tbody class="govuk-table__body">
        |         <tr class="govuk-table__row message-row">
        |            <!-- status -->
        |            <td class="govuk-table__cell status-align">
        |               <span class="govuk-visually-hidden">Neges a welwyd eisoes.&nbsp;</span>
        |            </td>
        |            <td class="govuk-table__cell">
        |               <a href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY0NjRhMWNlMDliN2Q4MWMxNTYwNzBiMA==" class="govuk-!-font-weight-bold hover-effect" style="font-weight:400; text-decoration: none;" data-sso="false">
        |               <span class="govuk-visually-hidden">oddi wrth:</span><span style="font-weight:400; text-decoration: none;">HMRC Imports(CDS) needs actions yet<span class="govuk-visually-hidden">.&nbsp;</span></span>
        |               </a>
        |               <br>
        |               <div class="govuk-task-list__name-and-hint black-text">
        |                  <a class="govuk-link" href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY0NjRhMWNlMDliN2Q4MWMxNTYwNzBiMA==" aria-describedby="company-details-1-status" data-sso="false">
        |                  <span class="govuk-visually-hidden">Pwnc:</span><span class="black-text no--underline">about import doc test</span><span class="govuk-visually-hidden">,&nbsp;</span>
        |                  </a>
        |               </div>
        |            </td>
        |            <td class="govuk-table__cell mob-align-right">
        |               <span class="">17 Mai 2023</span>
        |               <span class="govuk-visually-hidden">17 Mai 2023</span>
        |            </td>
        |         </tr>
        |      </tbody>
        |   </table>
        |</div>
        |""".stripMargin
    } else {
      """<style>
         |      @media screen and (max-width: 414px) {
         |      .mob-align-right {
         |      text-align: right
         |      }
         |      }
         |      @media (forced-colors: active) {
         |      .prefs__palette {
         |      forced-color-adjust: none;
         |      background-color: #ff0 !important
         |      }
         |      }
         |      .govuk-table__cell {
         |      padding: 10px 20px 10px 0;
         |      }
         |      .govuk-table__cell:last-child, .govuk-table__header:last-child {
         |      padding-right: 0;
         |      }
         |      .conversation-status-marker {
         |      display:inline-block;
         |      background-color:#1d70b8;
         |      }
         |      .black-text {
         |      color:#0b0c0c;
         |      }
         |      .dot-unread {
         |      height: 10px;
         |      width: 10px;
         |      background-color: #1d70b8;
         |      border-radius: 50%;
         |      display: inline-block;
         |      overflow: hidden;
         |      text-indent: -99999px;
         |      }
         |      .underline {
         |      text-decoration: underline !important;
         |      }
         |      .no-underline {
         |      text-decoration: none !important;
         |      }
         |      .message-counter {
         |      font-size:90%;
         |      color:#6F777B;
         |      padding-left:1ex;
         |      }
         |      .non-breaking {
         |      font-size: 90%;
         |      color: #6F777B;
         |      }
         |      .grid-header-row {
         |      border-bottom:1px solid rgba(177, 180, 182,0.75);
         |      padding:10px;
         |      }
         |      .black-text {
         |      color: #0b0c0c;
         |      }
         |      .black-text:hover {
         |      color: #003078;
         |      border-bottom-color: #003078;
         |      }
         |      a.underline-fix { text-decoration: none; }
         |      .conversation-status { text-align:center; }
         |      .status-align {
         |      text-align: right;
         |      }
         |      .hover-effect {
         |      color: #0b0c0c;
         |      }
         |      .hover-effect:hover {
         |      color: #003078;
         |      }
         |      .default-color{
         |      color:  #0b0c0c;
         |      }
         |   </style>
         |   <h1 class="govuk-heading-xl">Messages between you and HMRC</h1>
         |   <div class="govuk-body">
         |      <table class="govuk-table">
         |         <caption class="govuk-table__caption govuk-table__caption--m">
         |            <span class="govuk-visually-hidden">0 unread, 19 in total. Each message in the list includes its status (either unread or previously viewed), and its sender name, subject, and send time or date. If a message includes replies, then its subject says the number of messages in that conversation.</span>
         |         </caption>
         |         <thead class="govuk-table__head">
         |            <tr class="govuk-table__row">
         |               <th scope="col" class="govuk-table__header"><span class="govuk-visually-hidden">Status</span></th>
         |               <th scope="col" class="govuk-table__header">Message</th>
         |               <th scope="col" class="govuk-table__header mob-align-right">Date</th>
         |            </tr>
         |         </thead>
         |         <tbody class="govuk-table__body">
         |            <tr class="govuk-table__row message-row">
         |               <!-- status -->
         |               <td class="govuk-table__cell status-align">
         |                  <span class="govuk-visually-hidden">Previously viewed message.&nbsp;</span>
         |               </td>
         |               <td class="govuk-table__cell">
         |                  <a href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY0NjRhMWNlMDliN2Q4MWMxNTYwNzBiMA==" class="govuk-!-font-weight-bold hover-effect" style="font-weight:400; text-decoration: none;" data-sso="false">
         |                  <span class="govuk-visually-hidden">from:</span><span style="font-weight:400; text-decoration: none;">HMRC Imports(CDS) needs actions yet<span class="govuk-visually-hidden">.&nbsp;</span></span>
         |                  </a>
         |                  <br>
         |                  <div class="govuk-task-list__name-and-hint black-text">
         |                     <a class="govuk-link" href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY0NjRhMWNlMDliN2Q4MWMxNTYwNzBiMA==" aria-describedby="company-details-1-status" data-sso="false">
         |                     <span class="govuk-visually-hidden">Subject:</span><span class="black-text no--underline">about import doc test</span><span class="govuk-visually-hidden">,&nbsp;</span>
         |                     </a>
         |                  </div>
         |               </td>
         |               <td class="govuk-table__cell mob-align-right">
         |                  <span class="">17 May 2023</span>
         |                  <span class="govuk-visually-hidden">17 May 2023</span>
         |               </td>
         |            </tr>
         |         </tbody>
         |      </table>
         |   </div>
         |""".stripMargin
    }
  }

  def conversation200Response(language: String) = {
    if (language == "cy") {
      """<style>
        |   @media screen and (max-width: 414px) {
        |   .custom-caption {
        |   font-size: 14px !important;
        |   }
        |   }
        |   .govuk-heading-l {
        |   margin-bottom: -15px;
        |   }
        |   .custom-caption {
        |   font-size: 16px;
        |   color: #505a5f;
        |   display: block;
        |   padding-top: 2px;
        |   }
        |   .break-long  {
        |   word-break: break-word
        |   }
        |</style>
        |<div class="govuk-body-l">
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">about import doc test</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      HMRC Imports(CDS) needs actions wnaeth anfon y neges hon ar 17 Mai 2023 am 10:43yb
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      HMRC Imports(CDS) needs actions wnaeth anfon y neges hon ar 17 Mai 2023 am 10:43yb
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      Darllenwyd am y tro cyntaf ar 10 Gorffennaf 2023 am 1:13yh
        |      </span>
        |      <span class="govuk-visually-hidden">Gwelwyd am y tro cyntaf ar 10 Gorffennaf 2023 am 1:13yh</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      {"regime":"ztZAlnDY2qUTNFeBD6m3MQ==","templateId":"udALJyIbKapI/7d2Hhh9XOm2nRKxrP1ZQqGXLASQL8o=","platform":"4PRiqswj7rqtjhES3PWcdg==","ContactPolicyGroupId":"yk7_hM8eQQeFwA3zHyfRgg"}
        |   </div>
        |   <style>
        |      .govuk-character-count {
        |      margin-bottom: 5px;
        |      }
        |      span#reply-form-error + textarea {
        |      border: 2px solid #d4351c;
        |      }
        |   </style>
        |   <hr aria-hidden="true" class="govuk-section-break govuk-section-break--m govuk-section-break--visible">
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY0NjRhMWNlMDliN2Q4MWMxNTYwNzBiMA==">
        |      <input type="hidden" name="csrfToken" value="b341e549f7ac51bd4f72b671c71733b682649b56-1733154314554-7e9211d2eb67745dd698abf7">
        |      <div class="govuk-form-group govuk-character-count" data-i18n.characters-under-limit.one="Mae gennych 1 cymeriad yn weddill" data-module="govuk-character-count" data-i18n.words-over-limit.one="Mae gennych 1 gair yn ormod" data-i18n.words-at-limit="Mae gennych 0 o eiriau yn weddill" data-threshold="75" data-maxlength="4000" data-i18n.characters-over-limit.other="Mae gennych %{count} o gymeriadau yn ormod" data-i18n.characters-at-limit="Mae gennych 0 o gymeriadau yn weddill" data-i18n.words-over-limit.other="Mae gennych %{count} o eiriau yn ormod" data-i18n.words-under-limit.other="Mae gennych %{count} o eiriau yn weddill" data-i18n.words-under-limit.one="Mae gennych 1 gair yn weddill" data-i18n.characters-over-limit.one="Mae gennych 1 cymeriad yn ormod" data-i18n.characters-under-limit.other="Mae gennych %{count} o gymeriadau yn weddill">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Ateb y neges hon
        |         </label>
        |         <textarea class="govuk-textarea govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message govuk-visually-hidden">
        |            Gallwch nodi hyd at 4000 o gymeriadau
        |         </div>
        |         <div class="govuk-hint govuk-character-count__message govuk-character-count__status govuk-character-count__message--disabled" aria-hidden="true">Mae gennych 4,000 o gymeriadau yn weddill</div>
        |         <div class="govuk-character-count__sr-status govuk-visually-hidden" aria-live="polite" aria-hidden="true">Mae gennych 4,000 o gymeriadau yn weddill</div>
        |      </div>
        |      <button type="submit" class="govuk-button" data-module="govuk-button">
        |      Anfon
        |      </button>
        |   </form>
        |</div>
        |""".stripMargin
    } else {
      """<style>
        |   @media screen and (max-width: 414px) {
        |   .custom-caption {
        |   font-size: 14px !important;
        |   }
        |   }
        |   .govuk-heading-l {
        |   margin-bottom: -15px;
        |   }
        |   .custom-caption {
        |   font-size: 16px;
        |   color: #505a5f;
        |   display: block;
        |   padding-top: 2px;
        |   }
        |   .break-long  {
        |   word-break: break-word
        |   }
        |</style>
        |<div class="govuk-body-l">
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">about import doc test</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      HMRC Imports(CDS) needs actions sent this on 17 May 2023 at 10:43am
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      HMRC Imports(CDS) needs actions sent this on 17 May 2023 at 10:43am
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      First read on 10 July 2023 at 1:13pm
        |      </span>
        |      <span class="govuk-visually-hidden">First viewed on 10 July 2023 at 1:13pm</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      {"regime":"ztZAlnDY2qUTNFeBD6m3MQ==","templateId":"udALJyIbKapI/7d2Hhh9XOm2nRKxrP1ZQqGXLASQL8o=","platform":"4PRiqswj7rqtjhES3PWcdg==","ContactPolicyGroupId":"yk7_hM8eQQeFwA3zHyfRgg"}
        |   </div>
        |   <style>
        |      .govuk-character-count {
        |      margin-bottom: 5px;
        |      }
        |      span#reply-form-error + textarea {
        |      border: 2px solid #d4351c;
        |      }
        |   </style>
        |   <hr aria-hidden="true" class="govuk-section-break govuk-section-break--m govuk-section-break--visible">
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY0NjRhMWNlMDliN2Q4MWMxNTYwNzBiMA==">
        |      <input type="hidden" name="csrfToken" value="fcf89627456633bd9814b94205b5f42a30874f6f-1733154227460-7e9211d2eb67745dd698abf7">
        |      <div class="govuk-form-group govuk-character-count" data-module="govuk-character-count" data-threshold="75" data-maxlength="4000">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Reply to this message
        |         </label>
        |         <textarea class="govuk-textarea govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message govuk-visually-hidden">
        |            You can enter up to 4000 characters
        |         </div>
        |         <div class="govuk-hint govuk-character-count__message govuk-character-count__status govuk-character-count__message--disabled" aria-hidden="true">You have 4,000 characters remaining</div>
        |         <div class="govuk-character-count__sr-status govuk-visually-hidden" aria-live="polite" aria-hidden="true">You have 4,000 characters remaining</div>
        |      </div>
        |      <button type="submit" class="govuk-button" data-module="govuk-button">
        |      Send
        |      </button>
        |   </form>
        |</div>
        |""".stripMargin
    }
  }

  def conversation400Response(language: String) = {
    if (language == "cy") {
      """<style>
        |   @media screen and (max-width: 414px) {
        |   .custom-caption {
        |   font-size: 14px !important;
        |   }
        |   }
        |   .govuk-heading-l {
        |   margin-bottom: -15px;
        |   }
        |   .custom-caption {
        |   font-size: 16px;
        |   color: #505a5f;
        |   display: block;
        |   padding-top: 2px;
        |   }
        |   .break-long  {
        |   word-break: break-word
        |   }
        |</style>
        |<div class="govuk-body-l">
        |   <div class="govuk-error-summary" data-module="govuk-error-summary">
        |      <div role="alert">
        |         <h2 class="govuk-error-summary__title">Mae problem wedi codi</h2>
        |         <div class="govuk-error-summary__body">
        |            <ul class="govuk-list govuk-error-summary__list">
        |               <li>
        |                  <a href="#reply-form">Mae’n rhaid i chi ysgrifennu neges er mwyn ateb</a>
        |               </li>
        |            </ul>
        |         </div>
        |      </div>
        |   </div>
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">about import doc</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      HMRC Imports(CDS) needs actions wnaeth anfon y neges hon ar 5 Gorffennaf 2022 am 5:33yh
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      HMRC Imports(CDS) needs actions wnaeth anfon y neges hon ar 5 Gorffennaf 2022 am 5:33yh
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      Darllenwyd am y tro cyntaf ar 28 Medi 2022 am 12:26yh
        |      </span>
        |      <span class="govuk-visually-hidden">Gwelwyd am y tro cyntaf ar 28 Medi 2022 am 12:26yh</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      This is a test message
        |   </div>
        |   <style>
        |      .govuk-character-count {
        |      margin-bottom: 5px;
        |      }
        |      span#reply-form-error + textarea {
        |      border: 2px solid #d4351c;
        |      }
        |   </style>
        |   <hr aria-hidden="true" class="govuk-section-break govuk-section-break--m govuk-section-break--visible">
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzYyYzQ2N2M3Mzk4ZWM2N2E2ZGY3ZGM0OQ==">
        |      <input type="hidden" name="csrfToken" value="ca735da57120289f5dee74e5d88f8b61c79f1f0e-1733153098084-7e9211d2eb67745dd698abf7">
        |      <div class="govuk-form-group govuk-form-group--error govuk-character-count" data-i18n.characters-under-limit.one="Mae gennych 1 cymeriad yn weddill" data-module="govuk-character-count" data-i18n.words-over-limit.one="Mae gennych 1 gair yn ormod" data-i18n.words-at-limit="Mae gennych 0 o eiriau yn weddill" data-threshold="75" data-maxlength="4000" data-i18n.characters-over-limit.other="Mae gennych %{count} o gymeriadau yn ormod" data-i18n.characters-at-limit="Mae gennych 0 o gymeriadau yn weddill" data-i18n.words-over-limit.other="Mae gennych %{count} o eiriau yn ormod" data-i18n.words-under-limit.other="Mae gennych %{count} o eiriau yn weddill" data-i18n.words-under-limit.one="Mae gennych 1 gair yn weddill" data-i18n.characters-over-limit.one="Mae gennych 1 cymeriad yn ormod" data-i18n.characters-under-limit.other="Mae gennych %{count} o gymeriadau yn weddill">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Ateb y neges hon
        |         </label>
        |         <p id="reply-form-error" class="govuk-error-message" data-gtm-vis-recent-on-screen8267218_1730="61" data-gtm-vis-first-on-screen8267218_1730="61" data-gtm-vis-total-visible-time8267218_1730="100" data-gtm-vis-has-fired8267218_1730="1">
        |            <span class="govuk-visually-hidden">Error:</span>
        |            Mae’n rhaid i chi ysgrifennu neges er mwyn ateb
        |         </p>
        |         <textarea class="govuk-textarea govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info reply-form-error" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message govuk-visually-hidden">
        |            Gallwch nodi hyd at 4000 o gymeriadau
        |         </div>
        |         <div class="govuk-hint govuk-character-count__message govuk-character-count__status govuk-character-count__message--disabled" aria-hidden="true">Mae gennych 4,000 o gymeriadau yn weddill</div>
        |         <div class="govuk-character-count__sr-status govuk-visually-hidden" aria-live="polite" aria-hidden="true">Mae gennych 4,000 o gymeriadau yn weddill</div>
        |      </div>
        |      <button type="submit" class="govuk-button" data-module="govuk-button">
        |      Anfon
        |      </button>
        |   </form>
        |</div>
        |""".stripMargin
    } else {
      """<style>
        |   @media screen and (max-width: 414px) {
        |   .custom-caption {
        |   font-size: 14px !important;
        |   }
        |   }
        |   .govuk-heading-l {
        |   margin-bottom: -15px;
        |   }
        |   .custom-caption {
        |   font-size: 16px;
        |   color: #505a5f;
        |   display: block;
        |   padding-top: 2px;
        |   }
        |   .break-long  {
        |   word-break: break-word
        |   }
        |</style>
        |<div class="govuk-body-l">
        |   <div class="govuk-error-summary" data-module="govuk-error-summary">
        |      <div role="alert">
        |         <h2 class="govuk-error-summary__title">There is a problem</h2>
        |         <div class="govuk-error-summary__body">
        |            <ul class="govuk-list govuk-error-summary__list">
        |               <li>
        |                  <a href="#reply-form">You must write a message to reply</a>
        |               </li>
        |            </ul>
        |         </div>
        |      </div>
        |   </div>
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">about import doc test</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      HMRC Imports(CDS) needs actions sent this on 17 May 2023 at 10:43am
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      HMRC Imports(CDS) needs actions sent this on 17 May 2023 at 10:43am
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      First read on 10 July 2023 at 1:13pm
        |      </span>
        |      <span class="govuk-visually-hidden">First viewed on 10 July 2023 at 1:13pm</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      {"regime":"ztZAlnDY2qUTNFeBD6m3MQ==","templateId":"udALJyIbKapI/7d2Hhh9XOm2nRKxrP1ZQqGXLASQL8o=","platform":"4PRiqswj7rqtjhES3PWcdg==","ContactPolicyGroupId":"yk7_hM8eQQeFwA3zHyfRgg"}
        |   </div>
        |   <style>
        |      .govuk-character-count {
        |      margin-bottom: 5px;
        |      }
        |      span#reply-form-error + textarea {
        |      border: 2px solid #d4351c;
        |      }
        |   </style>
        |   <hr aria-hidden="true" class="govuk-section-break govuk-section-break--m govuk-section-break--visible">
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY0NjRhMWNlMDliN2Q4MWMxNTYwNzBiMA==">
        |      <input type="hidden" name="csrfToken" value="a9e325275abcd116023dbeec52c1d424b2868ff1-1733154382877-7e9211d2eb67745dd698abf7">
        |      <div class="govuk-form-group govuk-form-group--error govuk-character-count" data-module="govuk-character-count" data-threshold="75" data-maxlength="4000">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Reply to this message
        |         </label>
        |         <p id="reply-form-error" class="govuk-error-message" data-gtm-vis-recent-on-screen8267218_1730="73" data-gtm-vis-first-on-screen8267218_1730="73" data-gtm-vis-total-visible-time8267218_1730="100" data-gtm-vis-has-fired8267218_1730="1">
        |            <span class="govuk-visually-hidden">Error:</span>
        |            You must write a message to reply
        |         </p>
        |         <textarea class="govuk-textarea govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info reply-form-error" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message govuk-visually-hidden">
        |            You can enter up to 4000 characters
        |         </div>
        |         <div class="govuk-hint govuk-character-count__message govuk-character-count__status govuk-character-count__message--disabled" aria-hidden="true">You have 4,000 characters remaining</div>
        |         <div class="govuk-character-count__sr-status govuk-visually-hidden" aria-live="polite" aria-hidden="true">You have 4,000 characters remaining</div>
        |      </div>
        |      <button type="submit" class="govuk-button" data-module="govuk-button">
        |      Send
        |      </button>
        |   </form>
        |</div>
        |""".stripMargin
    }
  }

// scalastyle:on

  def replySubmissionResult(language: String) =
    if (language == "cy") {
      """<style>
        |   .govuk-panel--confirmation > * {
        |   word-break: break-word;
        |   }
        |</style>
        |<div class="govuk-panel govuk-panel--confirmation">
        |   <h1 class="govuk-panel__title">
        |      Anfonwyd y neges
        |   </h1>
        |   <div class="govuk-panel__body">
        |      Daeth eich neges i law
        |   </div>
        |</div>
        |<h2 class="govuk-heading-m">Yr hyn sy’n digwydd nesaf</h2>
        |<p class="govuk-body">Does dim rhaid i chi wneud unrhyw beth ar hyn o bryd.</p>
        |<p class="govuk-body">Byddwn yn cysylltu â chi os bydd angen rhagor o wybodaeth arnom.</p>
        |<form action="/cds-file-upload-service/messages">
        |   <input type="hidden" name="sent" value="true">
        |   <button class="govuk-button" data-module="govuk-button">Yn ôl i’ch negeseuon</button>
        |</form>
        |""".stripMargin
    } else {
      """<style>
        |   .govuk-panel--confirmation > * {
        |   word-break: break-word;
        |   }
        |</style>
        |<div class="govuk-panel govuk-panel--confirmation">
        |   <h1 class="govuk-panel__title">
        |      Message sent
        |   </h1>
        |   <div class="govuk-panel__body">
        |      We received your message
        |   </div>
        |</div>
        |<h2 class="govuk-heading-m">What happens next</h2>
        |<p class="govuk-body">You do not need to do anything now.</p>
        |<p class="govuk-body">We will contact you if we need more information.</p>
        |<form action="/cds-file-upload-service/messages">
        |   <input type="hidden" name="sent" value="true">
        |   <button class="govuk-button" data-module="govuk-button">Back to your messages</button>
        |</form>
        |""".stripMargin
    }
}
