package se.skoglycke.isitup.api.v1.controller.beans;

import se.skoglycke.isitup.util.ValueObject;

import java.util.List;

public class ServicesResponse extends ValueObject {

    private final Integer timeOut;
    private final Integer timeBetweenPollsInMillis;
    private final List<ServiceResponse> services;

    public ServicesResponse(Integer timeOut, Integer timeBetweenPollsInMillis, List<ServiceResponse> services) {
        this.timeOut = timeOut;
        this.timeBetweenPollsInMillis = timeBetweenPollsInMillis;
        this.services = services;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public Integer getTimeBetweenPollsInMillis() {
        return timeBetweenPollsInMillis;
    }

    public List<ServiceResponse> getServices() {
        return services;
    }
}
