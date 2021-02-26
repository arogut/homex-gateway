package com.arogut.homex.gateway.client;

import com.arogut.homex.gateway.JwtUtil;
import feign.MethodMetadata;
import feign.Target;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactivefeign.client.ReactiveHttpRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FeignClientJwtInterceptorTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private FeignClientJwtInterceptor interceptor;

    @Test
    void shouldProperlyAddAuthHeader() throws Exception {
        ReactiveHttpRequest reactiveHttpRequest = new ReactiveHttpRequest(createMethodMetadata(), Target.EmptyTarget.create(Void.class), URI.create(""), new HashMap<>(), null);
        Mockito.when(jwtUtil.isTokenExpired(Mockito.anyString())).thenReturn(false);
        ReflectionTestUtils.setField(interceptor, "token", "valid-token");

        interceptor.apply(reactiveHttpRequest);

        Assertions.assertThat(reactiveHttpRequest.headers()).containsKey("Authorization");
        Assertions.assertThat(reactiveHttpRequest.headers()).containsValue(List.of("Bearer valid-token"));
    }

    @Test
    void shouldUpdateTokenAndAddAuthHeader() throws Exception {
        ReactiveHttpRequest reactiveHttpRequest = new ReactiveHttpRequest(createMethodMetadata(), Target.EmptyTarget.create(Void.class), URI.create(""), new HashMap<>(), null);
        Mockito.when(jwtUtil.isTokenExpired(Mockito.any())).thenReturn(true);
        Mockito.when(jwtUtil.generateToken(Mockito.anyString(), Mockito.anyMap())).thenReturn("new-token");

        interceptor.apply(reactiveHttpRequest);

        Assertions.assertThat(reactiveHttpRequest.headers()).containsKey("Authorization");
        Assertions.assertThat(reactiveHttpRequest.headers()).containsValue(List.of("Bearer new-token"));
    }

    private MethodMetadata createMethodMetadata() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<MethodMetadata> constructor = MethodMetadata.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance(null);
    }
}
