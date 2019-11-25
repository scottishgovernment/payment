package scot.gov.payment.service.worldpay.responseurls;

import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.worldpay.WorldpayResponseType;

import java.nio.charset.StandardCharsets;

public class PaymentUrlFormatter {

    UrlEncoder encoder = new UrlEncoder(StandardCharsets.UTF_8.toString());

    public String formatPaymentUrl(
            String paymentUrl,
            String orderCode,
            String siteUrl) throws PaymentException {
        StringBuilder stringBuilder = new StringBuilder(paymentUrl).append("");
        for (WorldpayResponseType type : WorldpayResponseType.values()) {
            appendParamFortype(stringBuilder, type, orderCode, siteUrl);
        }
        return stringBuilder.toString();
    }

    void appendParamFortype(
            StringBuilder builder,
            WorldpayResponseType type,
            String orderCode,
            String siteUrl) throws PaymentException {

        String responseUrl = responseUrl(type, orderCode, siteUrl);
        String encodesResponseUrl = encoder.encodeUrl(responseUrl);
        builder
                .append('&')
                .append(type.getParamName())
                .append('=')
                .append(encodesResponseUrl);
    }

    String responseUrl(WorldpayResponseType type, String orderCode, String siteUrl) {
        return new StringBuilder(siteUrl)
                .append("payment-result/")
                .append(type.name().toLowerCase()).append("/")
                .append("?param=")
                .append(orderCode)
                .toString();
    }

}
