// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GrpcServlet.proto

package ru.art.grpc.servlet;

/**
 * Protobuf type {@code ru.art.grpc.servlet.GrpcRequest}
 */
public  final class GrpcRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ru.art.grpc.servlet.GrpcRequest)
    GrpcRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GrpcRequest.newBuilder() to construct.
  private GrpcRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GrpcRequest() {
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GrpcRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            com.google.protobuf.Value.Builder subBuilder = null;
            if (serviceRequest_ != null) {
              subBuilder = serviceRequest_.toBuilder();
            }
            serviceRequest_ = input.readMessage(com.google.protobuf.Value.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(serviceRequest_);
              serviceRequest_ = subBuilder.buildPartial();
            }

            break;
          }
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return ru.art.grpc.servlet.GrpcServletMessage.internal_static_ru_art_grpc_servlet_GrpcRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return ru.art.grpc.servlet.GrpcServletMessage.internal_static_ru_art_grpc_servlet_GrpcRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ru.art.grpc.servlet.GrpcRequest.class, ru.art.grpc.servlet.GrpcRequest.Builder.class);
  }

  public static final int SERVICEREQUEST_FIELD_NUMBER = 1;
  private com.google.protobuf.Value serviceRequest_;
  /**
   * <code>.google.protobuf.Value serviceRequest = 1;</code>
   */
  public boolean hasServiceRequest() {
    return serviceRequest_ != null;
  }
  /**
   * <code>.google.protobuf.Value serviceRequest = 1;</code>
   */
  public com.google.protobuf.Value getServiceRequest() {
    return serviceRequest_ == null ? com.google.protobuf.Value.getDefaultInstance() : serviceRequest_;
  }
  /**
   * <code>.google.protobuf.Value serviceRequest = 1;</code>
   */
  public com.google.protobuf.ValueOrBuilder getServiceRequestOrBuilder() {
    return getServiceRequest();
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (serviceRequest_ != null) {
      output.writeMessage(1, getServiceRequest());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (serviceRequest_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getServiceRequest());
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof ru.art.grpc.servlet.GrpcRequest)) {
      return super.equals(obj);
    }
    ru.art.grpc.servlet.GrpcRequest other = (ru.art.grpc.servlet.GrpcRequest) obj;

    boolean result = true;
    result = result && (hasServiceRequest() == other.hasServiceRequest());
    if (hasServiceRequest()) {
      result = result && getServiceRequest()
          .equals(other.getServiceRequest());
    }
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasServiceRequest()) {
      hash = (37 * hash) + SERVICEREQUEST_FIELD_NUMBER;
      hash = (53 * hash) + getServiceRequest().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ru.art.grpc.servlet.GrpcRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(ru.art.grpc.servlet.GrpcRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code ru.art.grpc.servlet.GrpcRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ru.art.grpc.servlet.GrpcRequest)
      ru.art.grpc.servlet.GrpcRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ru.art.grpc.servlet.GrpcServletMessage.internal_static_ru_art_grpc_servlet_GrpcRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ru.art.grpc.servlet.GrpcServletMessage.internal_static_ru_art_grpc_servlet_GrpcRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ru.art.grpc.servlet.GrpcRequest.class, ru.art.grpc.servlet.GrpcRequest.Builder.class);
    }

    // Construct using ru.art.grpc.servlet.GrpcRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (serviceRequestBuilder_ == null) {
        serviceRequest_ = null;
      } else {
        serviceRequest_ = null;
        serviceRequestBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return ru.art.grpc.servlet.GrpcServletMessage.internal_static_ru_art_grpc_servlet_GrpcRequest_descriptor;
    }

    @java.lang.Override
    public ru.art.grpc.servlet.GrpcRequest getDefaultInstanceForType() {
      return ru.art.grpc.servlet.GrpcRequest.getDefaultInstance();
    }

    @java.lang.Override
    public ru.art.grpc.servlet.GrpcRequest build() {
      ru.art.grpc.servlet.GrpcRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public ru.art.grpc.servlet.GrpcRequest buildPartial() {
      ru.art.grpc.servlet.GrpcRequest result = new ru.art.grpc.servlet.GrpcRequest(this);
      if (serviceRequestBuilder_ == null) {
        result.serviceRequest_ = serviceRequest_;
      } else {
        result.serviceRequest_ = serviceRequestBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return (Builder) super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof ru.art.grpc.servlet.GrpcRequest) {
        return mergeFrom((ru.art.grpc.servlet.GrpcRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ru.art.grpc.servlet.GrpcRequest other) {
      if (other == ru.art.grpc.servlet.GrpcRequest.getDefaultInstance()) return this;
      if (other.hasServiceRequest()) {
        mergeServiceRequest(other.getServiceRequest());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      ru.art.grpc.servlet.GrpcRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ru.art.grpc.servlet.GrpcRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.google.protobuf.Value serviceRequest_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Value, com.google.protobuf.Value.Builder, com.google.protobuf.ValueOrBuilder> serviceRequestBuilder_;
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public boolean hasServiceRequest() {
      return serviceRequestBuilder_ != null || serviceRequest_ != null;
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public com.google.protobuf.Value getServiceRequest() {
      if (serviceRequestBuilder_ == null) {
        return serviceRequest_ == null ? com.google.protobuf.Value.getDefaultInstance() : serviceRequest_;
      } else {
        return serviceRequestBuilder_.getMessage();
      }
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public Builder setServiceRequest(com.google.protobuf.Value value) {
      if (serviceRequestBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        serviceRequest_ = value;
        onChanged();
      } else {
        serviceRequestBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public Builder setServiceRequest(
        com.google.protobuf.Value.Builder builderForValue) {
      if (serviceRequestBuilder_ == null) {
        serviceRequest_ = builderForValue.build();
        onChanged();
      } else {
        serviceRequestBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public Builder mergeServiceRequest(com.google.protobuf.Value value) {
      if (serviceRequestBuilder_ == null) {
        if (serviceRequest_ != null) {
          serviceRequest_ =
            com.google.protobuf.Value.newBuilder(serviceRequest_).mergeFrom(value).buildPartial();
        } else {
          serviceRequest_ = value;
        }
        onChanged();
      } else {
        serviceRequestBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public Builder clearServiceRequest() {
      if (serviceRequestBuilder_ == null) {
        serviceRequest_ = null;
        onChanged();
      } else {
        serviceRequest_ = null;
        serviceRequestBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public com.google.protobuf.Value.Builder getServiceRequestBuilder() {
      
      onChanged();
      return getServiceRequestFieldBuilder().getBuilder();
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    public com.google.protobuf.ValueOrBuilder getServiceRequestOrBuilder() {
      if (serviceRequestBuilder_ != null) {
        return serviceRequestBuilder_.getMessageOrBuilder();
      } else {
        return serviceRequest_ == null ?
            com.google.protobuf.Value.getDefaultInstance() : serviceRequest_;
      }
    }
    /**
     * <code>.google.protobuf.Value serviceRequest = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Value, com.google.protobuf.Value.Builder, com.google.protobuf.ValueOrBuilder> 
        getServiceRequestFieldBuilder() {
      if (serviceRequestBuilder_ == null) {
        serviceRequestBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.protobuf.Value, com.google.protobuf.Value.Builder, com.google.protobuf.ValueOrBuilder>(
                getServiceRequest(),
                getParentForChildren(),
                isClean());
        serviceRequest_ = null;
      }
      return serviceRequestBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:ru.art.grpc.servlet.GrpcRequest)
  }

  // @@protoc_insertion_point(class_scope:ru.art.grpc.servlet.GrpcRequest)
  private static final ru.art.grpc.servlet.GrpcRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ru.art.grpc.servlet.GrpcRequest();
  }

  public static ru.art.grpc.servlet.GrpcRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GrpcRequest>
      PARSER = new com.google.protobuf.AbstractParser<GrpcRequest>() {
    @java.lang.Override
    public GrpcRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new GrpcRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GrpcRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GrpcRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public ru.art.grpc.servlet.GrpcRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

