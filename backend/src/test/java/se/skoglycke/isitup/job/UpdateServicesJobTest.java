package se.skoglycke.isitup.job;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.skoglycke.isitup.domain.IsItUpService;
import se.skoglycke.isitup.domain.service.AddServiceRequest;
import se.skoglycke.isitup.domain.service.Service;
import se.skoglycke.isitup.infrastructure.RestClient;
import se.skoglycke.isitup.infrastructure.ServiceRepository;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ActiveProfiles("test")
class UpdateServicesJobTest {
    private static final int PORT = 1337;

    private static ClientAndServer mockServer;

    @Autowired
    private IsItUpService isItUpService;

    @Autowired
    private ConcurrentMap<Long, Service> serviceContext;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ServiceRepository serviceRepository;

    private UpdateServicesJob updateServicesJob;

    @BeforeAll
    static void beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(1337);
    }

    @BeforeEach
    void setUp() {
        serviceRepository.deleteAll().block();
        serviceContext.clear();
        updateServicesJob = new UpdateServicesJob(isItUpService, serviceContext, restClient, 1000L);
    }

    @AfterAll
    static void afterAll() {
        mockServer.stop();
    }

    @Test
    void fillContext() {

        assertThat(serviceContext).isEmpty();

        new MockServerClient("127.0.0.1", PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/test"))
                .respond(
                        response()
                        .withStatusCode(200)
                        .withBody("[]")
                );

        Service savedService = isItUpService
                .addService(new AddServiceRequest("Aftonbladet", "https://localhost:" + PORT + "/test"))
                .block();

        assertThat(savedService).isNotNull();

        updateServicesJob.fillContextAfterStartUp();

        await()
                .atLeast(Duration.ofMillis(100))
                .atMost(Duration.ofSeconds(5))
                .with()
                .pollInterval(Duration.ofMillis(100))
                .until(() -> serviceContext.get(savedService.getId()) != null);
    }
}