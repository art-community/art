// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GrpcServlet.proto

package ru.art.grpc.servlet;

public final class GrpcServletMessage {
  private GrpcServletMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ru_art_grpc_servlet_GrpcRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ru_art_grpc_servlet_GrpcRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ru_art_grpc_servlet_GrpcResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ru_art_grpc_servlet_GrpcResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021GrpcServlet.proto\022\023ru.art.grpc.servlet" +
      "\032\013Value.proto\"n\n\013GrpcRequest\022\021\n\tserviceI" +
      "d\030\001 \001(\t\022\020\n\010methodId\030\002 \001(\t\022:\n\013requestData" +
      "\030\003 \001(\0132%.ru.art.protobuf.entity.Protobuf" +
      "Value\"t\n\014GrpcResponse\022\021\n\terrorCode\030\001 \001(\t" +
      "\022;\n\014responseData\030\002 \001(\0132%.ru.art.protobuf" +
      ".entity.ProtobufValue\022\024\n\014errorMessage\030\003 " +
      "\001(\tB+\n\023ru.art.grpc.servletB\022GrpcServletM" +
      "essageP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          ru.art.protobuf.entity.ProtobufValueMessage.getDescriptor(),
        }, assigner);
    internal_static_ru_art_grpc_servlet_GrpcRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ru_art_grpc_servlet_GrpcRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ru_art_grpc_servlet_GrpcRequest_descriptor,
        new java.lang.String[] { "ServiceId", "MethodId", "RequestData", });
    internal_static_ru_art_grpc_servlet_GrpcResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_ru_art_grpc_servlet_GrpcResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ru_art_grpc_servlet_GrpcResponse_descriptor,
        new java.lang.String[] { "ErrorCode", "ResponseData", "ErrorMessage", });
    ru.art.protobuf.entity.ProtobufValueMessage.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
