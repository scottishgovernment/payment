package scot.gov.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

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
        private String url;

        @NotEmpty
        private String merchantCode;

        @NotEmpty
        private String installationId;

        @NotEmpty
        private String username;

        @NotEmpty
        private String password;

        int connectTimeoutSeconds = 1;

        int connectionTTLSeconds = 300;

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

        public int getConnectionTTLSeconds() {
            return connectionTTLSeconds;
        }

        public void setConnectionTTLSeconds(int connectionTTLSeconds) {
            this.connectionTTLSeconds = connectionTTLSeconds;
        }

        public int getReadTimeoutSeconds() {
            return readTimeoutSeconds;
        }

        public void setReadTimeoutSeconds(int readTimeoutSeconds) {
            this.readTimeoutSeconds = readTimeoutSeconds;
        }
    }
}
