package scot.gov.payment.rest;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.payment.rest.listeners.MetricName;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 25/11/2019.
 */
public class HealthCheckTest {

    @Test
    public void healthyIfNoErrors() {

        // ARRANGE
        HealthCheck sut = new HealthCheck();
        sut.metricRegistry = mockMetricsRegistry(0);

        // ACT
        Response actual = sut.health();

        // ASSERT
        Assert.assertEquals(200, actual.getStatus());
        JsonNode node = actual.readEntity(JsonNode.class);
        assertTrue(node.get("ok").asBoolean());
    }

    @Test
    public void unhealthyIfErrors() throws Exception {

        // ARRANGE
        HealthCheck sut = new HealthCheck();
        sut.metricRegistry = mockMetricsRegistry(100);

        // ACT
        Response actual = sut.health();

        // ASSERT
        Assert.assertEquals(503, actual.getStatus());
        JsonNode node = actual.readEntity(JsonNode.class);
        assertFalse(node.get("ok").asBoolean());
    }

    private MetricRegistry mockMetricsRegistry(double exceptionRate) {

        MetricRegistry registry = mock(MetricRegistry.class);

        SortedMap<String, Meter> meters = new TreeMap<>();
        Meter errorRate = mock(Meter.class);
        when(errorRate.getFiveMinuteRate()).thenReturn(exceptionRate);
        meters.put(MetricName.EXCEPTION_RATE.name(PaymentResource.class), errorRate);
        when(registry.getMeters()).thenReturn(meters);
        when(registry.getMeters(Mockito.any())).thenReturn(meters);

        SortedMap<String, Counter> counters = new TreeMap<>();
        Counter errorCounter = mock(Counter.class);
        when(errorCounter.getCount()).thenReturn(0L);
        counters.put(MetricName.ERRORS.name(PaymentResource.class), errorCounter);
        when(registry.getCounters()).thenReturn(counters);
        when(registry.getCounters(Mockito.any())).thenReturn(counters);

        return registry;
    }

}
