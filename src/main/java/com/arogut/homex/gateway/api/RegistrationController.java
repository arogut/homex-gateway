package com.arogut.homex.gateway.api;

import com.arogut.homex.gateway.model.RegistrationRequest;
import com.arogut.homex.gateway.model.RegistrationResponse;
import com.arogut.homex.gateway.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/devices/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public Mono<ResponseEntity<RegistrationResponse>> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return registrationService.register(registrationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping("/refresh")
    public Mono<ResponseEntity<RegistrationResponse>> refresh(@RequestHeader(value = "Authorization", required = false) String token) {
        return registrationService.refresh(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(401).build());
    }
}
