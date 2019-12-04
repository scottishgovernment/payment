package scot.gov.payment.rest.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.payment.rest.PaymentResource;
import scot.gov.payment.rest.PaymentResourceListener;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;

import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.entries;

public class LoggingResourceListener implements PaymentResourceListener {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentResource.class);

    private static final String ORDER_CODE = "payment.ordercode";

    private static final String SUCCESS = "payment.success";

    private static final String REFERENCE_ID = "payment.referenceId";

    private static final String PAYMENT_URL = "payment.paymentURL";

    private static final String ERROR = "payment.error";


    @Override
    public void onPaymentRequest(PaymentRequest request) {
        Map<String, String> event = new HashMap<>();
        event.put(ORDER_CODE, request.getOrderCode());
        LOG.info("makePayment request {}", entries(event) );
    }

    @Override
    public void onPaymentResult(PaymentRequest request, PaymentResult result) {
        Map<String, String> event = new HashMap<>();
        event.put(SUCCESS, Boolean.toString(result.isSuccess()));
        event.put(ORDER_CODE, request.getOrderCode());
        if (result.isSuccess()) {
            event.put(REFERENCE_ID, result.getReferenceId());
            event.put(PAYMENT_URL, result.getPaymentUrl());
        } else {
            event.put(ERROR, result.getError());
        }
        LOG.info("makePayment {} result {}", request.getOrderCode(), entries(event) );
    }

    @Override
    public void onPaymentException(PaymentRequest request, PaymentException exception) {
        Map<String, String> event = new HashMap<>();
        event.put(ORDER_CODE, request.getOrderCode());
        LOG.error("makePayment {} exception {}", request.getOrderCode(), entries(event), exception);
    }
}
