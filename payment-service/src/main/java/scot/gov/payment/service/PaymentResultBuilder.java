package scot.gov.payment.service;

public class PaymentResultBuilder {

    private boolean success;

    private String error;

    private String orderCode;

    private String referenceId;

    private String paymentUrl;

    public static PaymentResultBuilder error(String error) {
        PaymentResultBuilder builder = new PaymentResultBuilder();
        builder.success = false;
        builder.error = error;
        return builder;
    }

    public static PaymentResultBuilder success() {
        PaymentResultBuilder builder = new PaymentResultBuilder();
        builder.success = true;
        return builder;
    }

    public PaymentResultBuilder orderCode(String orderCode) {
        this.orderCode = orderCode;
        return this;
    }

    public PaymentResultBuilder referenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public PaymentResultBuilder paymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
        return this;
    }

    public PaymentResult build() {
        PaymentResult result = new PaymentResult();
        result.setSuccess(success);
        result.setError(error);
        result.setOrderCode(orderCode);
        result.setReferenceId(referenceId);
        result.setPaymentUrl(paymentUrl);
        return result;
    }
}
