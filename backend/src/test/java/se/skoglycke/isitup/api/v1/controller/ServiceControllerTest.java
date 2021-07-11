package se.skoglycke.isitup.api.v1.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.skoglycke.isitup.api.error.ErrorResponse;
import se.skoglycke.isitup.api.v1.controller.beans.ServiceResponse;
import se.skoglycke.isitup.api.v1.controller.beans.StatusResponse;
import se.skoglycke.isitup.api.v1.controller.mapper.ServiceMapper;
import se.skoglycke.isitup.domain.IsItUpService;
import se.skoglycke.isitup.domain.service.AddServiceRequest;
import se.skoglycke.isitup.domain.service.UpdateServiceRequest;
import se.skoglycke.isitup.errors.AlreadyInDatabaseException;
import se.skoglycke.isitup.errors.ServiceNotFoundException;
import se.skoglycke.isitup.util.ControllerTestConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.skoglycke.isitup.util.TestHelper.getService;

@WebFluxTest(ServiceController.class)
@Import(ControllerTestConfiguration.class)
class ServiceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IsItUpService isItUpService;

    @Test
    void getAllServices() {
        Mockito
                .when(isItUpService.getAllServices())
                .thenReturn(Flux.just(getService()));

        webTestClient.get()
                .uri("/api/v1/services")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ServiceResponse>>() {
                })
                .value(List::size, equalTo(1))
                .value(serviceResponseList -> serviceResponseList.get(0).getId(), equalTo(1L))
                .value(serviceResponseList -> serviceResponseList.get(0).getName(), equalTo("Aftonbladet"))
                .value(serviceResponseList -> serviceResponseList.get(0).getUrl(), equalTo("https://aftonbladet.se"))
                .value(serviceResponseList -> serviceResponseList.get(0).getCreatedAt(), equalTo(LocalDateTime.parse("2020-01-01T12:00:00")))
                .value(serviceResponseList -> serviceResponseList.get(0).getUpdatedAt(), equalTo(LocalDateTime.parse("2020-01-01T13:00:00")))
                .value(serviceResponseList -> serviceResponseList.get(0).getStatus(), equalTo(StatusResponse.OK));
    }

    @Test
    void getServiceById() {
        Mockito
                .when(isItUpService.getServiceById(1L))
                .thenReturn(Mono.just(getService()));

        webTestClient.get()
                .uri("/api/v1/services/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ServiceResponse.class)
                .value(ServiceResponse::getId, equalTo(1L))
                .value(ServiceResponse::getName, equalTo("Aftonbladet"))
                .value(ServiceResponse::getUrl, equalTo("https://aftonbladet.se"))
                .value(ServiceResponse::getCreatedAt, equalTo(LocalDateTime.parse("2020-01-01T12:00:00")))
                .value(ServiceResponse::getUpdatedAt, equalTo(LocalDateTime.parse("2020-01-01T13:00:00")))
                .value(ServiceResponse::getStatus, equalTo(StatusResponse.OK));
    }

    @Test
    void addNewService() {
        Mockito
                .when(isItUpService.addService(new AddServiceRequest("Aftonbladet", "https://aftonbladet.se")))
                .thenReturn(Mono.just(getService()));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/services")
                        .queryParam("name", "Aftonbladet")
                        .queryParam("url", "https://aftonbladet.se")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ServiceResponse>() {
                })
                .value(ServiceResponse::getId, equalTo(1L))
                .value(ServiceResponse::getName, equalTo("Aftonbladet"))
                .value(ServiceResponse::getUrl, equalTo("https://aftonbladet.se"))
                .value(ServiceResponse::getCreatedAt, equalTo(LocalDateTime.parse("2020-01-01T12:00:00")))
                .value(ServiceResponse::getUpdatedAt, equalTo(LocalDateTime.parse("2020-01-01T13:00:00")))
                .value(ServiceResponse::getStatus, equalTo(StatusResponse.OK));
    }

    @Test
    void updateService() {
        Mockito
                .when(isItUpService.updateService(new UpdateServiceRequest(1L, "Aftonbladet", "https://aftonbladet.se")))
                .thenReturn(Mono.just(getService()));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/services/1")
                        .queryParam("name", "Aftonbladet")
                        .queryParam("url", "https://aftonbladet.se")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ServiceResponse>() {
                })
                .value(ServiceResponse::getId, equalTo(1L))
                .value(ServiceResponse::getName, equalTo("Aftonbladet"))
                .value(ServiceResponse::getUrl, equalTo("https://aftonbladet.se"))
                .value(ServiceResponse::getCreatedAt, equalTo(LocalDateTime.parse("2020-01-01T12:00:00")))
                .value(ServiceResponse::getUpdatedAt, equalTo(LocalDateTime.parse("2020-01-01T13:00:00")))
                .value(ServiceResponse::getStatus, equalTo(StatusResponse.OK));
    }

    @Test
    void deleteService() {
        Mockito
                .when(isItUpService.deleteService(1L))
                .thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/api/v1/services/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful();
        verify(isItUpService, times(1)).deleteService(1L);
    }

    @Test
    void handleServiceNotFoundException() {
        String errorMessage = "Could not find service with id 1";

        Mockito
                .when(isItUpService.getServiceById(1L))
                .thenReturn(Mono.error(new ServiceNotFoundException(errorMessage)));

        webTestClient.get()
                .uri("/api/v1/services/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(ErrorResponse::getHttpCode, equalTo(400))
                .value(ErrorResponse::getHttpStatus, equalTo("Bad Request"))
                .value(ErrorResponse::getErrorCode, equalTo(4001))
                .value(ErrorResponse::getMessage, equalTo(errorMessage));
    }

    @Test
    void handleAlreadyInDbException() {
        String errorMessage = "Service with name: Aftonbladet and url: https://aftonbladet.se is already registered";

        Mockito
                .when(isItUpService.addService(new AddServiceRequest("Aftonbladet", "https://aftonbladet.se")))
                .thenReturn(Mono.error(new AlreadyInDatabaseException(errorMessage)));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/services")
                        .queryParam("name", "Aftonbladet")
                        .queryParam("url", "https://aftonbladet.se")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(ErrorResponse::getHttpCode, equalTo(400))
                .value(ErrorResponse::getHttpStatus, equalTo("Bad Request"))
                .value(ErrorResponse::getErrorCode, equalTo(4002))
                .value(ErrorResponse::getMessage, equalTo(errorMessage));
    }
}