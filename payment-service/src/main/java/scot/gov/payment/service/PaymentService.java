package scot.gov.payment.service;

public interface PaymentService {

    void makePayment(PaymentRequest request, String siteUrl, PaymentCallback callback);
}
