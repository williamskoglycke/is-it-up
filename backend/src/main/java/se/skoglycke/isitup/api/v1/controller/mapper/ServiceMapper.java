package se.skoglycke.isitup.api.v1.controller.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.skoglycke.isitup.api.v1.controller.beans.ServiceResponse;
import se.skoglycke.isitup.api.v1.controller.beans.ServicesResponse;
import se.skoglycke.isitup.api.v1.controller.beans.StatusResponse;
import se.skoglycke.isitup.domain.service.Service;
import se.skoglycke.isitup.domain.service.Services;
import se.skoglycke.isitup.domain.service.Status;

import static java.util.stream.Collectors.toList;

@Component
public class ServiceMapper {

    private final Integer timeOutInMillis;
    private final Integer refreshTime;

    public ServiceMapper(@Value("${is-it-up.ping-timeout}") Integer timeOutInMillis,
                         @Value("${is-it-up.refresh-interval}") Integer refreshTime) {
        this.timeOutInMillis = timeOutInMillis;
        this.refreshTime = refreshTime;
    }

    public ServicesResponse mapToServicesResponse(Services services) {
        return new ServicesResponse(
                timeOutInMillis,
                refreshTime,
                services.getServices().stream().map(this::mapToServiceResponse).collect(toList())
        );
    }

    public ServiceResponse mapToServiceResponse(Service service) {
        return new ServiceResponse(
                service.getStatus().map(this::mapToStatusResponse).orElse(null),
                service.getId(),
                service.getName(),
                service.getUrl(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }

    private StatusResponse mapToStatusResponse(Status status) {
        return switch (status) {
            case OK -> StatusResponse.OK;
            case FAIL -> StatusResponse.FAIL;
        };
    }
}
