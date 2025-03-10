package com.appsdevblog.reactive.ws.users.repository;

import com.appsdevblog.reactive.ws.users.data.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {
    Flux<UserEntity> findAllBy(Pageable pageable);

    Mono<UserEntity> findByEmail(String username);
}
