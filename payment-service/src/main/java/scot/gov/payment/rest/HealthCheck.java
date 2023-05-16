package scot.gov.payment.rest;

import com.codahale.metrics.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.inject.Inject;
import java.util.Map;

import static scot.gov.payment.rest.listeners.MetricName.EXCEPTION_RATE;

/**
 * Healthcheck endpoint for the payment rest service.
 *
 * Logic is based on metrics collected by the PaymentResource class.  If there have been errors in the last 5
 * minutes then the service will be unhealthy.
 */
@Path("health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthCheck {

    private static final double EPSILON = 0.00001;

    @Inject
    HealthCheck() {
        // Default constructor
    }

    @Inject
    MetricRegistry metricRegistry;

    @GET
    public Response health() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode result = factory.objectNode();
        ArrayNode errors = factory.arrayNode();
        ObjectNode data = factory.objectNode();
        addMetricsInfo(errors, data);

        boolean ok = errors.size() == 0;
        result.put("ok", ok);
        result.set("errors", errors);
        result.set("data", data);

        int status = ok ? 200 : 503;
        return Response.status(status)
                .entity(result)
                .build();
    }

    private void addMetricsInfo(ArrayNode errors, ObjectNode data) {

        Meter exceptionRate = metricRegistry.getMeters().get(EXCEPTION_RATE.name(PaymentResource.class));
        if (exceptionRate.getFiveMinuteRate() > EPSILON) {
            errors.add("payment exceptions in the last 5 minutes");
        }

        // collect all of the metrics and add them to the data
        MetricFilter filter = paymentResourceMetricFilter();
        for (Map.Entry<String, Meter> entry : metricRegistry.getMeters(filter).entrySet()) {
            data.put(entry.getKey(), formatMeter(entry.getValue()));
        }

        for (Map.Entry<String, Counter> entry : metricRegistry.getCounters(filter).entrySet()) {
            data.put(entry.getKey(), entry.getValue().getCount());
        }
    }

    private String formatMeter(Metered m) {
        return String.format("count: %d, meanRate: %.02f, oneMinRate: %.02f, fiveMinRate: %.02f, fifteenMinRate: %.02f",
                m.getCount(),
                m.getMeanRate(),
                m.getOneMinuteRate(),
                m.getFiveMinuteRate(),
                m.getFifteenMinuteRate() );
    }

    private MetricFilter paymentResourceMetricFilter() {
        return (name, metric) -> name.startsWith(PaymentResource.class.getName());
    }
}