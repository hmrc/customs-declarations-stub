
# customs-declarations-stub

 [ ![Download](https://api.bintray.com/packages/hmrc/releases/customs-declarations-stub/images/download.svg) ](https://bintray.com/hmrc/releases/customs-declarations-stub/_latestVersion)

This application provides a stubs for the following services:
* Customs Declarations API service - that enables frontend services that use Customs Declarations API with a stub to develop locally without depending on the API. 
* Customs Data Store service - just for the email verification endpoint (no other endpoints are stubbed here)
* Upscan service - the 'batch-file-upload' and 's3-bucket' endpoints

##Customs Declarations API service

### Usage custom notifications
If you send a declaration with specific letter at the beginning of the LRN you can control what notifications you receive.

If LRN starts with:
- 'G' - Stub will send Accepted notification
- 'B' - Stub will send Rejected notification
- 'D' - Stub will send Accepted and Additional Documents Required notifications
- 'Q' - Stub will send Query Notification Message notification
- other letters will invoke default behaviour which is Accepted notification

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
This endpoint enables retrieving the email address associated with the given EORI number as long as it is a **verified** email address.

If the EORI's email address is verified the 200(OK) response's payload is
```
{ "address":"some@email.com", "timestamp":"1987-03-20T01:02:03Z" }
```
otherwise a 404(NOT_FOUND) response is returned.

Note that for any given EORI number ending in `99`, the associated email address will always be considered **unverified**, resulting accordingly in a 404(NOT_FOUND) response.

## Upscan service
Endpoint to simulate an S3 url that accepts a multipart file upload and sends the required success/failure notification to the SFUS backend service. 

```
    POST    /upscan/s3-bucket
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
