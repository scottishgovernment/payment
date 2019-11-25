package scot.gov.payment.rest;

import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;

/**
 * Listen to events from the PaymentResource
 */
public interface PaymentResourceListener {

    void onPaymentRequest(PaymentRequest request);

    void onPaymentResult(PaymentRequest request, PaymentResult result);

    void onPaymentException(PaymentRequest request, PaymentException exception);
}
