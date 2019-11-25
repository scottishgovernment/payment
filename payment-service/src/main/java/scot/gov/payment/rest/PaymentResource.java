package scot.gov.payment.rest;

import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Form;
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
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_FORM_URLENCODED })
    public Response makePayment(
            @FormParam("ordercode") String ordercode,
            @FormParam("description") String description,
            @FormParam("amount") String amount,
            @HeaderParam("x-forwarded-host") String forwardedHost,
            @HeaderParam("x-forwarded-proto") String forwardedProtocol) {

        PaymentRequest request = toRequest(ordercode, description, amount);
        listener.onPaymentRequest(request);

        try {
            String siteUrl = getSiteUrl(forwardedHost, forwardedProtocol);
            PaymentResult result = service.makePayment(request, siteUrl);
            listener.onPaymentResult(request, result);
            return toRespone(request, result);
        } catch (PaymentException e) {
            listener.onPaymentException(request, e);
            return Response.status(500).entity(formForException(request)).build();
        }
    }

    PaymentRequest toRequest(String ordercode, String description, String amount) {
        PaymentRequest request = new PaymentRequest();
        request.setOrderCode(ordercode);
        request.setDescription(description);
        request.setAmount(amount);
        return request;
    }

    String getSiteUrl(String forwardedHost, String forwardedProtocol) {
        return isNoneBlank(forwardedHost, forwardedProtocol)
                ? String.format("%s://%s/", forwardedProtocol, forwardedHost)
                : "https://www.gov.scot/";
    }

    Response toRespone(PaymentRequest request, PaymentResult result) {
        // if the payment was successful then redirect to the payment url, otherwise return a 400 error
        return result.isSuccess()
                // redirect to the payment page
                ? Response.status(301)
                    .header("Location", result.getPaymentUrl())
                    .build()

                // return a 400 error
                : Response.status(400)
                    .entity(formForUnsuccessfulResult(request, result))
                    .build();
    }

    Form formForUnsuccessfulResult(PaymentRequest request, PaymentResult result) {
        Form form = formForRequest(request);
        form.param("message", result.isSuccess() ? "" : result.getError());
        return form;
    }

    Form formForException(PaymentRequest request) {
        Form form = formForRequest(request);
        form.param("message", "Unexpected error processing payment");
        return form;
    }

    Form formForRequest(PaymentRequest request) {
        Form form = new Form();
        form.param("ordercode", request.getOrderCode());
        form.param("description", request.getDescription());
        form.param("amount", request.getAmount());
        return form;
    }

}
