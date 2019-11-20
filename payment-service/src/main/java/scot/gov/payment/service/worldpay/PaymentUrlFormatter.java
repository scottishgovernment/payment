package scot.gov.payment.service.worldpay;

import scot.gov.payment.PaymentConfiguration;

import javax.inject.Inject;

public class PaymentUrlFormatter {

    @Inject
    PaymentConfiguration configuration;

    String formatPaymentUrl(String baseUrl) {
        // TODO: logic to format the repsone url as mentioned here:
        // http://confluence.digital.gov.uk/pages/viewpage.action?spaceKey=MGV&title=Worldpay+PoC+Test+via+postman+and+browser
        // see if Katie remembers where the full docs are for this....
        return baseUrl;
    }
}
