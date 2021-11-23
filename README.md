
# customs-declarations-stub

 [ ![Download](https://api.bintray.com/packages/hmrc/releases/customs-declarations-stub/images/download.svg) ](https://bintray.com/hmrc/releases/customs-declarations-stub/_latestVersion)

This application provides a stubs for the following services:
* Customs Declarations API service - that enables frontend services that use Customs Declarations API with a stub to develop locally without depending on the API. 
* Customs Data Store service - just for the email verification endpoint (no other endpoints are stubbed here)
* Upscan service - the 'batch-file-upload' and 's3-bucket' endpoints
* Tariff API

## Customs Declarations API service

### How to deliver custom notifications
If you send a declaration with specific letter at the beginning of the LRN you can control what notifications you receive.

If LRN starts with:
- 'B' - Stub will send a 'Rejected' notification
- 'D' - Stub will send an 'Accepted' and an 'Additional Documents Required' notifications
- 'G' - Stub will send an 'Accepted' notification
- 'Q' - Stub will send a 'Query Notification Message' notification
- 'R' - Stub will send a 'Received' notification
- 'U' - Stub will send an 'Undergoing Physical Check' notification
- 'X' - Stub will send a 'Goods Have Exited The Community' notification
- other letters will invoke default behaviour which is Accepted notification

In addition if the 2nd character of the LRN is a digit (0-9) you can control the delay in seconds of the notification delivery.

## Customs Declarations Information stubbing
```
    GET    /mrn/:mrn/status
```
This endpoint returns mocked DeclarationStatusResponse with dynamic EORI and MRN taken from the request.
MRN is part of the url, but EORI is taken from Auth service using `Authorization` token.

By default the response is successful.

In case you need unsuccessful response to be returned, these can be triggered by providing MRN as per rules below:
- ends with '9999' - Not Found (404) response

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


## TARIFF API
Endpoint used by the CDS Exports service to simulate the [Tariff API](https://api.trade-tariff.service.gov.uk/reference.html#get-commodities-id)
which, given a goods_nomenclature_item_id (a commodity code), responds with a Json payload which identifies the commodity.

```
    GET    /api/v2/commodities/:id
```

The given id must be a numeric string of 10 digits otherwise a Not Found (404) response is returned.

If the given id is:
- '2208303000' - the payload of the OK (200) response is the content of 'conf/messages/supplementary-units-2208303000.json' (supplementary units -> l alc. 100%)
- '3903110000' - the payload of the OK (200) response is the content of 'conf/messages/supplementary-units-3903110000.json' (no supplementary units)
- '6103230000' - the payload of the OK (200) response is the content of 'conf/messages/supplementary-units-6103230000.json' (supplementary units -> p/st)
- '7114110000' - the payload of the OK (200) response is the content of 'conf/messages/supplementary-units-7114110000.json' (supplementary units -> g)

Otherwise, if the last digit of the given id is:
- '0' or '1' - the payload of the OK (200) response is the content of 'conf/messages/supplementary-units-required.json' (supplementary units -> l alc. 100%)
- '9' - the response is Not Found (404)

For any other trailing digit, the body of the OK (200) response is the content of 'conf/messages/supplementary-units-not-required.json' 

## Upscan service
Endpoint to simulate an S3 url that accepts a multipart file upload and sends the required success/failure notification to the SFUS backend service. 

```
    POST    /upscan/s3-bucket
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
