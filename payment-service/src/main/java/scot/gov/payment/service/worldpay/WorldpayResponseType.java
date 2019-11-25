package scot.gov.payment.service.worldpay;

public enum WorldpayResponseType {

    AUTHORISED("successURL"),
    SHOPPER_REDIRECTED("pendingURL"),
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
