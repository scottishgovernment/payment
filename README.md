# Payment

This service is used to integrate with Worldpay for incoming payments to the
Scottish Government via the www.gov.scot website.

# Configuration

* `port`
    * Port that the service should listen on.
    * Type: Integer
    * Default: 9095

* `worldpay_url`
    * Url used to submit payments
    * Type: String
    * Default: none

* `worldpay_username`
    * Username used to authenticate with Worldpay
    * Type: String
    * Default: none

* `worldpay_password`
    * Password used to authenticate with Worldpay
    * Type: String
    * Default: none

* `worldpay_mechantCode`
    * Merchant code included in Worldpay requests
    * Type: String
    * Default: none

* `worldpay_installationId`
    * Installation id included in Worldpay requests
    * Type: String
    * Default: none


# Endpoints

`POST /payment`

Requests a payment be processed.  If successful the response will indicate a
Worldpay URL that the user can be redirected to to make a payment.  If Worldpay
return an error or an exception is thrown then an error will be returned.

# Monitoring

The healthcheck endpoint is `GET /health`.  The status code is `200` if the service
is healthy, and `503` otherwise.

The `data` property is an array of metrics collected when processing payments,
and `errors' will return a list of errors that have caused the service to be
unhealthy.
