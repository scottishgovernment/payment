package scot.gov.payment.rest;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.payment.rest.listeners.CompoundResourceListener;
import scot.gov.payment.service.*;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 20/11/2019.
 */
public class PaymentResourceTest {

    @Test
    public void sucessfulPaymentRedirectsToPaymentUrl() throws PaymentException{
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        PaymentResult result = sucess();
        sut.service = serviceWithResponse(result);

        // ACT
        Response response = sut.makePayment("order", "description", "100", "www.gov2.scot", "http");

        // ASSERT
        assertEquals(301, response.getStatus());
        assertSame(response.getHeaderString("Location"), result.getPaymentUrl());
    }

    @Test
    public void usesDefaultSiteUrlIfRequestHeadersNull() throws PaymentException{
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        PaymentResult result = sucess();
        sut.service = serviceWithResponse(result);

        // ACT
        Response response = sut.makePayment("order", "description", "100", null, null);

        // ASSERT
        assertEquals(301, response.getStatus());
        verify(sut.service).makePayment(any(PaymentRequest.class), Mockito.startsWith("https://www.gov.scot"));
    }


    @Test
    public void unsucessfullPaymentReturns400Response() throws PaymentException{
        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        PaymentResult result = fail();
        sut.service = serviceWithResponse(result);

        // ACT
        Response response = sut.makePayment("order", "description", "100", "www.gov.scot", "https");

        // ASSERT
        assertEquals(400, response.getStatus());
    }

    @Test
    public void paymentExceptionReturns500Response() throws PaymentException {

        // ARRANGE
        PaymentResource sut = new PaymentResource();
        sut.listener = new CompoundResourceListener();
        sut.service = exceptionThrowingService();

        // ACT
        Response response = sut.makePayment("order", "description", "100", "www.gov.scot", "https");

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
}
