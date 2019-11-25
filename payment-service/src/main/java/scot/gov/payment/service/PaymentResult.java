package scot.gov.payment.service;

public class PaymentResult {

    private boolean success;

    private String error;

    private String orderCode;

    private String referenceId;

    private String paymentUrl;

    public boolean isSuccess() {
        return success;
    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    protected void setError(String error) {
        this.error = error;
    }

    public String getOrderCode() {
        return orderCode;
    }

    protected void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getReferenceId() {
        return referenceId;
    }

    protected void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "success=" + success +
                ", error='" + error + '\'' +
                ", orderCode='" + orderCode + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", paymentUrl='" + paymentUrl + '\'' +
                '}';
    }
}
