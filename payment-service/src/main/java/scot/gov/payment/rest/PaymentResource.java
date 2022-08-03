package scot.gov.payment.rest;

import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.*;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Rest endpoint for processing payments.
 */
@Path("service/payment")
public class PaymentResource {

    @Inject
    PaymentResource() {
        // Default constructor
    }

    @Inject
    PaymentConfiguration configuration;

    @Inject
    PaymentService service;

    @Inject
    PaymentResourceListener listener;

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public void makePayment(
            PaymentRequest request,
            @HeaderParam("x-forwarded-host") String forwardedHost,
            @HeaderParam("x-forwarded-proto") String forwardedProtocol,
            @Suspended final AsyncResponse response) {

        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            listener.onInvalidPaymentRequest(request, violations);
            InvalidPaymentResponse invalidPayment = InvalidPaymentResponse.invalidResponse(violations);
            response.resume(Response.status(400).entity(invalidPayment).build());
            return;
        }

        listener.onPaymentRequest(request);
        String siteUrl = getSiteUrl(forwardedHost, forwardedProtocol);

        if (validatePaymentRequest(request)) {
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
            return;
        }
        
        PaymentResult result = new PaymentResult(
                "Invalid payment request",
                request.getOrderCode(),
                false,
                String.format("%spayment/error/?orderCode=%s", siteUrl, request.getOrderCode()));
        response.resume(Response.status(400).entity(result).build());
    }

    String getSiteUrl(String forwardedHost, String forwardedProtocol) {
        if (isBlank(forwardedHost) || isBlank(forwardedProtocol)) {
            return "https://www.gov.scot/";
        }
        return String.format("%s://%s/", forwardedProtocol, forwardedHost);
    }

    boolean validatePaymentRequest(PaymentRequest request) {
        // Order code < 64 characters with no spaces allowed
        // Amount must be between 0.01 and 5000.00 inclusive
        String orderCode = request.getOrderCode();
        if (orderCode.length() > 64 || orderCode.contains(" ")) {
            return false;
        }
        try {
            double amount = Double.parseDouble(request.getAmount());
            if (amount < 0.01 || amount > 5000.00) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
