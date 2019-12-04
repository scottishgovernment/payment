package scot.gov.payment.service;

/**
 * Callback used to asynchronously provide result of a payment request
 */
public interface PaymentCallback {

    void onPaymentResult(PaymentRequest request, PaymentResult result);

    void onPaymentException(PaymentRequest request, PaymentException exception);
}
