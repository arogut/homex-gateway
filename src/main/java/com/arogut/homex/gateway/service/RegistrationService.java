package com.arogut.homex.gateway.service;

import com.arogut.homex.gateway.JwtUtil;
import com.arogut.homex.gateway.client.DataClient;
import com.arogut.homex.gateway.model.AuthType;
import com.arogut.homex.gateway.model.Contract;
import com.arogut.homex.gateway.model.Device;
import com.arogut.homex.gateway.model.DeviceType;
import com.arogut.homex.gateway.model.RegistrationRequest;
import com.arogut.homex.gateway.model.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final DataClient dataClient;
    private final JwtUtil jwtUtil;

    public Mono<RegistrationResponse> register(RegistrationRequest registrationRequest) {
        return dataClient.register(createDevice(registrationRequest))
                .map(device -> createResponse(device.getId()));
    }

    public Mono<RegistrationResponse> refresh(String token) {
        return Mono.justOrEmpty(token)
                .map(t -> t.substring(7))
                .filter(t -> !jwtUtil.isTokenExpired(t))
                .map(jwtUtil::getAllClaimsFromToken)
                .map(claims -> createResponse(claims.getSubject()));
    }

    private Device createDevice(RegistrationRequest registrationRequest) {
        return Device.builder()
                .macAddress(registrationRequest.getMetadata().getMacAddress())
                .name(registrationRequest.getMetadata().getName())
                .deviceType(DeviceType.SOURCE)
                .host(registrationRequest.getMetadata().getHost())
                .port(registrationRequest.getMetadata().getPort())
                .contract(Contract.builder()
                        .measurements(registrationRequest.getContract().getMeasurements())
                        .commands(registrationRequest.getContract().getCommands())
                        .build())
                .build();
    }

    private RegistrationResponse createResponse(String subject) {
        return RegistrationResponse.builder()
                .deviceId(subject)
                .token(jwtUtil.generateToken(subject, Map.of("role", AuthType.DEVICE)))
                .expiration(Long.parseLong(jwtUtil.getExpiration()))
                .build();
    }
}
