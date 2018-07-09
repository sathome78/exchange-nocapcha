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
public final class MessageVerificationServiceGrpc {

  private MessageVerificationServiceGrpc() {}

  public static final String SERVICE_NAME = "me.exrates.service.decred.walletrpc.MessageVerificationService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest,
      me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse> METHOD_VERIFY_MESSAGE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest, me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.MessageVerificationService", "VerifyMessage"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MessageVerificationServiceStub newStub(io.grpc.Channel channel) {
    return new MessageVerificationServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MessageVerificationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new MessageVerificationServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MessageVerificationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new MessageVerificationServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class MessageVerificationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void verifyMessage(me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_VERIFY_MESSAGE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_VERIFY_MESSAGE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest,
                me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse>(
                  this, METHODID_VERIFY_MESSAGE)))
          .build();
    }
  }

  /**
   */
  public static final class MessageVerificationServiceStub extends io.grpc.stub.AbstractStub<MessageVerificationServiceStub> {
    private MessageVerificationServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MessageVerificationServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MessageVerificationServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MessageVerificationServiceStub(channel, callOptions);
    }

    /**
     */
    public void verifyMessage(me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_VERIFY_MESSAGE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MessageVerificationServiceBlockingStub extends io.grpc.stub.AbstractStub<MessageVerificationServiceBlockingStub> {
    private MessageVerificationServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MessageVerificationServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MessageVerificationServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MessageVerificationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse verifyMessage(me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_VERIFY_MESSAGE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MessageVerificationServiceFutureStub extends io.grpc.stub.AbstractStub<MessageVerificationServiceFutureStub> {
    private MessageVerificationServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MessageVerificationServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MessageVerificationServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MessageVerificationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse> verifyMessage(
        me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_VERIFY_MESSAGE, getCallOptions()), request);
    }
  }

  private static final int METHODID_VERIFY_MESSAGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MessageVerificationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MessageVerificationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VERIFY_MESSAGE:
          serviceImpl.verifyMessage((me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.VerifyMessageResponse>) responseObserver);
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

  private static final class MessageVerificationServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.walletrpc.WalletApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MessageVerificationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MessageVerificationServiceDescriptorSupplier())
              .addMethod(METHOD_VERIFY_MESSAGE)
              .build();
        }
      }
    }
    return result;
  }
}
