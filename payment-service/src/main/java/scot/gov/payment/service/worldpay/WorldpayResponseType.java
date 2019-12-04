package scot.gov.payment.service.worldpay;

public enum WorldpayResponseType {

    AUTHORISED("successURL"),
    REDIRECTED("pendingURL"),
    REFUSED("failureURL"),
    CANCELLED("cancelURL"),
    ERROR("errorURL");

    private final String paramName;

    WorldpayResponseType(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
