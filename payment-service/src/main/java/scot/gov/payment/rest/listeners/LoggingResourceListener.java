package scot.gov.payment.rest.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.payment.rest.PaymentResource;
import scot.gov.payment.rest.PaymentResourceListener;
import scot.gov.payment.service.InvalidPaymentResponse;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;

import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static net.logstash.logback.argument.StructuredArguments.entries;

public class LoggingResourceListener implements PaymentResourceListener {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentResource.class);

    private static final String ORDER_CODE = "payment.ordercode";

    private static final String AMOUNT = "payment.amount";

    private static final String DESCRIPTION = "payment.description";

    private static final String SUCCESS = "payment.success";

    private static final String EMAIL_ADDRESS = "payment.emailaddress";

    private static final String REFERENCE_ID = "payment.referenceId";

    private static final String PAYMENT_URL = "payment.paymentURL";

    private static final String ERROR = "payment.error";


    @Override
    public void onPaymentRequest(PaymentRequest request) {
        Map<String, String> event = eventMap(request);
        LOG.info("makePayment request {}", entries(event) );
    }

    @Override
    public void onPaymentResult(PaymentRequest request, PaymentResult result) {
        Map<String, String> event = eventMap(request);
        event.put(SUCCESS, Boolean.toString(result.isSuccess()));
        if (result.isSuccess()) {
            event.put(REFERENCE_ID, result.getReferenceId());
            event.put(PAYMENT_URL, result.getPaymentUrl());
        } else {
            event.put(ERROR, result.getError());
        }
        LOG.info("makePayment result success={} {} ", result.isSuccess(), entries(event) );
    }

    @Override
    public void onInvalidPaymentRequest(PaymentRequest request, Set<ConstraintViolation<PaymentRequest>> violations) {
        Map<String, String> event = eventMap(request);
        String violationsString = violations.stream()
                .map(InvalidPaymentResponse::violation)
                .map(InvalidPaymentResponse.Violation::toString)
                .collect(joining(","));
        event.put("violations", violationsString);
        LOG.warn("makePayment invalidrequest {}", entries(event));
    }

    @Override
    public void onPaymentException(PaymentRequest request, PaymentException exception) {
        LOG.error("makePayment exception {}", entries(eventMap(request)), exception);
    }

    Map<String, String> eventMap(PaymentRequest request) {
        Map<String, String> event = new HashMap<>();
        event.put(ORDER_CODE, request.getOrderCode());
        event.put(AMOUNT, request.getAmount());
        event.put(DESCRIPTION, request.getDescription());
        event.put(EMAIL_ADDRESS, request.getEmailAddress());
        return event;
    }
}
