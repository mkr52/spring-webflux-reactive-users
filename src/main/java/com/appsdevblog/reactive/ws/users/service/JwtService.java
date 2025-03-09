package com.appsdevblog.reactive.ws.users.service;

public interface JwtService {
    String generateJwt(String subject); // subject is userId
}
