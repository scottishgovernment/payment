package scot.gov.payment.rest;

import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isBlank;

/**
 * Rest endpoint for processing payments.
 */
@Path("payment")
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
    public Response makePayment(
            PaymentRequest request,
            @HeaderParam("x-forwarded-host") String forwardedHost,
            @HeaderParam("x-forwarded-proto") String forwardedProtocol) {

        listener.onPaymentRequest(request);

        try {
            String siteUrl = getSiteUrl(forwardedHost, forwardedProtocol);
            PaymentResult result = service.makePayment(request, siteUrl);
            listener.onPaymentResult(request, result);

            int status = result.isSuccess() ? 200 : 400;
            return Response.status(status).entity(result).build();
        } catch (PaymentException e) {
            listener.onPaymentException(request, e);
            PaymentResult result = PaymentResultBuilder.error("Unexpected error trying to process payment").build();
            return Response
                    .status(500)
                    .entity(result)
                    .build();
        }
    }

    String getSiteUrl(String forwardedHost, String forwardedProtocol) {
        if (isBlank(forwardedHost) || isBlank(forwardedProtocol)) {
            return "https://www.gov.scot/";
        }
        return String.format("%s://%s/", forwardedProtocol, forwardedHost);
    }

}
