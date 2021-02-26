package com.arogut.homex.gateway.service;

import com.arogut.homex.gateway.JwtUtil;
import com.arogut.homex.gateway.client.DataClient;
import com.arogut.homex.gateway.model.Command;
import com.arogut.homex.gateway.model.Contract;
import com.arogut.homex.gateway.model.Device;
import com.arogut.homex.gateway.model.DeviceMetadata;
import com.arogut.homex.gateway.model.DeviceType;
import com.arogut.homex.gateway.model.Measurement;
import com.arogut.homex.gateway.model.RegistrationRequest;
import com.arogut.homex.gateway.model.ValueType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private DataClient dataClient;

    private JwtUtil jwtUtil = new JwtUtil("0123456789012345678901234567890123456789012345678901234567890123", "1000");

    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(dataClient, jwtUtil);
    }

    @Test
    void shouldProperlyCallDataToAddDevice() {
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
                                .params(Set.of())
                                .build()))
                        .build())
                .build();
        Device device = Device.builder()
                .id("dummy")
                .name("dummy")
                .isConnected(true)
                .deviceType(DeviceType.SOURCE)
                .host("localhost")
                .port(999)
                .build();

        Mockito.when(dataClient.register(Mockito.any(Device.class)))
                .thenReturn(Mono.just(device));

        var response = registrationService.register(request).block();
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getExpiration()).isEqualTo(1000L);
        Assertions.assertThat(response.getDeviceId()).isEqualTo("dummy");
        Assertions.assertThat(response.getToken()).isNotEmpty();
    }
}
