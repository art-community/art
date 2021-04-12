package io.art.model.implementation.server;

import io.art.core.collection.*;
import io.art.core.model.*;
import io.art.value.constants.ValueModuleConstants.*;
import lombok.*;
import reactor.netty.http.server.*;
import reactor.netty.tcp.*;

import java.util.function.*;

@Builder
@Getter
public class HttpServerModel {
    private final ImmutableMap<String, HttpServiceModel> services;
    private final String host;
    private final Integer port;
    private final boolean compression;
    private final boolean logging;
    private final boolean wiretap;
    private final boolean accessLogging;
    private final int fragmentationMtu;
    private final DataFormat defaultDataFormat;
    private final DataFormat defaultMetaDataFormat;
    private final ServiceMethodIdentifier defaultServiceMethod;
    private final UnaryOperator<HttpRequestDecoderSpec> requestDecoderConfigurator;
    private final boolean redirectToHttps;
    private final Consumer<? super SslProvider.SslContextSpec> sslConfigurator;
}