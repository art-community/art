package io.art.storage.record;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public interface StorageRecord <T>{

    CompletableFuture<Optional<T>> getFuture();

    Optional<T> getOptional();

    T get();

    T orElse(T other);

    T orElseThrow();

    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

    <U> U map(Function<Optional<T>, U> mapper);



    boolean isDone();

    boolean isPresent();

    boolean isEmpty();

    StorageRecord<T> synchronize();
}
