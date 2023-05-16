package scot.gov.payment.rest;

import jakarta.validation.ConstraintViolation;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;

import java.util.Set;

/**
 * Listen to events from the PaymentResource
 */
public interface PaymentResourceListener {

    void onPaymentRequest(PaymentRequest request);

    void onPaymentResult(PaymentRequest request, PaymentResult result);

    void onInvalidPaymentRequest(PaymentRequest request, Set<ConstraintViolation<PaymentRequest>> violations);

    void onPaymentException(PaymentRequest request, PaymentException exception);
}
