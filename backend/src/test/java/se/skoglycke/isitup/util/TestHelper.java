package se.skoglycke.isitup.util;

import se.skoglycke.isitup.domain.service.Service;
import se.skoglycke.isitup.domain.service.Status;

import java.time.LocalDateTime;

public class TestHelper {

    public static Service getService() {
        return new Service(
                1L,
                "Aftonbladet",
                "https://aftonbladet.se",
                LocalDateTime.parse("2020-01-01T12:00:00"),
                LocalDateTime.parse("2020-01-01T13:00:00"),
                Status.OK
        );
    }

}
