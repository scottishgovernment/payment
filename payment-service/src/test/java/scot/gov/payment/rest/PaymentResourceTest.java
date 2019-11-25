package scot.gov.payment.rest;

import org.junit.Test;
import scot.gov.payment.rest.listeners.CompoundResourceListener;
import scot.gov.payment.service.*;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 20/11/2019.
 */
public class PaymentResourceTest {

    @Test
    public void sucessfullPaymentReturns200Response() throws PaymentException{
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        PaymentRequest request = anyRequest();
        PaymentResult result = sucess();
        sut.service = serviceWithResponse(result);

        // ACT
        Response response = sut.makePayment(request, "www.gov2.scot", "http");

        // ASSERT
        assertEquals(200, response.getStatus());
        assertSame(response.getEntity(), result);
        // make sure that it use the url derived from the request headers
        verify(sut.service).makePayment(request, "http://www.gov2.scot/");
    }

    @Test
    public void usesDefaultSiteUrlIfRequestHeadersNull() throws PaymentException{
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        PaymentRequest request = anyRequest();
        PaymentResult result = sucess();
        sut.service = serviceWithResponse(result);

        // ACT
        Response response = sut.makePayment(request, null, null);

        // ASSERT
        assertEquals(200, response.getStatus());
        assertSame(response.getEntity(), result);
        // uses a sensible default if the forwarding headers are not included
        verify(sut.service).makePayment(request, "https://www.gov.scot/");
    }


    @Test
    public void unsucessfullPaymentReturns400Response() throws PaymentException{
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        PaymentRequest request = anyRequest();
        PaymentResult result = fail();
        sut.service = serviceWithResponse(result);

        // ACT
        Response response = sut.makePayment(request, "www.gov.scot", "https");

        // ASSERT
        assertEquals(400, response.getStatus());
        assertSame(response.getEntity(), result);
    }

    @Test
    public void paymentExceptionReturns500Response() throws PaymentException {

        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        sut.service = exceptionThrowingService();
        PaymentRequest request = anyRequest();

        // ACT
        Response response = sut.makePayment(request, "www.gov.scot", "https");

        // ASSERT
        assertEquals(500, response.getStatus());
    }

    PaymentService serviceWithResponse(PaymentResult result) throws PaymentException {
        PaymentService service = mock(PaymentService.class);
        when(service.makePayment(any(), any())).thenReturn(result);
        return service;
    }

    PaymentResult sucess() {
        return PaymentResultBuilder.success().build();
    }

    PaymentResult fail() {
        return PaymentResultBuilder.error("fail").build();
    }

    PaymentService exceptionThrowingService() throws PaymentException {
        PaymentService service = mock(PaymentService.class);
        when(service.makePayment(any(), any())).thenThrow(new PaymentException("fail"));
        return service;
    }

    PaymentRequest anyRequest() {
        return mock(PaymentRequest.class);
    }
}
