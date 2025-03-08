package com.appsdevblog.reactive.ws.users.service;

import com.appsdevblog.reactive.ws.users.model.CreateUserRequest;
import com.appsdevblog.reactive.ws.users.model.UserRest;
import com.appsdevblog.reactive.ws.users.data.UserEntity;
import com.appsdevblog.reactive.ws.users.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono) {

        //Create Entity
        return createUserRequestMono
                .flatMap(this::convertToEntity)
                .flatMap(userRepository::save)
                .mapNotNull(item -> convertToUserRest(item));
//                .onErrorMap(DuplicateKeyException.class, ex -> new ResponseStatusException(
//                        HttpStatus.CONFLICT,
//                        ex.getMessage()
//                ))
//                .onErrorMap(throwable -> {
//                    if(throwable instanceof DuplicateKeyException) {
//                        return new ResponseStatusException(
//                                HttpStatus.CONFLICT, throwable.getMessage());
//                    } else if(throwable instanceof DataIntegrityViolationException){
//                        return new ResponseStatusException(
//                                HttpStatus.BAD_REQUEST, throwable.getMessage());
//                    } else {
//                        return new ResponseStatusException(
//                                HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
//                    }
//                });
    }

    @Override
    public Mono<UserRest> getUserById(UUID id) {
        return userRepository.findById(id)
                .mapNotNull(this::convertToUserRest);
    }

    @Override
    public Flux<UserRest> findAll(int page, int limit) {
        if (page > 0) page = page - 1;
        Pageable pageable = PageRequest.of(page, limit);
        return userRepository.findAllBy(pageable)
                .map(userEntity -> convertToUserRest(userEntity));
    }

    private Mono<UserEntity> convertToEntity(CreateUserRequest createUserRequest) {
        return Mono.fromCallable(() -> {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(createUserRequest, userEntity);
            userEntity.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            return userEntity;
        }).subscribeOn(Schedulers.boundedElastic());
    }

        private UserRest convertToUserRest (UserEntity entity){
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(entity, userRest);
            return userRest;
        }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(userEntity -> User
                        .withUsername(userEntity.getEmail())
                        .password(userEntity.getPassword())
                        .authorities(new ArrayList<>())
                        .build());
    }
}
