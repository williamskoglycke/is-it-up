package se.skoglycke.isitup.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import se.skoglycke.isitup.domain.service.AddServiceRequest;
import se.skoglycke.isitup.domain.service.Service;
import se.skoglycke.isitup.domain.service.Status;
import se.skoglycke.isitup.domain.service.UpdateServiceRequest;
import se.skoglycke.isitup.infrastructure.ServiceRepository;
import se.skoglycke.isitup.infrastructure.entities.ServiceEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class IsItUpServiceTest {

    @Autowired
    private IsItUpService isItUpService;

    @Autowired
    private ServiceRepository serviceRepository;

    @BeforeEach
    void setUp() {
        serviceRepository.deleteAll().block();
    }

    @Test
    void addService() {
        String name = "Aftonbladet";
        String url = "https://aftonbladet.se";

        isItUpService.addService(new AddServiceRequest(name, url)).block();

        List<ServiceEntity> serviceEntities = serviceRepository.findAll().collectList().block();
        assertThat(serviceEntities).hasSize(1);

        ServiceEntity serviceEntity = serviceEntities.get(0);
        assertThat(serviceEntity.getId()).isNotNull();
        assertThat(serviceEntity.getName()).isEqualTo(name);
        assertThat(serviceEntity.getUrl()).isEqualTo(url);
        assertThat(serviceEntity.getCreatedAt()).isNotNull();
        assertThat(serviceEntity.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateService() {
        Service savedService = isItUpService.addService(new AddServiceRequest("name", "url")).block();
        Long serviceId = savedService.getId();

        ServiceEntity serviceEntity = serviceRepository.findById(serviceId).block();
        assertThat(serviceEntity).isNotNull();
        assertThat(serviceEntity.getName()).isEqualTo("name");
        assertThat(serviceEntity.getUrl()).isEqualTo("url");

        String newName = "DN";
        String newUrl = "https://dn.se";

        isItUpService.updateService(new UpdateServiceRequest(serviceId, newName, newUrl)).block();

        ServiceEntity updatedEntity = serviceRepository.findById(serviceId).block();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.getName()).isEqualTo(newName);
        assertThat(updatedEntity.getUrl()).isEqualTo(newUrl);
    }

    @Test
    void deleteService() {
        Service savedService = isItUpService.addService(new AddServiceRequest("name", "url")).block();
        Long serviceId = savedService.getId();

        isItUpService.deleteService(serviceId).block();

        List<ServiceEntity> serviceEntities = serviceRepository.findAll().collectList().block();
        assertThat(serviceEntities).isEmpty();
    }
}
