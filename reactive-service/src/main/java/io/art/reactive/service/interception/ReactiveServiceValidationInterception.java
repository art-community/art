/*
 * ART
 *
 * Copyright 2020 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.art.reactive.service.interception;

import reactor.core.publisher.*;
import io.art.reactive.service.specification.*;
import io.art.service.*;
import io.art.server.service.exception.*;
import io.art.service.interceptor.*;
import io.art.server.service.validation.*;
import static java.util.Objects.*;
import static reactor.core.publisher.Flux.*;
import static io.art.core.caster.Caster.*;
import static io.art.reactive.service.constants.ReactiveServiceModuleConstants.*;
import static io.art.reactive.service.model.ReactiveService.*;
import static io.art.server.module.ServerModule.*;
import static io.art.server.constants.RequestValidationPolicy.*;
import static io.art.server.constants.ServiceExceptionsMessages.*;
import static io.art.server.model.ServiceInterceptionResult.*;

public class ReactiveServiceValidationInterception extends ServiceValidationInterception {
    @Override
    @SuppressWarnings("Duplicates")
    public ServiceInterceptionResult intercept(ServiceRequest<?> request) {
        String serviceId = request.getServiceMethodCommand().getServiceId();
        String methodId = request.getServiceMethodCommand().getMethodId();
        Specification serviceSpecification = serviceModuleState().getServiceRegistry().get(serviceId);

        if (!serviceSpecification.getServiceTypes().contains(REACTIVE_SERVICE_TYPE)) {
            return super.intercept(request);
        }

        if (request.getValidationPolicy() == NOT_NULL) {
            if (isNull(request.getRequestData()))
                throw new ValidationException(REQUEST_DATA_IS_NULL);
            return nextInterceptor(request);
        }
        if (request.getValidationPolicy() == NON_VALIDATABLE) return nextInterceptor(request);
        if (isNull(request.getRequestData()))
            throw new ValidationException(REQUEST_DATA_IS_NULL);

        ReactiveServiceSpecification reactiveServiceSpecification = (ReactiveServiceSpecification) serviceSpecification;
        ReactiveMethod reactiveMethod;
        if (isNull(reactiveMethod = reactiveServiceSpecification.getReactiveService().getMethods().get(methodId))) {
            return super.intercept(request);
        }

        ReactiveMethodProcessingMode requestProcessingMode = reactiveMethod.requestProcessingMode();
        switch (requestProcessingMode) {
            case STRAIGHT:
                return super.intercept(request);
            case REACTIVE:
                Flux<?> requestDataStream = from(cast(request.getRequestData()))
                        .doOnNext(payload -> ((Validatable) payload).onValidating(new Validator(((Validatable) payload))));
                return nextInterceptor(request, cast(new ServiceRequest<Flux<?>>(request.getServiceMethodCommand(), request.getValidationPolicy(), requestDataStream)));
        }
        return nextInterceptor(request);
    }
}
