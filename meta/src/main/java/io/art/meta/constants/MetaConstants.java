package io.art.meta.constants;

public interface MetaConstants {
    String UNSUPPORTED_TYPE = "Unsupported type: {0}";
    String IS_NAME = "is";
    String GET_NAME = "get";

    enum MetaTypeInternalKind {
        VOID,
        STRING,
        LONG,
        DOUBLE,
        SHORT,
        FLOAT,
        INTEGER,
        BYTE,
        CHARACTER,
        BOOLEAN,
        DATE,
        LOCAL_DATE_TIME,
        ZONED_DATE_TIME,
        DURATION,

        ARRAY,
        LONG_ARRAY,
        DOUBLE_ARRAY,
        FLOAT_ARRAY,
        INTEGER_ARRAY,
        BOOLEAN_ARRAY,
        CHARACTER_ARRAY,
        SHORT_ARRAY,
        BYTE_ARRAY,

        COLLECTION,
        IMMUTABLE_COLLECTION,
        LIST,
        IMMUTABLE_ARRAY,
        SET,
        IMMUTABLE_SET,
        QUEUE,
        DEQUEUE,
        STREAM,

        MAP,
        IMMUTABLE_MAP,

        FLUX,
        MONO,

        LAZY,
        OPTIONAL,
        SUPPLIER,
        ENUM,
        INPUT_STREAM,
        OUTPUT_STREAM,
        NIO_BUFFER,
        NETTY_BUFFER,

        CUSTOM,
    }

    static boolean isPrimitive(MetaTypeInternalKind kind) {
        switch (kind) {
            case LONG:
            case SHORT:
            case DOUBLE:
            case FLOAT:
            case INTEGER:
            case CHARACTER:
            case BOOLEAN:
            case BYTE:
                return true;
            default:
                return false;
        }
    }

    enum MetaTypeExternalKind {
        MAP,
        ARRAY,
        STRING,
        LONG,
        DOUBLE,
        FLOAT,
        INTEGER,
        BOOLEAN,
        CHARACTER,
        SHORT,
        BYTE,
        BINARY
    }

    interface Errors {
        String TRANSFORMATION_NOT_AVAILABLE = "Transformation is not available for value: {0}";
        String TRANSFORMER_NOT_FOUND = "Transformer is not found for type: {0}";
    }
}
