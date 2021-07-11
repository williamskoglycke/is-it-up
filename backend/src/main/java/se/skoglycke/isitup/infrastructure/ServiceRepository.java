package se.skoglycke.isitup.infrastructure;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import se.skoglycke.isitup.infrastructure.entities.ServiceEntity;

public interface ServiceRepository extends ReactiveCrudRepository<ServiceEntity, Long> {
    Mono<ServiceEntity> findByNameAndUrl(String name, String url);

    // Needed custom query, if not spring data set created_at and updated_at to the same as before (updated_at never worked)
    @Modifying
    @Query("update services set name=:name, url=:url where id=:id")
    Mono<Integer> updateService(@Param("id") Long id, @Param("name") String name, @Param("url") String url);
}
