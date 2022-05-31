package scot.gov.payment;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class PaymentConfiguration {

    private int port = 9095;

    @Valid
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

        @NotEmpty
        private String url = "sdf";

        @NotEmpty
        private String merchantCode = "blah";

        @NotEmpty
        private String installationId = "sdf";

        @NotEmpty
        private String username ="asf";

        @NotEmpty
        private String password = "asdasd";

        int connectTimeoutSeconds = 1;

        int readTimeoutSeconds = 10;

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

        public String getInstallationId() {
            return installationId;
        }

        public void setInstallationId(String installationId) {
            this.installationId = installationId;
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

        public int getConnectTimeoutSeconds() {
            return connectTimeoutSeconds;
        }

        public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
            this.connectTimeoutSeconds = connectTimeoutSeconds;
        }

        public int getReadTimeoutSeconds() {
            return readTimeoutSeconds;
        }

        public void setReadTimeoutSeconds(int readTimeoutSeconds) {
            this.readTimeoutSeconds = readTimeoutSeconds;
        }
    }
}
