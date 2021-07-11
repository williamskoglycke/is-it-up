package se.skoglycke.isitup.infrastructure.mapper;

import se.skoglycke.isitup.domain.service.Service;
import se.skoglycke.isitup.infrastructure.entities.ServiceEntity;

public final class ServiceMapper {

    private ServiceMapper() {
    }

    public static Service mapToService(ServiceEntity serviceEntity) {
        return new Service(serviceEntity.getId(), serviceEntity.getName(), serviceEntity.getUrl(), serviceEntity.getCreatedAt(), serviceEntity.getUpdatedAt());
    }

}
