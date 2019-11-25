# Payment

This service is used to integrate with Worldpay for incoming payments to the Scottish Government via the gov.scot website

# Configuration

* `worldpay_url`
    * Url used to submit payments
    * Type: Sting
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
    * Merchant code included in worldpay requests
    * Type: String
    * Default: none

* `worldpay_instalationId`
    * Instalation id included in worldpay requests
    * Type: String
    * Default: none


# Endpoints

`POST /payment`

Requests a payment be processed.  If successful the response will indicate a worldpay url that the user can be
redirected to to make a payment.  If worldpay return an error or an exception is thrown then an error will be
returned.

{
    "validPostcode": true,
    "scottishPostcode": true,
    "inRentPressureZone": true,
    "rentPressureZoneTitle": "Stockbridge, Edinburgh"
    "maxIncrease": 1.5
}

# Monitoring

The healthcheck endpoint is `GET /health`.  The status code is `200` if the service is healthy, and `503`
otherwise.

The `data` property is an erray of metrics collected when processing payments, and `errors' will return a list of
errors that have caused the serrivce to be unhealthy.
