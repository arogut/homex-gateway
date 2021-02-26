package com.arogut.homex.gateway.client;

import com.arogut.homex.gateway.model.Device;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "DataClient", url = "${data.url}")
public interface DataClient {

    @PostMapping(value = "/devices")
    Mono<Device> register(@RequestBody Device message);
}
