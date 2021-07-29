/*
 * ART
 *
 * Copyright 2019-2021 ART
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

package io.art.meta.model;

import io.art.core.annotation.*;
import io.art.core.collection.*;
import io.art.meta.exception.*;
import io.art.meta.registry.*;
import io.art.meta.validator.*;
import lombok.*;
import static io.art.core.caster.Caster.*;
import static io.art.core.collector.ArrayCollector.*;
import static io.art.core.constants.StringConstants.*;
import static io.art.core.factory.ListFactory.*;
import static io.art.core.factory.MapFactory.*;
import static io.art.core.factory.SetFactory.*;
import static io.art.meta.constants.MetaConstants.Errors.*;
import static io.art.meta.state.MetaComputationState.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

@ToString
@ForGenerator
@EqualsAndHashCode
public abstract class MetaLibrary {
    private ImmutableMap<Class<?>, MetaClass<?>> classes;
    private final Map<String, MetaPackage> packages = map();
    private final Set<MetaClass<?>> rootClasses = set();
    private final List<MetaLibrary> dependencies = linkedList();
    private final AtomicBoolean computed = new AtomicBoolean(false);

    protected MetaLibrary(MetaLibrary[] dependencies) {
        this.dependencies.addAll(linkedListOf(dependencies));
    }

    protected <T extends MetaPackage> T register(T metaPackage) {
        packages.put(metaPackage.name(), metaPackage);
        return metaPackage;
    }

    protected <T extends MetaClass<?>> T register(T metaClass) {
        rootClasses.add(metaClass);
        return metaClass;
    }

    public ImmutableMap<String, MetaPackage> packages() {
        return immutableMapOf(packages);
    }

    public ImmutableSet<MetaClass<?>> rootClasses() {
        return immutableSetOf(rootClasses);
    }

    public ImmutableMap<Class<?>, MetaClass<?>> classes() {
        return classes;
    }

    public <T extends MetaPackage> T packageOf(String name) {
        return cast(packages.get(name));
    }

    public void compute() {
        List<MetaLibrary> registryDependencies = MetaLibraryMutableRegistry
                .get()
                .stream()
                .map(Supplier::get)
                .collect(listCollector());
        for (MetaLibrary dependency : registryDependencies) {
            dependency.computeLibrary();
        }
        computeLibrary();
        MetaClassMutableRegistry.clear();
        MetaLibraryMutableRegistry.clear();
    }

    private void computeLibrary() {
        if (computed.compareAndSet(false, true)) {
            for (MetaLibrary dependency : this.dependencies) {
                dependency.computeLibrary();
            }

            rootClasses.forEach(MetaClass::beginComputation);
            packages.values().forEach(MetaPackage::beginComputation);

            ImmutableArray<ValidationResult> validationErrors = getValidationErrors();
            if (validationErrors.isEmpty()) {
                rootClasses.forEach(MetaClass::completeComputation);
                packages.values().forEach(MetaPackage::completeComputation);
                classes = MetaClassMutableRegistry.get();
                return;
            }

            StringBuilder validationErrorMessage = new StringBuilder(META_COMPUTATION_FAILED);
            validationErrors.forEach(error -> validationErrorMessage.append(error.getMessage()).append(NEW_LINE));
            throw new MetaException(validationErrorMessage.toString());
        }
    }
}
