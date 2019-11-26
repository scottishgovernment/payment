package scot.gov.payment;

import javax.validation.constraints.NotEmpty;

public class PaymentConfiguration {

    private int port = 9095;

    private Worldpay worldpay = new Worldpay();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setWorldpay(Worldpay worldpay) {
        this.worldpay = worldpay;
    }

    public Worldpay getWorldpay() {
        return worldpay;
    }

    public static class Worldpay {

        private String url = "https://secure-test.worldpay.com/jsp/merchant/xml/paymentService.jsp";

        @NotEmpty
        private String merchantCode;

        @NotEmpty
        private String instalationId;

        @NotEmpty
        private String username;

        @NotEmpty
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
