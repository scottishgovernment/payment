package scot.gov.payment.service.worldpay;

import jakarta.ws.rs.core.Response;
import org.junit.Test;
import scot.gov.payment.service.*;
import scot.gov.payment.service.worldpay.responseurls.PaymentUrlFormatter;

import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorldpayPaymentServiceTest {

    String siteUrl = "https://www.gov.scot/";

    @Test
    public void documentBuilderExceptionWrappedAsExpected() throws Exception {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        when(sut.worldpayDocumentBuilder.buildPaymentDocument(any())).thenThrow(new RuntimeException("arg"));

        // ACT
        sut.makePayment(paymentRequest, siteUrl, new PaymentCallback() {
            @Override
            public void onPaymentResult(PaymentRequest request, PaymentResult result) {
                fail("expected an exception");
            }

            @Override
            public void onPaymentException(PaymentRequest request, PaymentException exception) {
                assertNotNull(exception);
            }
        });
    }

    @Test
    public void successResponseFromWorldHandledCorrectly() {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        Response response = response(200, xmlFixture("/successResponse.xml"));
        sut.amountConverter = new AmountConverter();
        sut.worldpayDocumentParser = new WorldpayDocumentParser();
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        sut.paymentUrlFormatter = appendingUrlFormatter();

        // ACT
        sut.handleResponse(paymentRequest, siteUrl, response, new PaymentCallback() {
            @Override
            public void onPaymentResult(PaymentRequest request, PaymentResult result) {
                assertTrue(result.isSuccess());
            }

            @Override
            public void onPaymentException(PaymentRequest request, PaymentException exception) {
                fail("did not expect an exception");
            }
        });
    }

    @Test
    public void duplicateOrderResponseFromWorldHandledCorrectly() {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        Response response = response(200, xmlFixture("/errorResponse.xml"));
        sut.worldpayDocumentParser = new WorldpayDocumentParser();
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        sut.paymentUrlFormatter = appendingUrlFormatter();

        // ACT
        sut.handleResponse(paymentRequest, siteUrl, response, new PaymentCallback() {
            @Override
            public void onPaymentResult(PaymentRequest request, PaymentResult result) {
                assertFalse(result.isSuccess());
            }

            @Override
            public void onPaymentException(PaymentRequest request, PaymentException exception) {
                fail("did not expect an exception");
            }
        });
    }

    @Test
    public void serverErrorResponseFromWorldHandledCorrectly() throws Exception {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentResult paymentResult = PaymentResultBuilder.success().build();
        Response response = response(500, xmlFixture("/successResponse.xml"));
        sut.worldpayDocumentParser = mock(WorldpayDocumentParser.class);
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        sut.paymentUrlFormatter = appendingUrlFormatter();
        when(sut.worldpayDocumentParser.parseResponse(any())).thenReturn(paymentResult);

        // ACT
        sut.handleResponse(paymentRequest, siteUrl, response, new PaymentCallback() {
            @Override
            public void onPaymentResult(PaymentRequest request, PaymentResult result) {
                assertFalse(result.isSuccess());
            }

            @Override
            public void onPaymentException(PaymentRequest request, PaymentException exception) {
                fail("did not expect an exception");
            }
        });
    }

    @Test
    public void documentParseExceptionHandledCorrectly() throws Exception {
        // ARRANGE
        WorldpayPaymentService sut = new WorldpayPaymentService();
        PaymentRequest paymentRequest = new PaymentRequest();
        Response response = response(200, xmlFixture("/successResponse.xml"));
        sut.worldpayDocumentParser = mock(WorldpayDocumentParser.class);
        PaymentException paymentException = new PaymentException("arg");
        when(sut.worldpayDocumentParser.parseResponse(any())).thenThrow(paymentException);
        sut.worldpayDocumentBuilder = mock(WorldpayDocumentBuilder.class);
        sut.paymentUrlFormatter = appendingUrlFormatter();

        // ACT
        sut.handleResponse(paymentRequest, siteUrl, response, new PaymentCallback() {
            @Override
            public void onPaymentResult(PaymentRequest request, PaymentResult result) {
                fail("expected a failure");
            }

            @Override
            public void onPaymentException(PaymentRequest request, PaymentException exception) {
                assertEquals(paymentRequest, request);
                assertEquals(paymentException, exception);
            }
        });
    }

    Response response(int status, Object entity) {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(status);
        when(response.getEntity()).thenReturn(entity);
        return response;
    }

    InputStream xmlFixture(String name) {
        return WorldpayPaymentServiceTest.class.getResourceAsStream(name);
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
