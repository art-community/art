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

package io.art.http.client.interceptor;

import org.apache.http.client.methods.*;
import io.art.core.constants.*;
import static io.art.core.constants.InterceptionStrategy.*;
import java.util.function.*;

@FunctionalInterface
public interface HttpClientRequestInterception {
    static HttpClientRequestInterception interceptAndContinue(Consumer<HttpUriRequest> runnable) {
        return request -> {
            runnable.accept(request);
            return NEXT;
        };
    }

    static HttpClientRequestInterception interceptAndCall(Consumer<HttpUriRequest> runnable) {
        return request -> {
            runnable.accept(request);
            return PROCESS;
        };
    }

    static HttpClientRequestInterception interceptAndReturn(Consumer<HttpUriRequest> runnable) {
        return request -> {
            runnable.accept(request);
            return TERMINATE;
        };
    }

    InterceptionStrategy intercept(HttpUriRequest request);
}
