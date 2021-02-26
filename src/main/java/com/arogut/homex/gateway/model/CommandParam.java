package com.arogut.homex.gateway.model;

import javax.validation.constraints.NotNull;

public class CommandParam {

    @NotNull
    private String name;

    @NotNull
    private ValueType type;
}
