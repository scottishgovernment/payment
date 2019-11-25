package scot.gov.payment.rest;

import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

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

            listener.onPaymentResult(result);
            return Response
                    .status(result.isSuccess() ? 200 : 400)
                    .entity(result)
                    .build();
        } catch (PaymentException e) {
            listener.onPaymentException(request, e);
            return Response
                    .status(500)
                    .entity("Unexpected error processing payment")
                    .build();
        }
    }

    String getSiteUrl(String forwardedHost, String forwardedProtocol) {
        return isNoneBlank(forwardedHost, forwardedProtocol)
            ? String.format("%s://%s/", forwardedProtocol, forwardedHost)
            : "https://www.gov.scot/";
    }

}
