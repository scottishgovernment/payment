package scot.gov.payment.rest;

import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.Response;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import scot.gov.payment.rest.listeners.CompoundResourceListener;
import scot.gov.payment.service.PaymentCallback;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;
import scot.gov.payment.service.PaymentResultBuilder;
import scot.gov.payment.service.PaymentService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentResourceTest {

    @Test
    public void successfulPaymentReturns200Response() {

        PaymentResource sut = new PaymentResource();
        PaymentResult result = success();
        CapturingPaymentService service = new CapturingPaymentService(result);
        sut.service = service;
        sut.listener = new CompoundResourceListener();
        ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        when(asyncResponse.resume(response.capture())).thenReturn(true);

        // ACT
        sut.makePayment(anyRequest(), "www2.gov.scot", "https", asyncResponse);

        // ASSERT
        Response value = response.getValue();
        assertEquals(200, value.getStatus());
        assertSame(value.getEntity(), result);
        assertEquals("https://www2.gov.scot/", service.siteUrl);
    }

    @Test
    public void usesDefaultSiteUrlIfHosrtHeaderIsNull() {
        PaymentResource sut = new PaymentResource();
        PaymentResult result = success();
        CapturingPaymentService service = new CapturingPaymentService(result);
        sut.service = service;
        sut.listener = new CompoundResourceListener();
        ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        when(asyncResponse.resume(response.capture())).thenReturn(true);

        // ACT
        sut.makePayment(anyRequest(), null, "https", asyncResponse);

        // ASSERT
        Response value = response.getValue();
        assertEquals(200, value.getStatus());
        assertSame(value.getEntity(), result);
        assertEquals("https://www.gov.scot/", service.siteUrl);
    }

    @Test
    public void usesDefaultSiteUrlIfProtoHeaderIsNull() {
        PaymentResource sut = new PaymentResource();
        PaymentResult result = success();
        CapturingPaymentService service = new CapturingPaymentService(result);
        sut.service = service;
        sut.listener = new CompoundResourceListener();
        ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        when(asyncResponse.resume(response.capture())).thenReturn(true);

        // ACT
        sut.makePayment(anyRequest(), "wwww.gov.scot", null, asyncResponse);

        // ASSERT
        Response value = response.getValue();
        assertEquals(200, value.getStatus());
        assertSame(value.getEntity(), result);
        assertEquals("https://www.gov.scot/", service.siteUrl);
    }

    @Test
    public void unsucessfullPaymentReturns400Response() {
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        PaymentResult result = fail();
        CapturingPaymentService service = new CapturingPaymentService(result);
        sut.service = service;
        sut.listener = new CompoundResourceListener();
        ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        when(asyncResponse.resume(response.capture())).thenReturn(true);

        // ACT
        sut.makePayment(anyRequest(), "wwww.gov.scot", null, asyncResponse);

        // ASSERT
        Response value = response.getValue();
        assertEquals(400, value.getStatus());
        assertSame(value.getEntity(), result);
        assertEquals("https://www.gov.scot/", service.siteUrl);

    }

    @Test
    public void paymentExceptionReturns500Response() {
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        PaymentException exception = new PaymentException("arg");
        ExceptionPaymentService service = new ExceptionPaymentService(exception);
        sut.service = service;
        sut.listener = new CompoundResourceListener();
        ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        when(asyncResponse.resume(response.capture())).thenReturn(true);

        // ACT
        sut.makePayment(anyRequest(), "wwww.gov.scot", null, asyncResponse);

        // ASSERT
        Response value = response.getValue();
        assertEquals(500, value.getStatus());
    }

    @Test
    public void invalidPaymentExceptionReturns400Response() {
        PaymentResource sut = new PaymentResource();
        PaymentResult result = success();
        CapturingPaymentService service = new CapturingPaymentService(result);
        sut.service = service;
        sut.listener = new CompoundResourceListener();
        ArgumentCaptor<Response> response = ArgumentCaptor.forClass(Response.class);
        AsyncResponse asyncResponse = mock(AsyncResponse.class);
        when(asyncResponse.resume(response.capture())).thenReturn(true);

        // ACT
        sut.makePayment(invalidRequest(), "wwww.gov.scot", null, asyncResponse);

        // ASSERT
        Response value = response.getValue();
        assertEquals(400, value.getStatus());
    }

    PaymentResult success() {
        return PaymentResultBuilder.success().build();
    }

    PaymentResult fail() {
        return PaymentResultBuilder.error("fail").build();
    }

    PaymentRequest anyRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount("1.00");
        request.setDescription("description");
        request.setEmailAddress("test@email.com");
        request.setOrderCode("ordercode");
        return request;
    }

    PaymentRequest invalidRequest() {
        PaymentRequest request = anyRequest();
        request.setEmailAddress("invalid");
        return request;
    }
    static class CapturingPaymentService implements PaymentService {

        PaymentResult result;

        String siteUrl;

        CapturingPaymentService(PaymentResult result) {
            this.result = result;
        }

        @Override
        public void makePayment(PaymentRequest request, String siteUrl, PaymentCallback callback) {
            this.siteUrl = siteUrl;
            callback.onPaymentResult(request, result);
        }
    }

    static class ExceptionPaymentService implements PaymentService {

        PaymentException exception;

        ExceptionPaymentService(PaymentException exception) {
            this.exception = exception;
        }

        @Override
        public void makePayment(PaymentRequest request, String siteUrl, PaymentCallback callback) {
            callback.onPaymentException(request, exception);
        }
    }

}
