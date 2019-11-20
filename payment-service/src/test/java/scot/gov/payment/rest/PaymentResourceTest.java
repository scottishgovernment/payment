package scot.gov.payment.rest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.payment.service.*;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 20/11/2019.
 */
public class PaymentResourceTest {

    @Test
    public void sucessfullPaymentReturns200Response() throws PaymentException{
        // ARRANGE
        PaymentResource paymentResource = new PaymentResource();
        PaymentRequest request = anyRequest();
        PaymentResult result = sucess();
        paymentResource.service = serviceWithResponse(result);

        // ACT
        Response response = paymentResource.makePayment(request);

        // ASSERT
        assertEquals(response.getStatus(), 200);
        assertSame(response.getEntity(), result);
    }

    @Test
    public void unsucessfullPaymentReturns500Response() throws PaymentException{
        // ARRANGE
        PaymentResource paymentResource = new PaymentResource();
        PaymentRequest request = anyRequest();
        PaymentResult result = fail();
        paymentResource.service = serviceWithResponse(result);

        // ACT
        Response response = paymentResource.makePayment(request);

        // ASSERT
        assertEquals(response.getStatus(), 500);
        assertSame(response.getEntity(), result);
    }

    @Test
    public void paymentExceptionReturns500Response() throws PaymentException {

        // ARRANGE
        PaymentResource paymentResource = new PaymentResource();
        paymentResource.service = exceptionThrowingService();
        PaymentRequest request = anyRequest();

        // ACT
        Response response = paymentResource.makePayment(request);

        // ASSERT
        assertEquals(response.getStatus(), 500);
    }

    PaymentService serviceWithResponse(PaymentResult result) throws PaymentException {
        PaymentService service = mock(PaymentService.class);
        when(service.makePayment(any())).thenReturn(result);
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
        when(service.makePayment(any())).thenThrow(new PaymentException("fail"));
        return service;
    }

    PaymentRequest anyRequest() {
        return mock(PaymentRequest.class);
    }
}
