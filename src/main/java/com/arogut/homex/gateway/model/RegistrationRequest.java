package com.arogut.homex.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @Valid
    @NotNull(message = "{metadata.cannot.be.null}")
    private DeviceMetadata metadata;

    @Valid
    @NotNull(message = "{contract.cannot.be.null}")
    private Contract contract;
}
