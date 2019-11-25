package scot.gov.payment.rest.listeners;

import com.codahale.metrics.MetricRegistry;

public enum MetricName {

    // count of all requests
    REQUESTS("requests"),

    // count of unexpected exceptions
    EXCEPTIONS("exceptions"),

    // countof errors returned by the payment provider
    ERRORS("errors"),

    // rates for the above
    REQUEST_RATE("requestRate"),

    ERROR_RATE("errorRate"),

    EXCEPTION_RATE("exceptionRate");

    private final String name;

    private MetricName(String metricName) {
        this.name = metricName;
    }

    public String getName() {
        return name;
    }

    public String name(Class clazz) {
        return MetricRegistry.name(clazz, name);
    }

}