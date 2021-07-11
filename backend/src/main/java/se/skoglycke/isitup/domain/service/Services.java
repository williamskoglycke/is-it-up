package se.skoglycke.isitup.domain.service;

import se.skoglycke.isitup.util.ValueObject;

import java.util.List;

public class Services extends ValueObject {
    private final List<Service> services;

    public Services(List<Service> services) {
        this.services = services;
    }

    public List<Service> getServices() {
        return services;
    }
}
