package scot.gov.payment;

import com.codahale.metrics.MetricRegistry;
import dagger.Module;
import dagger.Provides;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.payment.rest.PaymentResourceListener;
import scot.gov.payment.rest.listeners.CompoundResourceListener;
import scot.gov.payment.rest.listeners.LoggingResourceListener;
import scot.gov.payment.rest.listeners.MetricCollectingResourceListener;
import scot.gov.payment.service.PaymentService;
import scot.gov.payment.service.worldpay.WorldpayDocumentParser;
import scot.gov.payment.service.worldpay.WorldpayPaymentService;
import scot.gov.payment.service.worldpay.responseurls.PaymentUrlFormatter;
import scot.mygov.config.Configuration;

import javax.inject.Singleton;

import static java.util.concurrent.TimeUnit.SECONDS;

@Module
public class PaymentModule {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentModule.class);

    private static final String APP_NAME = "payment";

    @Provides
    @Singleton
    PaymentConfiguration configuration() {
        Configuration<PaymentConfiguration> configuration = Configuration
                .load(new PaymentConfiguration(), APP_NAME)
                .validate();
        LOG.info("{}", configuration);
        return configuration.getConfiguration();
    }

    @Provides
    @Singleton
    PaymentService paymentService(WorldpayPaymentService service) {
        return service;
    }

    @Provides
    PaymentConfiguration.Worldpay worldpayConfig(PaymentConfiguration config) {
        return config.getWorldpay();
    }

    @Provides
    @Singleton
    Client client(PaymentConfiguration.Worldpay config) {
        ResteasyClientBuilder builder =
                (ResteasyClientBuilder) ClientBuilder.newBuilder();
        Client client = builder
                .connectionPoolSize(10)
                .connectionTTL(config.getConnectionTTLSeconds(), SECONDS)
                .connectTimeout(config.getConnectTimeoutSeconds(), SECONDS)
                .readTimeout(config.getReadTimeoutSeconds(), SECONDS)
                .build();
        ClientRequestFilter basicAuthFilter = new BasicAuthentication(
                config.getUsername(),
                config.getPassword());
        client.register(basicAuthFilter);
        return client;
    }

    @Provides
    @Singleton
    WebTarget worldpayTarget(Client client, PaymentConfiguration.Worldpay config) {
        String url = config.getUrl();
        UriBuilder uriBuilder = UriBuilder.fromUri(url);
        return client.target(uriBuilder);
    }

    @Provides
    @Singleton
    MetricRegistry metricsRegistry() {
        return new MetricRegistry();
    }

    @Provides
    @Singleton
    WorldpayDocumentParser worldpayDocumentParser() {
        return new WorldpayDocumentParser();
    }

    @Provides
    @Singleton
    PaymentUrlFormatter paymentUrlFormatter() {
        return new PaymentUrlFormatter();
    }

    @Provides
    @Singleton
    PaymentResourceListener resourceListener(MetricRegistry metricRegistry) {
        return new CompoundResourceListener(
                new LoggingResourceListener(),
                new MetricCollectingResourceListener(metricRegistry));
    }
}