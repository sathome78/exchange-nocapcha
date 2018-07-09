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
public final class AgendaServiceGrpc {

  private AgendaServiceGrpc() {}

  public static final String SERVICE_NAME = "me.exrates.service.decred.walletrpc.AgendaService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest,
      me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse> METHOD_AGENDAS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest, me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.AgendaService", "Agendas"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AgendaServiceStub newStub(io.grpc.Channel channel) {
    return new AgendaServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AgendaServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new AgendaServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AgendaServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new AgendaServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class AgendaServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void agendas(me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_AGENDAS, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_AGENDAS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest,
                me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse>(
                  this, METHODID_AGENDAS)))
          .build();
    }
  }

  /**
   */
  public static final class AgendaServiceStub extends io.grpc.stub.AbstractStub<AgendaServiceStub> {
    private AgendaServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgendaServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgendaServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgendaServiceStub(channel, callOptions);
    }

    /**
     */
    public void agendas(me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_AGENDAS, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AgendaServiceBlockingStub extends io.grpc.stub.AbstractStub<AgendaServiceBlockingStub> {
    private AgendaServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgendaServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgendaServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgendaServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse agendas(me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_AGENDAS, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AgendaServiceFutureStub extends io.grpc.stub.AbstractStub<AgendaServiceFutureStub> {
    private AgendaServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgendaServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgendaServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgendaServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse> agendas(
        me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_AGENDAS, getCallOptions()), request);
    }
  }

  private static final int METHODID_AGENDAS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AgendaServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AgendaServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_AGENDAS:
          serviceImpl.agendas((me.exrates.service.decred.walletrpc.WalletApi.AgendasRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AgendasResponse>) responseObserver);
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

  private static final class AgendaServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.walletrpc.WalletApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AgendaServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AgendaServiceDescriptorSupplier())
              .addMethod(METHOD_AGENDAS)
              .build();
        }
      }
    }
    return result;
  }
}
