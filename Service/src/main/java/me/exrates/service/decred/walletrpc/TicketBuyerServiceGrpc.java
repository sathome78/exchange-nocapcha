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
public final class TicketBuyerServiceGrpc {

  private TicketBuyerServiceGrpc() {}

  public static final String SERVICE_NAME = "me.exrates.service.decred.walletrpc.TicketBuyerService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest,
      me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse> METHOD_START_AUTO_BUYER =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest, me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "StartAutoBuyer"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest,
      me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse> METHOD_STOP_AUTO_BUYER =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest, me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "StopAutoBuyer"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest,
      me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse> METHOD_TICKET_BUYER_CONFIG =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest, me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "TicketBuyerConfig"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse> METHOD_SET_ACCOUNT =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest, me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetAccount"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse> METHOD_SET_BALANCE_TO_MAINTAIN =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest, me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetBalanceToMaintain"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse> METHOD_SET_MAX_FEE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest, me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetMaxFee"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse> METHOD_SET_MAX_PRICE_RELATIVE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest, me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetMaxPriceRelative"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse> METHOD_SET_MAX_PRICE_ABSOLUTE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest, me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetMaxPriceAbsolute"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse> METHOD_SET_VOTING_ADDRESS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest, me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetVotingAddress"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse> METHOD_SET_POOL_ADDRESS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest, me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetPoolAddress"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse> METHOD_SET_POOL_FEES =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest, me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetPoolFees"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse> METHOD_SET_MAX_PER_BLOCK =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest, me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.TicketBuyerService", "SetMaxPerBlock"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TicketBuyerServiceStub newStub(io.grpc.Channel channel) {
    return new TicketBuyerServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TicketBuyerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TicketBuyerServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TicketBuyerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TicketBuyerServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class TicketBuyerServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void startAutoBuyer(me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_START_AUTO_BUYER, responseObserver);
    }

    /**
     */
    public void stopAutoBuyer(me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_STOP_AUTO_BUYER, responseObserver);
    }

    /**
     */
    public void ticketBuyerConfig(me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_TICKET_BUYER_CONFIG, responseObserver);
    }

    /**
     */
    public void setAccount(me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_ACCOUNT, responseObserver);
    }

    /**
     */
    public void setBalanceToMaintain(me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_BALANCE_TO_MAINTAIN, responseObserver);
    }

    /**
     */
    public void setMaxFee(me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_MAX_FEE, responseObserver);
    }

    /**
     */
    public void setMaxPriceRelative(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_MAX_PRICE_RELATIVE, responseObserver);
    }

    /**
     */
    public void setMaxPriceAbsolute(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_MAX_PRICE_ABSOLUTE, responseObserver);
    }

    /**
     */
    public void setVotingAddress(me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_VOTING_ADDRESS, responseObserver);
    }

    /**
     */
    public void setPoolAddress(me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_POOL_ADDRESS, responseObserver);
    }

    /**
     */
    public void setPoolFees(me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_POOL_FEES, responseObserver);
    }

    /**
     */
    public void setMaxPerBlock(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_MAX_PER_BLOCK, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_START_AUTO_BUYER,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest,
                me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse>(
                  this, METHODID_START_AUTO_BUYER)))
          .addMethod(
            METHOD_STOP_AUTO_BUYER,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest,
                me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse>(
                  this, METHODID_STOP_AUTO_BUYER)))
          .addMethod(
            METHOD_TICKET_BUYER_CONFIG,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest,
                me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse>(
                  this, METHODID_TICKET_BUYER_CONFIG)))
          .addMethod(
            METHOD_SET_ACCOUNT,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse>(
                  this, METHODID_SET_ACCOUNT)))
          .addMethod(
            METHOD_SET_BALANCE_TO_MAINTAIN,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse>(
                  this, METHODID_SET_BALANCE_TO_MAINTAIN)))
          .addMethod(
            METHOD_SET_MAX_FEE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse>(
                  this, METHODID_SET_MAX_FEE)))
          .addMethod(
            METHOD_SET_MAX_PRICE_RELATIVE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse>(
                  this, METHODID_SET_MAX_PRICE_RELATIVE)))
          .addMethod(
            METHOD_SET_MAX_PRICE_ABSOLUTE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse>(
                  this, METHODID_SET_MAX_PRICE_ABSOLUTE)))
          .addMethod(
            METHOD_SET_VOTING_ADDRESS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse>(
                  this, METHODID_SET_VOTING_ADDRESS)))
          .addMethod(
            METHOD_SET_POOL_ADDRESS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse>(
                  this, METHODID_SET_POOL_ADDRESS)))
          .addMethod(
            METHOD_SET_POOL_FEES,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse>(
                  this, METHODID_SET_POOL_FEES)))
          .addMethod(
            METHOD_SET_MAX_PER_BLOCK,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse>(
                  this, METHODID_SET_MAX_PER_BLOCK)))
          .build();
    }
  }

  /**
   */
  public static final class TicketBuyerServiceStub extends io.grpc.stub.AbstractStub<TicketBuyerServiceStub> {
    private TicketBuyerServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TicketBuyerServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TicketBuyerServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TicketBuyerServiceStub(channel, callOptions);
    }

    /**
     */
    public void startAutoBuyer(me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_START_AUTO_BUYER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stopAutoBuyer(me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_STOP_AUTO_BUYER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ticketBuyerConfig(me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_TICKET_BUYER_CONFIG, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setAccount(me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_ACCOUNT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setBalanceToMaintain(me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_BALANCE_TO_MAINTAIN, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxFee(me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_FEE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxPriceRelative(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_PRICE_RELATIVE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxPriceAbsolute(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_PRICE_ABSOLUTE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setVotingAddress(me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_VOTING_ADDRESS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPoolAddress(me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_POOL_ADDRESS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPoolFees(me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_POOL_FEES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMaxPerBlock(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_PER_BLOCK, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TicketBuyerServiceBlockingStub extends io.grpc.stub.AbstractStub<TicketBuyerServiceBlockingStub> {
    private TicketBuyerServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TicketBuyerServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TicketBuyerServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TicketBuyerServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse startAutoBuyer(me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_START_AUTO_BUYER, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse stopAutoBuyer(me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_STOP_AUTO_BUYER, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse ticketBuyerConfig(me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_TICKET_BUYER_CONFIG, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse setAccount(me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_ACCOUNT, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse setBalanceToMaintain(me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_BALANCE_TO_MAINTAIN, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse setMaxFee(me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_MAX_FEE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse setMaxPriceRelative(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_MAX_PRICE_RELATIVE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse setMaxPriceAbsolute(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_MAX_PRICE_ABSOLUTE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse setVotingAddress(me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_VOTING_ADDRESS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse setPoolAddress(me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_POOL_ADDRESS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse setPoolFees(me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_POOL_FEES, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse setMaxPerBlock(me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_MAX_PER_BLOCK, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TicketBuyerServiceFutureStub extends io.grpc.stub.AbstractStub<TicketBuyerServiceFutureStub> {
    private TicketBuyerServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TicketBuyerServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TicketBuyerServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TicketBuyerServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse> startAutoBuyer(
        me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_START_AUTO_BUYER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse> stopAutoBuyer(
        me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_STOP_AUTO_BUYER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse> ticketBuyerConfig(
        me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_TICKET_BUYER_CONFIG, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse> setAccount(
        me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_ACCOUNT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse> setBalanceToMaintain(
        me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_BALANCE_TO_MAINTAIN, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse> setMaxFee(
        me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_FEE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse> setMaxPriceRelative(
        me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_PRICE_RELATIVE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse> setMaxPriceAbsolute(
        me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_PRICE_ABSOLUTE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse> setVotingAddress(
        me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_VOTING_ADDRESS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse> setPoolAddress(
        me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_POOL_ADDRESS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse> setPoolFees(
        me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_POOL_FEES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse> setMaxPerBlock(
        me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_MAX_PER_BLOCK, getCallOptions()), request);
    }
  }

  private static final int METHODID_START_AUTO_BUYER = 0;
  private static final int METHODID_STOP_AUTO_BUYER = 1;
  private static final int METHODID_TICKET_BUYER_CONFIG = 2;
  private static final int METHODID_SET_ACCOUNT = 3;
  private static final int METHODID_SET_BALANCE_TO_MAINTAIN = 4;
  private static final int METHODID_SET_MAX_FEE = 5;
  private static final int METHODID_SET_MAX_PRICE_RELATIVE = 6;
  private static final int METHODID_SET_MAX_PRICE_ABSOLUTE = 7;
  private static final int METHODID_SET_VOTING_ADDRESS = 8;
  private static final int METHODID_SET_POOL_ADDRESS = 9;
  private static final int METHODID_SET_POOL_FEES = 10;
  private static final int METHODID_SET_MAX_PER_BLOCK = 11;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TicketBuyerServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TicketBuyerServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_START_AUTO_BUYER:
          serviceImpl.startAutoBuyer((me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StartAutoBuyerResponse>) responseObserver);
          break;
        case METHODID_STOP_AUTO_BUYER:
          serviceImpl.stopAutoBuyer((me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StopAutoBuyerResponse>) responseObserver);
          break;
        case METHODID_TICKET_BUYER_CONFIG:
          serviceImpl.ticketBuyerConfig((me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TicketBuyerConfigResponse>) responseObserver);
          break;
        case METHODID_SET_ACCOUNT:
          serviceImpl.setAccount((me.exrates.service.decred.walletrpc.WalletApi.SetAccountRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetAccountResponse>) responseObserver);
          break;
        case METHODID_SET_BALANCE_TO_MAINTAIN:
          serviceImpl.setBalanceToMaintain((me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetBalanceToMaintainResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_FEE:
          serviceImpl.setMaxFee((me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxFeeResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_PRICE_RELATIVE:
          serviceImpl.setMaxPriceRelative((me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceRelativeResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_PRICE_ABSOLUTE:
          serviceImpl.setMaxPriceAbsolute((me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPriceAbsoluteResponse>) responseObserver);
          break;
        case METHODID_SET_VOTING_ADDRESS:
          serviceImpl.setVotingAddress((me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetVotingAddressResponse>) responseObserver);
          break;
        case METHODID_SET_POOL_ADDRESS:
          serviceImpl.setPoolAddress((me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetPoolAddressResponse>) responseObserver);
          break;
        case METHODID_SET_POOL_FEES:
          serviceImpl.setPoolFees((me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetPoolFeesResponse>) responseObserver);
          break;
        case METHODID_SET_MAX_PER_BLOCK:
          serviceImpl.setMaxPerBlock((me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SetMaxPerBlockResponse>) responseObserver);
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

  private static final class TicketBuyerServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.walletrpc.WalletApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TicketBuyerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TicketBuyerServiceDescriptorSupplier())
              .addMethod(METHOD_START_AUTO_BUYER)
              .addMethod(METHOD_STOP_AUTO_BUYER)
              .addMethod(METHOD_TICKET_BUYER_CONFIG)
              .addMethod(METHOD_SET_ACCOUNT)
              .addMethod(METHOD_SET_BALANCE_TO_MAINTAIN)
              .addMethod(METHOD_SET_MAX_FEE)
              .addMethod(METHOD_SET_MAX_PRICE_RELATIVE)
              .addMethod(METHOD_SET_MAX_PRICE_ABSOLUTE)
              .addMethod(METHOD_SET_VOTING_ADDRESS)
              .addMethod(METHOD_SET_POOL_ADDRESS)
              .addMethod(METHOD_SET_POOL_FEES)
              .addMethod(METHOD_SET_MAX_PER_BLOCK)
              .build();
        }
      }
    }
    return result;
  }
}
