package com.arogut.homex.gateway.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResponse {

    private String deviceId;
    private String token;
    private long expiration;
}
