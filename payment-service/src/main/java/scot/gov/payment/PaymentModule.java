package scot.gov.payment;

import com.codahale.metrics.MetricRegistry;
import dagger.Module;
import dagger.Provides;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.payment.rest.listeners.CompoundResourceListener;
import scot.gov.payment.rest.listeners.LoggingResourceListener;
import scot.gov.payment.rest.listeners.MetricCollectingResourceListener;
import scot.gov.payment.rest.PaymentResourceListener;
import scot.gov.payment.service.PaymentService;
import scot.gov.payment.service.worldpay.WorldpayDocumentParser;
import scot.gov.payment.service.worldpay.WorldpayPaymentService;
import scot.gov.payment.service.worldpay.responseurls.PaymentUrlFormatter;
import scot.mygov.config.Configuration;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

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
    @Singleton
    Client client(PaymentConfiguration config) {
        int connectTimeout = config.getWorldpay().getConnectTimeoutSeconds();
        int readTimeout = config.getWorldpay().getReadTimeoutSeconds();
        ResteasyClientBuilder builder =
                (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder()
                    .connectTimeout(connectTimeout, SECONDS)
                    .readTimeout(readTimeout, SECONDS);
        Client client = builder.connectionPoolSize(10).build();
        String username = config.getWorldpay().getUsername();
        String password = config.getWorldpay().getPassword();
        ClientRequestFilter basicAuthFilter = new BasicAuthentication(username, password);
        client.register(basicAuthFilter);
        return client;
    }

    @Provides
    @Singleton
    WebTarget worldpayTarget(Client client, PaymentConfiguration config) {
        String url = config.getWorldpay().getUrl();
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