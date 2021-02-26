package com.arogut.homex.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMetadata {

    @NotEmpty
    private String macAddress;

    @NotNull
    private String name;

    @NotEmpty
    private String host;

    @Max(99999)
    private int port;
}
