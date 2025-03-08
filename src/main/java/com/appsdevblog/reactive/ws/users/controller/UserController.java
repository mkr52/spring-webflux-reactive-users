package com.appsdevblog.reactive.ws.users.controller;

import com.appsdevblog.reactive.ws.users.model.CreateUserRequest;
import com.appsdevblog.reactive.ws.users.model.UserRest;
import com.appsdevblog.reactive.ws.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<UserRest>> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
        return userService.createUser(createUserRequest)
                .map( userRest -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .location(URI.create("/users/" + userRest.getId()))
                        .body(userRest)
                );
//        return createUserRequest.map(request ->
//                new UserRest(UUID.randomUUID(),
//                        request.getFirstName(),
//                        request.getLastName(),
//                        request.getEmail())
//        ).map( userRest -> ResponseEntity.status(HttpStatus.CREATED)
//                .location(URI.create("/users/" + userRest.getId()))
//                .body(userRest));
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserRest>> getUser(@PathVariable("userId") UUID userId) {
        return userService.getUserById(userId)
                .map(userRest -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(userRest))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @GetMapping
    public Flux<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return userService.findAll(page, limit);
//        return Flux.just(
//                new UserRest(UUID.randomUUID(), "sdfdsf", "dsfsdfsdf", "ab@tc.com"),
//                new UserRest(UUID.randomUUID(), "hjjghherg", "sdfdsfsdf", "vb@tc.com")
//        );
    }
}
