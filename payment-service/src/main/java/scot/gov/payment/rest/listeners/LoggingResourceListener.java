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

    private static final String ORDER_CODE = "payment.orderCode";

    private static final String SUCCESS = "payment.success";

    private static final String REFERENCE_ID = "payment.referenceId";

    private static final String PAYMENT_URL = "payment.paymentURL";


    @Override
    public void onPaymentRequest(PaymentRequest request) {
        Map<String, String> event = new HashMap<>();
        event.put(ORDER_CODE, request.getOrderCode());
        LOG.info("makePayment request {}", entries(event) );
    }

    @Override
    public void onPaymentResult(PaymentResult result) {
        Map<String, String> event = new HashMap<>();
        event.put(SUCCESS, result.getOrderCode());
        event.put(ORDER_CODE, result.getOrderCode());
        event.put(REFERENCE_ID, result.getReferenceId());
        event.put(PAYMENT_URL, result.getPaymentUrl());

        LOG.info("makePayment result {}", entries(event) );
    }

    @Override
    public void onPaymentException(PaymentRequest request, PaymentException exception) {
        Map<String, String> event = new HashMap<>();
        event.put(ORDER_CODE, request.getOrderCode());
        LOG.error("makePayment exception {}", entries(event), exception);
    }
}
