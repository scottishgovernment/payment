package scot.gov.payment.service.worldpay.responseurls;

import org.junit.Assert;
import org.junit.Test;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PaymentUrlFormatterTest {

    String siteUrl = "https://www.gov.scot/";

    @Test
    public void formatsPaymentUrlAsExected() throws Exception {

        // ARRANGE
        PaymentUrlFormatter sut = new PaymentUrlFormatter();
        String paymentUrl = "https://www.worldpay.com/?foo=bar";

        // ACR
        String actual  = sut.formatPaymentUrl(paymentUrl, "orderCode1", siteUrl);

        // ASSERT
        // the url starts with the paymenturl.
        assertTrue(startsWith(actual, paymentUrl));
        Map<String, String> params = params(actual);
        assertHasExpectedResultUrlParam(params, "successURL",
                "https://www.gov.scot/payment/authorised/?orderCode=orderCode1");

        assertHasExpectedResultUrlParam(params, "pendingURL",
                "https://www.gov.scot/payment/redirected/?orderCode=orderCode1");

        assertHasExpectedResultUrlParam(params, "failureURL",
                "https://www.gov.scot/payment/refused/?orderCode=orderCode1");

        assertHasExpectedResultUrlParam(params, "cancelURL",
                "https://www.gov.scot/payment/cancelled/?orderCode=orderCode1");

        assertHasExpectedResultUrlParam(params, "errorURL",
                "https://www.gov.scot/payment/error/?orderCode=orderCode1");
    }

    Map<String, String> params(String url) {
        String query = url.split("\\?")[1];
        String [] paramArray = query.split("&");
        List<String> paramList = Arrays.asList(paramArray);
        return paramList.stream()
                .map(param -> param.split("="))
                .collect(toMap(params -> params[0], params -> params[1]));
    }

    void assertHasExpectedResultUrlParam(Map<String, String> params, String param, String expectedValue) throws Exception {
        Assert.assertTrue(params.containsKey(param));
        String encodedValiue = params.get(param);
        String actual = URLDecoder.decode(encodedValiue, "UTF-8");
        assertEquals(expectedValue, actual);
    }
}
