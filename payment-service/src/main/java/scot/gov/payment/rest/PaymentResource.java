package scot.gov.payment.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("payment")
public class PaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentResource.class);

    @Inject
    PaymentConfiguration configuration;

    @Inject
    PaymentService service;

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response makePayment(PaymentRequest request) {

        PaymentResult result;
        int status;

        try {
            result = service.makePayment(request);
            status = result.isSuccess() ? 200 : 500;
        } catch (PaymentException e) {
            LOG.error("Failed to process payemnt request payment: {}", request.toString(), e);
            result = PaymentResultBuilder.error("Failed to make payment").build();
            status = 500;
        }

        return Response
                .status(status)
                .entity(result)
                .build();

    }
}
