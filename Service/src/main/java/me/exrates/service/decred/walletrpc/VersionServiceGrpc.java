package me.exrates.service.decred.walletrpc;

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
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: walletApi.proto")
public final class VersionServiceGrpc {

  private VersionServiceGrpc() {}

  public static final String SERVICE_NAME = "me.exrates.service.decred.walletrpc.VersionService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.VersionRequest,
      me.exrates.service.decred.walletrpc.WalletApi.VersionResponse> METHOD_VERSION =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.VersionRequest, me.exrates.service.decred.walletrpc.WalletApi.VersionResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.VersionService", "Version"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.VersionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.VersionResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VersionServiceStub newStub(io.grpc.Channel channel) {
    return new VersionServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VersionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new VersionServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VersionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new VersionServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class VersionServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void version(me.exrates.service.decred.walletrpc.WalletApi.VersionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.VersionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_VERSION, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_VERSION,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.VersionRequest,
                me.exrates.service.decred.walletrpc.WalletApi.VersionResponse>(
                  this, METHODID_VERSION)))
          .build();
    }
  }

  /**
   */
  public static final class VersionServiceStub extends io.grpc.stub.AbstractStub<VersionServiceStub> {
    private VersionServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VersionServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VersionServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VersionServiceStub(channel, callOptions);
    }

    /**
     */
    public void version(me.exrates.service.decred.walletrpc.WalletApi.VersionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.VersionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_VERSION, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class VersionServiceBlockingStub extends io.grpc.stub.AbstractStub<VersionServiceBlockingStub> {
    private VersionServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VersionServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VersionServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VersionServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.VersionResponse version(me.exrates.service.decred.walletrpc.WalletApi.VersionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_VERSION, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class VersionServiceFutureStub extends io.grpc.stub.AbstractStub<VersionServiceFutureStub> {
    private VersionServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VersionServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VersionServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VersionServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.VersionResponse> version(
        me.exrates.service.decred.walletrpc.WalletApi.VersionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_VERSION, getCallOptions()), request);
    }
  }

  private static final int METHODID_VERSION = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final VersionServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(VersionServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VERSION:
          serviceImpl.version((me.exrates.service.decred.walletrpc.WalletApi.VersionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.VersionResponse>) responseObserver);
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

  private static final class VersionServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.walletrpc.WalletApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (VersionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VersionServiceDescriptorSupplier())
              .addMethod(METHOD_VERSION)
              .build();
        }
      }
    }
    return result;
  }
}
