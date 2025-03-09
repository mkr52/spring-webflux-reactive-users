package com.appsdevblog.reactive.ws.users.service;

import reactor.core.publisher.Mono;

public interface JwtService {
    String generateJwt(String subject); // subject is userId
    Mono<Boolean> validateJwt(String token);
    String extractTokenSubject(String token);
}
