package gr.uom.it1566.Library.repository;

import gr.uom.it1566.Library.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;


public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
    Mono<UserEntity> findByEmail(String email);
}


