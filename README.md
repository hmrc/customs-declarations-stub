
# customs-declarations-stub

 [ ![Download](https://api.bintray.com/packages/hmrc/releases/customs-declarations-stub/images/download.svg) ](https://bintray.com/hmrc/releases/customs-declarations-stub/_latestVersion)

This application provides a stubs for the following services:
* Customs Declarations API service - that enables frontend services that use Customs Declarations API with a stub to develop locally without depending on the API. 
* Customs Data Store service - just for the email verification endpoint (no other endpoints are stubbed here)

## Customs Declarations API service

### How to deliver custom notifications
#### Submissions
If you send a declaration with specific letter at the beginning of the LRN you can control what notifications you receive.

If LRN starts with:
- 'B' - Stub will send a 'Rejected' notification (see section below on how to specify what error code is returned)
- 'C' - Stub will send a 'Cleared' notification
- 'D' - Stub will send an 'Accepted' and an 'Additional Documents Required' notifications
- 'G' - Stub will send an 'Accepted' notification
- 'I' - Stub will send a 'Awaiting exit results' notification
- 'J' - Stub will send a 'Declaration handled externally' notification
- 'K' - Stub will send a 'Declaration expired (no arrival)' notification
- 'L' - Stub will send a 'Declaration expired (no departure)' notification
- 'N' - Stub will send a 'Released' notification
- 'P' - Stub will not send any notifications, resulting in a 'Declaration Pending' state
- 'Q' - Stub will send a 'Query Notification Message' notification
- 'R' - Stub will send a 'Received' notification
- 'U' - Stub will send an 'Undergoing Physical Check' notification
- 'X' - Stub will send a 'Goods Have Exited The Community' notification
- other letters will invoke default behaviour which is Accepted notification

In addition, if the 2nd character of the LRN is a digit (0-9) you can control the delay in seconds of the notification delivery.

For 'Rejected' notifications it is possible to control what error codes are returned by the stub. If LRN starts with 'BCDS' then the stub will treat the next five digits as the
CDS error code you want it to return. Not all error codes are supported, if the code requested is not supported then the stub by default will return a CDS10020 error. Currently
supported codes are: CDS10010, CDS10020, CDS12046, CDS12056, CDS12062, CDS12070,CDS12075, CDS12077, CDS12119 & CDS12120

Note that, frontend side, the **content** of the "Confirmation" page displayed after the declaration submission will be specific only for the following LRN's initials:
- 'C' - 'Cleared' status (only if an 'Arrived' declaration) => "**Declaration accepted, goods have permission to progress**" content
- 'C' - 'Cleared' status (when not an 'Arrived' declaration) => "**Your declaration is still being checked**" content
- 'D' - 'Additional Documents Required' status => "**Your declaration needs documents attached**" content
- 'Q' - 'Query Notification Message' => "**Your declaration is still being checked**" content
- 'R' - 'Received' status => "**Your declaration has been pre-lodged with HMRC**" content
- 'U' - 'Undergoing Physical Check' status => "**Your declaration needs documents attached**" content
- 'X' - 'Goods Have Exited The Community' status => "**Your declaration is still being checked**" content

For any other LRN's initial letter the "Confirmation" page will show the "**Declaration accepted**" page.

-

#### Cancellations
The third character can be used to determine the notification response of a cancellation request:
- 'S' - Stub sends 'Customs Position Granted' indicating successful cancellation
- Any other letter - Stub sends 'Customs Position Denied' indicated a denied cancellation request

#### Amendments
The ID field of Means of Border Transport can determine the response of an amendment request (This field is modified by adding a vehicle ID on /border-transport):
- 'DENIED' - Return a 'Customs Position Denied' notification to indicate a denied request (Declaration in holding state).
- 'EXTERNAL AMEND' Return an 'External Amendment' notification to indicate that the "Customs" made a modification to the declaration
- 'PENDING' - Does not return any notification. Just accept the submission.
- 'REJECTED' - Return a 'Rejected' notification to indicate a rejected amendment that has errors.
- Any other value - Return a 'Customs Position Granted' indicating a successful amendment.
Note that to receive any sort of 'Amendment' notification the LRN must start with one of 'B', 'C', 'D', 'G', 'Q', 'R', 'U', 'X'.

(**Local-only**)
The endpoint **/external-amendment/:lrn/:mrn/:actionId**, where **actionId** is the id of the **SubmissionRequest** action, sends an "*External amendment*" notification without necessarily also submitting a declaration.
e.g. (with httpie) =>
> $ http :6790/external-amendment/CSLRN5317100/24GB1955OS54767262/430f51dc-7d38-40a1-9a52-592f8da99510

#### No Notifications
Sometimes you may wish to test the behaviour of a service when no notifications are returned by DMS. 

To achieve this you can set the ID field of Means of Border Transport (This field is modified by adding a vehicle ID on /border-transport) to the value "NONOTIFY". 
Any submission of a declaration to the stub with this value will suppress any expected notifications from being sent by the stub.  

### File Upload
This stub also mocks the '/file-upload' endpoint of the Cust Dec API. This endpoint returns fake S3 urls that actually point 
to the testOnly endpoint '/cds-file-upload-service/test-only/s3-bucket' of the CDS File Upload Frontend service.

The fake S3 bucket urls have to point to a url that is available in the MTDP public zone (to allow the user's browser to upload
files to it). This is why fake S3 urls can not point to a endpoint on this service (as it is deployed in the protected zone). 

When users upload their files with SFUS UI it must be through public DNS 'www.development.tax.service.gov.uk' (including when uploading files to test-only endpoint). 
However, the SFUS service itself (not the user's browser) uploads the generated 'contacts' file as the first file, so this file need to use the internal DNS 'cds-file-upload-frontend.public.mdtp'.

This stub therefore has special logic that returns the first S3 url pointing to the internal DNS 'cds-file-upload-frontend.public.mdtp' (for the contacts file) 
and all subsequent urls point to the external public DNS 'www.development.tax.service.gov.uk'


### Schema Validation
By default, the stub validates all XML payloads it receives with the corresponding schema. However, in performance test scenarios this can
slow down the operation of the stub so there is a feature flag to disable the Schema validation work to speed up response times:

`sbt "run -Dmicroservice.services.features.schemaValidation=disabled"`

## Customs Declarations Information stubbing
```
    GET    /mrn/:mrn/status
```
This endpoint returns mocked DeclarationStatusResponse with dynamic EORI and MRN taken from the request.
MRN is part of the url, but EORI is taken from Auth service using `Authorization` token.
```
    GET    /mrn/:mrn/full
```
This endpoint returns mocked XML with dynamic EORI, declaration version and MRN taken from the request.
MRN nd dec version are part of the url, but EORI is taken from Auth service using `Authorization` token.


By default the response is successful.

In case you need unsuccessful response to be returned, these can be triggered by providing MRN or EORI as per rules below:
- ends with '8888' - Not Found (404) response

## Customs Data Store service
### eMail Address Verification
```
    GET    /eori/<EORI>/verified-email
```
This endpoint enables retrieving the email address associated with the given EORI number as long as it is a **verified** or **undeliverable** email address.

If the EORI's email address is verified the 200(OK) response's payload is
```
{ "address":"some@email.com", "timestamp":"1987-03-20T01:02:03Z" }
```
Or, if the EORI's email address is undeliverable (bounced) the 200(OK) response's payload is:
```
{
    "address": "some@email.com",
    "timestamp": "2020-03-20T01:02:03Z",
    "undeliverable": {
          "subject": "subject-example",
          "eventId": "example-id",
          "groupId": "example-group-id",
          "timestamp": "2021-05-14T10:59:45.811+01:00",
          "event": {
                     "id": "example-id",
                    "event": "someEvent",
                    "emailAddress": "some@email.com",
                    "detected": "2021-05-14T10:59:45.811+01:00",
                    "code": 12,
                    "reason": "Inbox full",
                    "enrolment": "HMRC-CUS-ORG~EORINumber~testEori"
        }
     }
}
```
otherwise a 404(NOT_FOUND) response is returned.

Note that for any given EORI number ending in `99`, the associated email address will always be considered **unverified**, resulting accordingly in a 404(NOT_FOUND) response.
For an EORI number inding in '98', the associated email address will always be considered **undeliverable**, resulting accordingly in an OK response with the above payload.


## SECURE TWO WAY MESSAGING
Endpoints used by the CDS Exports service to simulate the secure-message-frontend service (which provides partial html components displayed in the SFUS service).

```
GET           /secure-message-frontend/cds-file-upload-service/messages
```
Retrieve the html partial for the user's inbox

```
GET           /secure-message-frontend/cds-file-upload-service/conversation/:client/:conversationId
```
Retrieve the html partial for a specific message's content

```
POST          /secure-message-frontend/cds-file-upload-service/conversation/:client/:conversationId
```
Send the form post of the user's message in reply

```
GET           /secure-message-frontend/cds-file-upload-service/conversation/:client/:conversationId/result
```
Retrieve the html partial for the reply sent receipt page

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
