package scot.gov.payment;

import javax.ws.rs.core.Response;

/**
 * Created by z418868 on 18/11/2019.
 */
public class PaymentConfiguration {

    private int port = 2020;

    private WorldPay worldPay;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public WorldPay getWorldPay() {
        return worldPay;
    }

    public void setWorldPay(WorldPay worldPay) {
        this.worldPay = worldPay;
    }

    public static class ResponseUrls {
        String successURL;

        String pendingURL;

        String errorUrl;

        String failureURL;

        String cancelUrl;

        public String getSuccessURL() {
            return successURL;
        }

        public void setSuccessURL(String successURL) {
            this.successURL = successURL;
        }

        public String getPendingURL() {
            return pendingURL;
        }

        public void setPendingURL(String pendingURL) {
            this.pendingURL = pendingURL;
        }

        public String getErrorUrl() {
            return errorUrl;
        }

        public void setErrorUrl(String errorUrl) {
            this.errorUrl = errorUrl;
        }

        public String getFailureURL() {
            return failureURL;
        }

        public void setFailureURL(String failureURL) {
            this.failureURL = failureURL;
        }

        public String getCancelUrl() {
            return cancelUrl;
        }

        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }
    }


    public static class WorldPay {

        private String url = "https://secure-test.worldpay.com/jsp/merchant/xml/paymentService.jsp";

        private String merchantCode = "GBSSCOTTISHGOVCGTEST";

        private String instalationId = "1365399";

        private String username = "GBSSCOTTISHGOVCGTEST";

        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMerchantCode() {
            return merchantCode;
        }

        public void setMerchantCode(String merchantCode) {
            this.merchantCode = merchantCode;
        }

        public String getInstalationId() {
            return instalationId;
        }

        public void setInstalationId(String instalationId) {
            this.instalationId = instalationId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
