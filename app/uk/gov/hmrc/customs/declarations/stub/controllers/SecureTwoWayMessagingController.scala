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
        |               <a href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==" class="govuk-!-font-weight-bold hover-effect" style="font-weight:400; text-decoration: none;" data-sso="false">
        |               <span class="govuk-visually-hidden">oddi wrth:</span><span style="font-weight:400; text-decoration: none;">National Clearance Hub yet<span class="govuk-visually-hidden">.&nbsp;</span></span>
        |               </a>
        |               <br>
        |               <div class="govuk-task-list__name-and-hint black-text">
        |                  <a class="govuk-link" href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==" aria-describedby="company-details-1-status" data-sso="false">
        |                  <span class="govuk-visually-hidden">Pwnc:</span><span class="black-text no--underline">MRN123456789098765 needs some action</span><span class="govuk-visually-hidden">,&nbsp;</span>
        |                  </a>
        |               </div>
        |            </td>
        |            <td class="govuk-table__cell mob-align-right">
        |               <span class="">3:00yh</span>
        |               <span class="govuk-visually-hidden">3:00yh</span>
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
         |                  <a href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==" class="govuk-!-font-weight-bold hover-effect" style="font-weight:400; text-decoration: none;" data-sso="false">
         |                  <span class="govuk-visually-hidden">from:</span><span style="font-weight:400; text-decoration: none;">National Clearance Hub yet<span class="govuk-visually-hidden">.&nbsp;</span></span>
         |                  </a>
         |                  <br>
         |                  <div class="govuk-task-list__name-and-hint black-text">
         |                     <a class="govuk-link" href="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==" aria-describedby="company-details-1-status" data-sso="false">
         |                     <span class="govuk-visually-hidden">Subject:</span><span class="black-text no--underline">MRN123456789098765 needs some action</span><span class="govuk-visually-hidden">,&nbsp;</span>
         |                     </a>
         |                  </div>
         |               </td>
         |               <td class="govuk-table__cell mob-align-right">
         |                  <span class="">3:00pm</span>
         |                  <span class="govuk-visually-hidden">3:00pm</span>
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
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">MRN123456789098765 needs some action</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      National Clearance Hub wnaeth anfon y neges hon ar 3 Rhagfyr 2024 am 3:00yh
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      National Clearance Hub wnaeth anfon y neges hon ar 3 Rhagfyr 2024 am 3:00yh
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      Darllenwyd am y tro cyntaf ar 3 Rhagfyr 2024 am 3:01yh
        |      </span>
        |      <span class="govuk-visually-hidden">Gwelwyd am y tro cyntaf ar 3 Rhagfyr 2024 am 3:01yh</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      this is small test for cds raise a query
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
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==">
        |      <input type="hidden" name="csrfToken" value="e5df1dd7da0f6967462d02322e20c7ae48a9bc96-1733243186957-a8cc32f42cd75e9aa0eaf36f">
        |      <div class="govuk-form-group govuk-character-count" data-i18n.characters-under-limit.one="Mae gennych 1 cymeriad yn weddill" data-module="govuk-character-count" data-i18n.words-over-limit.one="Mae gennych 1 gair yn ormod" data-i18n.words-at-limit="Mae gennych 0 o eiriau yn weddill" data-threshold="75" data-maxlength="4000" data-i18n.characters-over-limit.other="Mae gennych %{count} o gymeriadau yn ormod" data-i18n.characters-at-limit="Mae gennych 0 o gymeriadau yn weddill" data-i18n.words-over-limit.other="Mae gennych %{count} o eiriau yn ormod" data-i18n.words-under-limit.other="Mae gennych %{count} o eiriau yn weddill" data-i18n.words-under-limit.one="Mae gennych 1 gair yn weddill" data-i18n.characters-over-limit.one="Mae gennych 1 cymeriad yn ormod" data-i18n.characters-under-limit.other="Mae gennych %{count} o gymeriadau yn weddill">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Ateb y neges hon
        |         </label>
        |         <textarea class="govuk-textarea govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message">
        |            Gallwch nodi hyd at 4000 o gymeriadau
        |         </div>
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
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">MRN123456789098765 needs some action</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      National Clearance Hub sent this on 3 December 2024 at 3:00pm
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      National Clearance Hub sent this on 3 December 2024 at 3:00pm
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      First read on 3 December 2024 at 3:01pm
        |      </span>
        |      <span class="govuk-visually-hidden">First viewed on 3 December 2024 at 3:01pm</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      this is small test for cds raise a query
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
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==">
        |      <input type="hidden" name="csrfToken" value="61fa6b1645e08d6f36cabf4e0906439bb54172c9-1733243312130-a8cc32f42cd75e9aa0eaf36f">
        |      <div class="govuk-form-group govuk-character-count" data-module="govuk-character-count" data-threshold="75" data-maxlength="4000">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Reply to this message
        |         </label>
        |         <textarea class="govuk-textarea govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message">
        |            You can enter up to 4000 characters
        |         </div>
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
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">MRN123456789098765 needs some action</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      National Clearance Hub wnaeth anfon y neges hon ar 3 Rhagfyr 2024 am 3:00yh
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      National Clearance Hub wnaeth anfon y neges hon ar 3 Rhagfyr 2024 am 3:00yh
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      Darllenwyd am y tro cyntaf ar 3 Rhagfyr 2024 am 3:01yh
        |      </span>
        |      <span class="govuk-visually-hidden">Gwelwyd am y tro cyntaf ar 3 Rhagfyr 2024 am 3:01yh</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      this is small test for cds raise a query
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
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==">
        |      <input type="hidden" name="csrfToken" value="80d80d2d920ef26ab805ad00d3979d3b03aca703-1733243429206-a8cc32f42cd75e9aa0eaf36f">
        |      <div class="govuk-form-group govuk-form-group--error govuk-character-count" data-i18n.characters-under-limit.one="Mae gennych 1 cymeriad yn weddill" data-module="govuk-character-count" data-i18n.words-over-limit.one="Mae gennych 1 gair yn ormod" data-i18n.words-at-limit="Mae gennych 0 o eiriau yn weddill" data-threshold="75" data-maxlength="4000" data-i18n.characters-over-limit.other="Mae gennych %{count} o gymeriadau yn ormod" data-i18n.characters-at-limit="Mae gennych 0 o gymeriadau yn weddill" data-i18n.words-over-limit.other="Mae gennych %{count} o eiriau yn ormod" data-i18n.words-under-limit.other="Mae gennych %{count} o eiriau yn weddill" data-i18n.words-under-limit.one="Mae gennych 1 gair yn weddill" data-i18n.characters-over-limit.one="Mae gennych 1 cymeriad yn ormod" data-i18n.characters-under-limit.other="Mae gennych %{count} o gymeriadau yn weddill">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Ateb y neges hon
        |         </label>
        |         <p id="reply-form-error" class="govuk-error-message">
        |            <span class="govuk-visually-hidden">Error:</span>
        |            Mae’n rhaid i chi ysgrifennu neges er mwyn ateb
        |         </p>
        |         <textarea class="govuk-textarea govuk-textarea--error govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info reply-form-error" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message">
        |            Gallwch nodi hyd at 4000 o gymeriadau
        |         </div>
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
        |   <h1 class="govuk-heading-l margin-top-small margin-bottom-small">MRN123456789098765 needs some action</h1>
        |   <p>
        |      <span aria-hidden="true" class="custom-caption">
        |      National Clearance Hub sent this on 3 December 2024 at 3:00pm
        |      </span>
        |      <span class="govuk-visually-hidden">
        |      National Clearance Hub sent this on 3 December 2024 at 3:00pm
        |      </span>
        |      <span aria-hidden="true" class="custom-caption">
        |      First read on 3 December 2024 at 3:01pm
        |      </span>
        |      <span class="govuk-visually-hidden">First viewed on 3 December 2024 at 3:01pm</span>
        |   </p>
        |   <div class="govuk-body break-long">
        |      this is small test for cds raise a query
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
        |   <form method="POST" novalidate="" action="/cds-file-upload-service/conversation/CDCM/Y29udmVyc2F0aW9uLzY3NGYxZDJhMGUzNDZmNWFjYzBiOTVlYQ==">
        |      <input type="hidden" name="csrfToken" value="9e3064aa0d8a9434fea3fe818c5427161bd56f6a-1733243368031-a8cc32f42cd75e9aa0eaf36f">
        |      <div class="govuk-form-group govuk-form-group--error govuk-character-count" data-module="govuk-character-count" data-threshold="75" data-maxlength="4000">
        |         <label class="govuk-label govuk-label--s " for="reply-form">
        |         Reply to this message
        |         </label>
        |         <p id="reply-form-error" class="govuk-error-message">
        |            <span class="govuk-visually-hidden">Error:</span>
        |            You must write a message to reply
        |         </p>
        |         <textarea class="govuk-textarea govuk-textarea--error govuk-js-character-count" id="reply-form" name="content" rows="5" aria-describedby="reply-form-info reply-form-error" spellcheck="true"></textarea>
        |         <div id="reply-form-info" class="govuk-hint govuk-character-count__message">
        |            You can enter up to 4000 characters
        |         </div>
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
