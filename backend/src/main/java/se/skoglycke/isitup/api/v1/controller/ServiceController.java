package se.skoglycke.isitup.api.v1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.skoglycke.isitup.api.error.ErrorResponse;
import se.skoglycke.isitup.api.v1.controller.beans.ServiceResponse;
import se.skoglycke.isitup.api.v1.controller.mapper.ServiceMapper;
import se.skoglycke.isitup.domain.IsItUpService;
import se.skoglycke.isitup.domain.service.AddServiceRequest;
import se.skoglycke.isitup.domain.service.UpdateServiceRequest;
import se.skoglycke.isitup.errors.AlreadyInDatabaseException;
import se.skoglycke.isitup.errors.ServiceNotFoundException;

import java.net.URI;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static se.skoglycke.isitup.api.BaseUrl.V1;
import static se.skoglycke.isitup.api.error.ErrorResponse.badRequest;

@CrossOrigin
@RestController
@RequestMapping(ServiceController.SERVICES_ENDPOINT)
public class ServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

    static final String SERVICES_ENDPOINT = V1 + "/services";

    private final IsItUpService isItUpService;
    private final ServiceMapper serviceMapper;

    public ServiceController(IsItUpService isItUpService, ServiceMapper serviceMapper) {
        this.isItUpService = isItUpService;
        this.serviceMapper = serviceMapper;
    }

    @GetMapping
    public Flux<ServiceResponse> getAllServices() {
        return isItUpService.getAllServices()
                .map(serviceMapper::mapToServiceResponse);
    }

    @GetMapping("/{serviceId}")
    public Mono<ServiceResponse> getServiceById(@PathVariable Long serviceId) {
        return isItUpService.getServiceById(serviceId)
                .map(serviceMapper::mapToServiceResponse);
    }

    @PostMapping
    public Mono<ResponseEntity<ServiceResponse>> addNewService(@RequestParam String name,
                                                               @RequestParam String url) {
        LOGGER.info("Adding service {} with url {}", name, url);
        AddServiceRequest addServiceRequest = new AddServiceRequest(name, url);
        return isItUpService.addService(addServiceRequest)
                .map(serviceMapper::mapToServiceResponse)
                .map(serviceResponse -> ResponseEntity
                        .created(URI.create(SERVICES_ENDPOINT + "/" + serviceResponse.getId()))
                        .body(serviceResponse)
                );
    }

    @PutMapping("/{serviceId}")
    public Mono<ResponseEntity> updateService(@PathVariable Long serviceId,
                                              @RequestParam String name,
                                              @RequestParam String url) {
        LOGGER.info("Updating service(id:{}) to name {} and url {}", serviceId, name, url);
        UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest(serviceId, name, url);
        return isItUpService.updateService(updateServiceRequest)
                .map(serviceMapper::mapToServiceResponse)
                .map(serviceResponse -> ResponseEntity
                        .created(URI.create(SERVICES_ENDPOINT + "/" + serviceResponse.getId()))
                        .body(serviceResponse));
    }

    @DeleteMapping("/{serviceId}")
    public Mono<ResponseEntity> deleteService(@PathVariable Long serviceId) {
        LOGGER.info("Deleting service with id: {}", serviceId);
        return isItUpService.deleteService(serviceId)
                .map(v -> ResponseEntity.noContent().build());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleServiceNotFoundException(ServiceNotFoundException e) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(badRequest(4001, e.getMessage()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(AlreadyInDatabaseException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyInDbException(AlreadyInDatabaseException e) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(badRequest(4002, e.getMessage()));
    }
}
