package se.skoglycke.isitup.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.skoglycke.isitup.domain.service.AddServiceRequest;
import se.skoglycke.isitup.domain.service.Service;
import se.skoglycke.isitup.domain.service.Services;
import se.skoglycke.isitup.domain.service.Status;
import se.skoglycke.isitup.domain.service.UpdateServiceRequest;
import se.skoglycke.isitup.errors.AlreadyInDatabaseException;
import se.skoglycke.isitup.errors.ServiceNotFoundException;
import se.skoglycke.isitup.infrastructure.ServiceRepository;
import se.skoglycke.isitup.infrastructure.entities.ServiceEntity;
import se.skoglycke.isitup.infrastructure.mapper.ServiceMapper;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@Component
public class IsItUpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsItUpService.class);

    private final ConcurrentMap<Long, Service> serviceContext;
    private final ServiceRepository serviceRepository;

    public IsItUpService(ConcurrentMap<Long, Service> serviceContext, ServiceRepository serviceRepository) {
        this.serviceContext = serviceContext;
        this.serviceRepository = serviceRepository;
    }

    public Stream<Services> getAllStatuses() {
        return Stream.generate(() -> {
            LOGGER.info("Getting statuses from context");
            return new Services(new ArrayList<>(serviceContext.values()));
        });
    }

    public Flux<Service> getAllServices() {
        LOGGER.info("Getting all services from DB");
        return serviceRepository.findAll().map(ServiceMapper::mapToService);
    }

    public Mono<Service> getServiceById(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .map(ServiceMapper::mapToService)
                .switchIfEmpty(getServiceNotFoundError(serviceId));
    }

    public Mono<Service> addService(AddServiceRequest addServiceRequest) {
        String name = addServiceRequest.name();
        String url = addServiceRequest.url();

        return serviceRepository.findByNameAndUrl(name, url)
                .flatMap(entity -> {
                    if (entity != null) {
                        return Mono.error(new AlreadyInDatabaseException("Service with name: %s and url: %s is already registered".formatted(name, url)));
                    }
                    return Mono.just(entity);
                })
                .switchIfEmpty(serviceRepository.save(getServiceEntity(name, url)))
                .map(ServiceMapper::mapToService)
                .doOnNext(service -> serviceContext.put(service.getId(), service.setStatus(Status.FAIL)));
    }

    private ServiceEntity getServiceEntity(String name, String url) {
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName(name);
        serviceEntity.setUrl(url);
        return serviceEntity;
    }

    public Mono<Service> updateService(UpdateServiceRequest request) {
        return serviceRepository.findById(request.id())
                .switchIfEmpty(getServiceNotFoundError(request.id()))
                .flatMap(serviceEntity -> serviceRepository.updateService(request.id(), request.name(), request.url())
                        .flatMap(v -> serviceRepository.findById(request.id())
                                .map(ServiceMapper::mapToService)
                        )
                );
    }

    public Mono<Void> deleteService(Long serviceId) {
        return getServiceById(serviceId) // Check if service exist
                .flatMap(service -> {
                    serviceContext.remove(serviceId);
                    return serviceRepository.deleteById(serviceId);
                });
    }

    private <T> Mono<T> getServiceNotFoundError(Long serviceId) {
        return Mono.error(new ServiceNotFoundException("Could not find service with id: " + serviceId));
    }
}
