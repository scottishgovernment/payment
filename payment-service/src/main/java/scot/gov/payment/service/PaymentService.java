package scot.gov.payment.service;

public interface PaymentService {

    PaymentResult makePayment(PaymentRequest request, String siteUrl) throws PaymentException;
}
