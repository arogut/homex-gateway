package com.arogut.homex.gateway.api;

import com.arogut.homex.gateway.JwtUtil;
import com.arogut.homex.gateway.client.DataClient;
import com.arogut.homex.gateway.model.AuthType;
import com.arogut.homex.gateway.model.Command;
import com.arogut.homex.gateway.model.Contract;
import com.arogut.homex.gateway.model.Device;
import com.arogut.homex.gateway.model.DeviceMetadata;
import com.arogut.homex.gateway.model.DeviceType;
import com.arogut.homex.gateway.model.Measurement;
import com.arogut.homex.gateway.model.RegistrationRequest;
import com.arogut.homex.gateway.model.RegistrationResponse;
import com.arogut.homex.gateway.model.ValueType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class RegistrationControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private DataClient dataClient;

    @SpyBean
    private JwtUtil jwtUtil;

    @Test
    void shouldRegisterDeviceAndReturn200OK() {
        RegistrationRequest request = RegistrationRequest.builder()
                .metadata(DeviceMetadata.builder()
                        .macAddress("mac-address")
                        .name("dummy")
                        .host("localhost")
                        .port(999)
                        .build())
                .contract(Contract.builder()
                        .measurements(Set.of(Measurement.builder()
                                .name("temp")
                                .type(ValueType.NUMBER)
                                .build()))
                        .commands(Set.of(Command.builder()
                                .name("turn-off")
                                .endpoint("/turn-off")
                                .params(Set.of())
                                .build()))
                        .build())
                .build();
        Device device = Device.builder()
                .id("dummy")
                .name("dummy")
                .isConnected(true)
                .macAddress("dummy")
                .deviceType(DeviceType.SOURCE)
                .host("localhost")
                .port(999)
                .build();
        Mockito.when(dataClient.register(Mockito.any(Device.class))).thenReturn(Mono.just(device));

        webClient.post()
                .uri("/devices/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegistrationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationResponse.class)
                .value(registrationResponse -> {
                    Assertions.assertThat(registrationResponse.getDeviceId()).isEqualTo("dummy");
                    Assertions.assertThat(registrationResponse.getToken()).isNotEmpty();
                });
    }

    @Test
    @WithMockUser
    void shouldNotAcceptDeviceAndReturn400() {
        RegistrationRequest request = RegistrationRequest.builder()
                .metadata(DeviceMetadata.builder()
                        .port(999)
                        .build())
                .contract(Contract.builder()
                        .measurements(Set.of(Measurement.builder()
                                .name("temp")
                                .type(ValueType.NUMBER)
                                .build()))
                        .commands(Set.of(Command.builder()
                                .name("turn-off")
                                .params(Set.of())
                                .build()))
                        .build())
                .build();

        webClient.post()
                .uri("/devices/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegistrationRequest.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldRefreshTokenForDevice() {
        String token = jwtUtil.generateToken("dummy", Map.of("role", AuthType.DEVICE));

        webClient.get()
                .uri("/devices/auth/refresh")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationResponse.class)
                .value(registrationResponse -> {
                    Assertions.assertThat(registrationResponse.getDeviceId()).isEqualTo("dummy");
                    Assertions.assertThat(registrationResponse.getToken()).isNotEmpty();
                });
    }

    @Test
    void refreshTokenEndpointShouldBeSecured() {
        webClient.get()
                .uri("/devices/auth/refresh")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
