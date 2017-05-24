package mclab;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.2.0)",
    comments = "Source: device_config.proto")
public final class DeviceConfigGrpc {

  private DeviceConfigGrpc() {}

  public static final String SERVICE_NAME = "device_config.DeviceConfig";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<mclab.EditConfigRequest,
      mclab.EditConfigResponse> METHOD_EDIT_CONFIG =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "device_config.DeviceConfig", "EditConfig"),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.EditConfigRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.EditConfigResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<mclab.GetConfigRequest,
      mclab.GetConfigResponse> METHOD_GET_CONFIG =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "device_config.DeviceConfig", "GetConfig"),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.GetConfigRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.GetConfigResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<mclab.HelloRequest,
      mclab.HelloResponse> METHOD_HELLO =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "device_config.DeviceConfig", "Hello"),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.HelloRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.HelloResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<mclab.LockRequest,
      mclab.LockResponse> METHOD_LOCK =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "device_config.DeviceConfig", "Lock"),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.LockRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(mclab.LockResponse.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DeviceConfigStub newStub(io.grpc.Channel channel) {
    return new DeviceConfigStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DeviceConfigBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DeviceConfigBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static DeviceConfigFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DeviceConfigFutureStub(channel);
  }

  /**
   */
  public static abstract class DeviceConfigImplBase implements io.grpc.BindableService {

    /**
     */
    public void editConfig(mclab.EditConfigRequest request,
        io.grpc.stub.StreamObserver<mclab.EditConfigResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_EDIT_CONFIG, responseObserver);
    }

    /**
     */
    public void getConfig(mclab.GetConfigRequest request,
        io.grpc.stub.StreamObserver<mclab.GetConfigResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_CONFIG, responseObserver);
    }

    /**
     */
    public void hello(mclab.HelloRequest request,
        io.grpc.stub.StreamObserver<mclab.HelloResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HELLO, responseObserver);
    }

    /**
     */
    public void lock(mclab.LockRequest request,
        io.grpc.stub.StreamObserver<mclab.LockResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LOCK, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_EDIT_CONFIG,
            asyncUnaryCall(
              new MethodHandlers<
                mclab.EditConfigRequest,
                mclab.EditConfigResponse>(
                  this, METHODID_EDIT_CONFIG)))
          .addMethod(
            METHOD_GET_CONFIG,
            asyncUnaryCall(
              new MethodHandlers<
                mclab.GetConfigRequest,
                mclab.GetConfigResponse>(
                  this, METHODID_GET_CONFIG)))
          .addMethod(
            METHOD_HELLO,
            asyncUnaryCall(
              new MethodHandlers<
                mclab.HelloRequest,
                mclab.HelloResponse>(
                  this, METHODID_HELLO)))
          .addMethod(
            METHOD_LOCK,
            asyncUnaryCall(
              new MethodHandlers<
                mclab.LockRequest,
                mclab.LockResponse>(
                  this, METHODID_LOCK)))
          .build();
    }
  }

  /**
   */
  public static final class DeviceConfigStub extends io.grpc.stub.AbstractStub<DeviceConfigStub> {
    private DeviceConfigStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DeviceConfigStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DeviceConfigStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DeviceConfigStub(channel, callOptions);
    }

    /**
     */
    public void editConfig(mclab.EditConfigRequest request,
        io.grpc.stub.StreamObserver<mclab.EditConfigResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_EDIT_CONFIG, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getConfig(mclab.GetConfigRequest request,
        io.grpc.stub.StreamObserver<mclab.GetConfigResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_CONFIG, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void hello(mclab.HelloRequest request,
        io.grpc.stub.StreamObserver<mclab.HelloResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HELLO, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void lock(mclab.LockRequest request,
        io.grpc.stub.StreamObserver<mclab.LockResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LOCK, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DeviceConfigBlockingStub extends io.grpc.stub.AbstractStub<DeviceConfigBlockingStub> {
    private DeviceConfigBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DeviceConfigBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DeviceConfigBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DeviceConfigBlockingStub(channel, callOptions);
    }

    /**
     */
    public mclab.EditConfigResponse editConfig(mclab.EditConfigRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_EDIT_CONFIG, getCallOptions(), request);
    }

    /**
     */
    public mclab.GetConfigResponse getConfig(mclab.GetConfigRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_CONFIG, getCallOptions(), request);
    }

    /**
     */
    public mclab.HelloResponse hello(mclab.HelloRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HELLO, getCallOptions(), request);
    }

    /**
     */
    public mclab.LockResponse lock(mclab.LockRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LOCK, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DeviceConfigFutureStub extends io.grpc.stub.AbstractStub<DeviceConfigFutureStub> {
    private DeviceConfigFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DeviceConfigFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DeviceConfigFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DeviceConfigFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<mclab.EditConfigResponse> editConfig(
        mclab.EditConfigRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_EDIT_CONFIG, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<mclab.GetConfigResponse> getConfig(
        mclab.GetConfigRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_CONFIG, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<mclab.HelloResponse> hello(
        mclab.HelloRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HELLO, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<mclab.LockResponse> lock(
        mclab.LockRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LOCK, getCallOptions()), request);
    }
  }

  private static final int METHODID_EDIT_CONFIG = 0;
  private static final int METHODID_GET_CONFIG = 1;
  private static final int METHODID_HELLO = 2;
  private static final int METHODID_LOCK = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DeviceConfigImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DeviceConfigImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EDIT_CONFIG:
          serviceImpl.editConfig((mclab.EditConfigRequest) request,
              (io.grpc.stub.StreamObserver<mclab.EditConfigResponse>) responseObserver);
          break;
        case METHODID_GET_CONFIG:
          serviceImpl.getConfig((mclab.GetConfigRequest) request,
              (io.grpc.stub.StreamObserver<mclab.GetConfigResponse>) responseObserver);
          break;
        case METHODID_HELLO:
          serviceImpl.hello((mclab.HelloRequest) request,
              (io.grpc.stub.StreamObserver<mclab.HelloResponse>) responseObserver);
          break;
        case METHODID_LOCK:
          serviceImpl.lock((mclab.LockRequest) request,
              (io.grpc.stub.StreamObserver<mclab.LockResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class DeviceConfigDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return mclab.AP_proto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DeviceConfigGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DeviceConfigDescriptorSupplier())
              .addMethod(METHOD_EDIT_CONFIG)
              .addMethod(METHOD_GET_CONFIG)
              .addMethod(METHOD_HELLO)
              .addMethod(METHOD_LOCK)
              .build();
        }
      }
    }
    return result;
  }
}
