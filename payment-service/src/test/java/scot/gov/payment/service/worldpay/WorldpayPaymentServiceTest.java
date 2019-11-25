package scot.gov.payment.service.worldpay;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;
import scot.gov.payment.service.worldpay.responseurls.PaymentUrlFormatter;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 20/11/2019.
 */
public class WorldpayPaymentServiceTest {

    String siteUrl = "https://www.gov.scot/";

    @Test
    public void successResponseFromWorldHandledCorrectly() throws Exception {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentResult paymentResult = new PaymentResult();
        sut.target = targetWithResponse(200, xmlFixture("/successResponse.xml"));
        sut.worldpayDocumentParser = mock(WorldpayDocumentParser.class);
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        sut.paymentUrlFormatter = appendingUrlFormatter();
        when(sut.worldpayDocumentParser.parseResponse(any())).thenReturn(paymentResult);

        // ACT
        PaymentResult actual = sut.makePayment(paymentRequest, siteUrl);

        // ASSERT
        assertSame(paymentResult, actual);
    }

    @Test
    public void errorResponseFromWorldHandledCorrectly() throws Exception {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        sut.target = targetWithResponse(500, xmlFixture("/successResponse.xml"));
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);

        // ACT
        PaymentResult actual = sut.makePayment(paymentRequest, siteUrl);

        // ASSERT
        assertFalse(actual.isSuccess());
    }

    @Test(expected = PaymentException.class)
    public void webTargetExceptionWrappedAsExpected() throws Exception {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentResult paymentResult = new PaymentResult();
        sut.target = exceptionThrowingTarget();
        sut.worldpayDocumentParser = mock(WorldpayDocumentParser.class);
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        when(sut.worldpayDocumentParser.parseResponse(any())).thenReturn(paymentResult);

        // ACT
        PaymentResult actual = sut.makePayment(paymentRequest, siteUrl);

        // ASSERT -- see expected
    }

    @Test(expected = PaymentException.class)
    public void documentBuilderExceptionWrappedAsExpected() throws Exception {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentResult paymentResult = new PaymentResult();
        sut.target = targetWithResponse(200, xmlFixture("/successResponse.xml"));
        sut.worldpayDocumentParser = mock(WorldpayDocumentParser.class);
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        when(sut.worldpayDocumentBuilder.buildPaymentDocuemnt(any())).thenThrow(new RuntimeException("arg"));
        when(sut.worldpayDocumentParser.parseResponse(any())).thenReturn(paymentResult);

        // ACT
        PaymentResult actual = sut.makePayment(paymentRequest, siteUrl);

        // ASSERT -- see expected exception
    }

    WebTarget targetWithResponse(int status, Object entity) {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(status);
        when(response.getEntity()).thenReturn(entity);

        WebTarget target = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);
        when(builder.post(any())).thenReturn(response);

        return target;
    }

    WebTarget exceptionThrowingTarget() {
        WebTarget target = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);
        when(builder.post(any())).thenThrow(new RuntimeException("arg"));
        return target;
    }

    InputStream xmlFixture(String name) throws IOException {
        return WorldpayPaymentServiceTest.class.getResourceAsStream(name);
        //return IOUtils.toString(in, "UTF-8");
    }

    PaymentUrlFormatter appendingUrlFormatter() {
        return new PaymentUrlFormatter() {
            @Override
            public String formatPaymentUrl(String baseUrl, String orderCode, String siteUrl) {
                return baseUrl + "-postfix";
            }
        };
    }
}
