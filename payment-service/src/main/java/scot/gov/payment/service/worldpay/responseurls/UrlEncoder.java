package scot.gov.payment.service.worldpay.responseurls;

import scot.gov.payment.service.PaymentException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Factored into own class for easier testing.
 */
public class UrlEncoder {

    private final String encoding;

    UrlEncoder(String encoding) {
        this.encoding = encoding;
    }

    String encodeUrl(String url) throws PaymentException {
        try {
            return URLEncoder.encode(url, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new PaymentException("Unable to encode url", e);
        }
    }
}
