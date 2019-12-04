package scot.gov.payment.rest;

import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isBlank;

/**
 * Rest endpoint for processing payments.
 */
@Path("service/payment")
public class PaymentResource {

    @Inject
    PaymentConfiguration configuration;

    @Inject
    PaymentService service;

    @Inject
    PaymentResourceListener listener;

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public void makePayment(
            PaymentRequest request,
            @HeaderParam("x-forwarded-host") String forwardedHost,
            @HeaderParam("x-forwarded-proto") String forwardedProtocol,
            @Suspended final AsyncResponse response) {

        listener.onPaymentRequest(request);
        String siteUrl = getSiteUrl(forwardedHost, forwardedProtocol);

        service.makePayment(request, siteUrl, new PaymentCallback() {
            @Override
            public void onPaymentResult(PaymentRequest request, PaymentResult result) {
                listener.onPaymentResult(request, result);
                int status = result.isSuccess() ? 200 : 400;
                response.resume(Response.status(status).entity(result).build());
            }

            @Override
            public void onPaymentException(PaymentRequest request, PaymentException e) {
                listener.onPaymentException(request, e);
                PaymentResult result = PaymentResultBuilder.error("Unexpected error trying to process payment").build();
                response.resume(Response
                        .status(500)
                        .entity(result)
                        .build());
            }
        });
    }

    String getSiteUrl(String forwardedHost, String forwardedProtocol) {
        if (isBlank(forwardedHost) || isBlank(forwardedProtocol)) {
            return "https://www.gov.scot/";
        }
        return String.format("%s://%s/", forwardedProtocol, forwardedHost);
    }

}
