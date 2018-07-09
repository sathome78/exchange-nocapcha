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
public final class SeedServiceGrpc {

  private SeedServiceGrpc() {}

  public static final String SERVICE_NAME = "me.exrates.service.decred.walletrpc.SeedService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest,
      me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse> METHOD_GENERATE_RANDOM_SEED =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest, me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.SeedService", "GenerateRandomSeed"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest,
      me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse> METHOD_DECODE_SEED =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest, me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.SeedService", "DecodeSeed"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SeedServiceStub newStub(io.grpc.Channel channel) {
    return new SeedServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SeedServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SeedServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SeedServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SeedServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class SeedServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void generateRandomSeed(me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GENERATE_RANDOM_SEED, responseObserver);
    }

    /**
     */
    public void decodeSeed(me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DECODE_SEED, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GENERATE_RANDOM_SEED,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest,
                me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse>(
                  this, METHODID_GENERATE_RANDOM_SEED)))
          .addMethod(
            METHOD_DECODE_SEED,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest,
                me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse>(
                  this, METHODID_DECODE_SEED)))
          .build();
    }
  }

  /**
   */
  public static final class SeedServiceStub extends io.grpc.stub.AbstractStub<SeedServiceStub> {
    private SeedServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SeedServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SeedServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SeedServiceStub(channel, callOptions);
    }

    /**
     */
    public void generateRandomSeed(me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GENERATE_RANDOM_SEED, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void decodeSeed(me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DECODE_SEED, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SeedServiceBlockingStub extends io.grpc.stub.AbstractStub<SeedServiceBlockingStub> {
    private SeedServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SeedServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SeedServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SeedServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse generateRandomSeed(me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GENERATE_RANDOM_SEED, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse decodeSeed(me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DECODE_SEED, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SeedServiceFutureStub extends io.grpc.stub.AbstractStub<SeedServiceFutureStub> {
    private SeedServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SeedServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SeedServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SeedServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse> generateRandomSeed(
        me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GENERATE_RANDOM_SEED, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse> decodeSeed(
        me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DECODE_SEED, getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_RANDOM_SEED = 0;
  private static final int METHODID_DECODE_SEED = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SeedServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SeedServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GENERATE_RANDOM_SEED:
          serviceImpl.generateRandomSeed((me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GenerateRandomSeedResponse>) responseObserver);
          break;
        case METHODID_DECODE_SEED:
          serviceImpl.decodeSeed((me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.DecodeSeedResponse>) responseObserver);
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

  private static final class SeedServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.walletrpc.WalletApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SeedServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SeedServiceDescriptorSupplier())
              .addMethod(METHOD_GENERATE_RANDOM_SEED)
              .addMethod(METHOD_DECODE_SEED)
              .build();
        }
      }
    }
    return result;
  }
}
