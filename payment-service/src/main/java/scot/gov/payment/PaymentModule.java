package scot.gov.payment;

import dagger.Module;
import dagger.Provides;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.payment.service.PaymentService;
import scot.gov.payment.service.worldpay.WorldpayPaymentService;
import scot.mygov.config.Configuration;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Module(injects = Payment.class)
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
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();
        Client client = builder.connectionPoolSize(10).build();
        String username = config.getWorldPay().getUsername();
        String password = config.getWorldPay().getPassword();
        ClientRequestFilter basicAuthFilter = new BasicAuthentication(username, password);
        client.register(basicAuthFilter);
        return client;
    }

    @Provides
    @Singleton
    WebTarget worldpayTarget(Client client, PaymentConfiguration config) {
        String url = config.getWorldPay().getUrl();
        UriBuilder uriBuilder = UriBuilder.fromUri(url);
        return client.target(uriBuilder);
    }
}