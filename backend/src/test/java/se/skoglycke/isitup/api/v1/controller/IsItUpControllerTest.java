package se.skoglycke.isitup.api.v1.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import se.skoglycke.isitup.api.v1.controller.beans.ServiceResponse;
import se.skoglycke.isitup.api.v1.controller.beans.ServicesResponse;
import se.skoglycke.isitup.api.v1.controller.beans.StatusResponse;
import se.skoglycke.isitup.api.v1.controller.mapper.ServiceMapper;
import se.skoglycke.isitup.domain.IsItUpService;
import se.skoglycke.isitup.domain.service.Services;
import se.skoglycke.isitup.util.ControllerTestConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static se.skoglycke.isitup.util.TestHelper.getService;

@WebFluxTest(IsItUpController.class)
@Import(ControllerTestConfiguration.class)
class IsItUpControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IsItUpService isItUpService;

    @Test
    void getStatusForServices() {
        Mockito
                .when(isItUpService.getAllStatuses())
                .thenReturn(Stream.of(new Services(List.of(getService()))));

        List<ServicesResponse> servicesResponses = webTestClient.get()
                .uri("/api/v1/status")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(ServicesResponse.class)
                .getResponseBody()
                .collectList()
                .block();

        assertThat(servicesResponses).hasSize(1);
    }
}