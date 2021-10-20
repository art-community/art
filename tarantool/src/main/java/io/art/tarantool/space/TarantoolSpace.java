package io.art.tarantool.space;

public interface TarantoolSpace<T, K> {
/*
    void bucketIdGenerator(Function<T, Long> bucketIdGenerator);

    TarantoolRecord<T> get(K key);
    TarantoolRecord<T> get(TarantoolTransactionDependency keyDependency);

    TarantoolRecord<T> get(String index, Value key);
    TarantoolRecord<T> get(String index, TarantoolTransactionDependency keyDependency);

    TarantoolRecord<ImmutableArray<T>> getAll();

    SelectRequest<T> select(K request);
    SelectRequest<T> select(TarantoolTransactionDependency requestDependency);
    SelectRequest<T> select(String index, Value request);
    SelectRequest<T> select(String index, TarantoolTransactionDependency requestDependency);

    TarantoolRecord<T> delete(K key);
    TarantoolRecord<T> delete(TarantoolTransactionDependency keyDependency);

    TarantoolRecord<T> insert(T data);
    TarantoolRecord<T> insert(TarantoolTransactionDependency dataDependency);

    TarantoolRecord<T> put(T data);
    TarantoolRecord<T> put(TarantoolTransactionDependency dataDependency);

    TarantoolRecord<T> replace(T data);
    TarantoolRecord<T> replace(TarantoolTransactionDependency dataDependency);

    TarantoolRecord<T> update(K key, TarantoolUpdateFieldOperation... operations);
    TarantoolRecord<T> update(TarantoolTransactionDependency keyDependency, TarantoolUpdateFieldOperation... operations);

    TarantoolRecord<T> upsert(T defaultData, TarantoolUpdateFieldOperation... operations);
    TarantoolRecord<T> upsert(TarantoolTransactionDependency defaultDataDependency, TarantoolUpdateFieldOperation... operations);

    TarantoolRecord<Long> count();
    TarantoolRecord<Long> len();

    void truncate();

    TarantoolRecord<Set<String>> listIndices();

    Long bucketOf(T data);

    void beginTransaction();

    void beginTransaction(Long bucketId);

    void commitTransaction();

    void cancelTransaction();

*/
}
