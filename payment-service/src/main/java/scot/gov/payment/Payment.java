package scot.gov.payment;

import dagger.Component;
import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;

public class Payment {

    private static final Logger LOG = LoggerFactory.getLogger(Payment.class);

    @Inject
    public Payment() {
    }

    @Inject
    PaymentConfiguration config;

    @Inject
    PaymentApplication app;

    public static final void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        try {
            Payment payment = DaggerPayment_Main.create().main();
            payment.run();
        } catch (Throwable t) {
            LOG.error("Application failed", t);
            System.exit(1);
        }
    }

    public void run() {
        Server server = new Server();
        server.deploy(app);
        server.start(Undertow.builder().addHttpListener(config.getPort(), "::"));
        LOG.info("Listening on port {}", server.port());
    }

    public static class Server extends UndertowJaxrsServer {
        public int port() {
            InetSocketAddress address = (InetSocketAddress) server
                    .getListenerInfo()
                    .get(0)
                    .getAddress();
            return address.getPort();
        }
    }

    @Singleton
    @Component(modules = PaymentModule.class)
    interface Main {
        Payment main();
    }

}
