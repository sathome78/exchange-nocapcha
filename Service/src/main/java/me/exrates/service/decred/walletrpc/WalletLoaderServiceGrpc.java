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
public final class WalletLoaderServiceGrpc {

  private WalletLoaderServiceGrpc() {}

  public static final String SERVICE_NAME = "me.exrates.service.decred.walletrpc.WalletLoaderService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse> METHOD_WALLET_EXISTS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest, me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "WalletExists"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest,
      me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse> METHOD_CREATE_WALLET =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest, me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "CreateWallet"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest,
      me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse> METHOD_CREATE_WATCHING_ONLY_WALLET =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest, me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "CreateWatchingOnlyWallet"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest,
      me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse> METHOD_OPEN_WALLET =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest, me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "OpenWallet"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest,
      me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse> METHOD_CLOSE_WALLET =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest, me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "CloseWallet"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest,
      me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse> METHOD_START_CONSENSUS_RPC =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest, me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "StartConsensusRpc"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest,
      me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse> METHOD_DISCOVER_ADDRESSES =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest, me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "DiscoverAddresses"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse> METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest, me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "SubscribeToBlockNotifications"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest,
      me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse> METHOD_FETCH_HEADERS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest, me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletLoaderService", "FetchHeaders"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WalletLoaderServiceStub newStub(io.grpc.Channel channel) {
    return new WalletLoaderServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WalletLoaderServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new WalletLoaderServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WalletLoaderServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new WalletLoaderServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class WalletLoaderServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void walletExists(me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_WALLET_EXISTS, responseObserver);
    }

    /**
     */
    public void createWallet(me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_WALLET, responseObserver);
    }

    /**
     */
    public void createWatchingOnlyWallet(me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_WATCHING_ONLY_WALLET, responseObserver);
    }

    /**
     */
    public void openWallet(me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_OPEN_WALLET, responseObserver);
    }

    /**
     */
    public void closeWallet(me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CLOSE_WALLET, responseObserver);
    }

    /**
     */
    public void startConsensusRpc(me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_START_CONSENSUS_RPC, responseObserver);
    }

    /**
     */
    public void discoverAddresses(me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DISCOVER_ADDRESSES, responseObserver);
    }

    /**
     */
    public void subscribeToBlockNotifications(me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS, responseObserver);
    }

    /**
     */
    public void fetchHeaders(me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FETCH_HEADERS, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_WALLET_EXISTS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse>(
                  this, METHODID_WALLET_EXISTS)))
          .addMethod(
            METHOD_CREATE_WALLET,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest,
                me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse>(
                  this, METHODID_CREATE_WALLET)))
          .addMethod(
            METHOD_CREATE_WATCHING_ONLY_WALLET,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest,
                me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse>(
                  this, METHODID_CREATE_WATCHING_ONLY_WALLET)))
          .addMethod(
            METHOD_OPEN_WALLET,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest,
                me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse>(
                  this, METHODID_OPEN_WALLET)))
          .addMethod(
            METHOD_CLOSE_WALLET,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest,
                me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse>(
                  this, METHODID_CLOSE_WALLET)))
          .addMethod(
            METHOD_START_CONSENSUS_RPC,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest,
                me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse>(
                  this, METHODID_START_CONSENSUS_RPC)))
          .addMethod(
            METHOD_DISCOVER_ADDRESSES,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest,
                me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse>(
                  this, METHODID_DISCOVER_ADDRESSES)))
          .addMethod(
            METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse>(
                  this, METHODID_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS)))
          .addMethod(
            METHOD_FETCH_HEADERS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest,
                me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse>(
                  this, METHODID_FETCH_HEADERS)))
          .build();
    }
  }

  /**
   */
  public static final class WalletLoaderServiceStub extends io.grpc.stub.AbstractStub<WalletLoaderServiceStub> {
    private WalletLoaderServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletLoaderServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletLoaderServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletLoaderServiceStub(channel, callOptions);
    }

    /**
     */
    public void walletExists(me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_WALLET_EXISTS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createWallet(me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_WALLET, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createWatchingOnlyWallet(me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_WATCHING_ONLY_WALLET, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void openWallet(me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_OPEN_WALLET, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void closeWallet(me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CLOSE_WALLET, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void startConsensusRpc(me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_START_CONSENSUS_RPC, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void discoverAddresses(me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DISCOVER_ADDRESSES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToBlockNotifications(me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fetchHeaders(me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FETCH_HEADERS, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class WalletLoaderServiceBlockingStub extends io.grpc.stub.AbstractStub<WalletLoaderServiceBlockingStub> {
    private WalletLoaderServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletLoaderServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletLoaderServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletLoaderServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse walletExists(me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_WALLET_EXISTS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse createWallet(me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_WALLET, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse createWatchingOnlyWallet(me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_WATCHING_ONLY_WALLET, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse openWallet(me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_OPEN_WALLET, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse closeWallet(me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CLOSE_WALLET, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse startConsensusRpc(me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_START_CONSENSUS_RPC, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse discoverAddresses(me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DISCOVER_ADDRESSES, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse subscribeToBlockNotifications(me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse fetchHeaders(me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FETCH_HEADERS, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class WalletLoaderServiceFutureStub extends io.grpc.stub.AbstractStub<WalletLoaderServiceFutureStub> {
    private WalletLoaderServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletLoaderServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletLoaderServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletLoaderServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse> walletExists(
        me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_WALLET_EXISTS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse> createWallet(
        me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_WALLET, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse> createWatchingOnlyWallet(
        me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_WATCHING_ONLY_WALLET, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse> openWallet(
        me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_OPEN_WALLET, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse> closeWallet(
        me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CLOSE_WALLET, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse> startConsensusRpc(
        me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_START_CONSENSUS_RPC, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse> discoverAddresses(
        me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DISCOVER_ADDRESSES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse> subscribeToBlockNotifications(
        me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse> fetchHeaders(
        me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FETCH_HEADERS, getCallOptions()), request);
    }
  }

  private static final int METHODID_WALLET_EXISTS = 0;
  private static final int METHODID_CREATE_WALLET = 1;
  private static final int METHODID_CREATE_WATCHING_ONLY_WALLET = 2;
  private static final int METHODID_OPEN_WALLET = 3;
  private static final int METHODID_CLOSE_WALLET = 4;
  private static final int METHODID_START_CONSENSUS_RPC = 5;
  private static final int METHODID_DISCOVER_ADDRESSES = 6;
  private static final int METHODID_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS = 7;
  private static final int METHODID_FETCH_HEADERS = 8;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final WalletLoaderServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WalletLoaderServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_WALLET_EXISTS:
          serviceImpl.walletExists((me.exrates.service.decred.walletrpc.WalletApi.WalletExistsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.WalletExistsResponse>) responseObserver);
          break;
        case METHODID_CREATE_WALLET:
          serviceImpl.createWallet((me.exrates.service.decred.walletrpc.WalletApi.CreateWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateWalletResponse>) responseObserver);
          break;
        case METHODID_CREATE_WATCHING_ONLY_WALLET:
          serviceImpl.createWatchingOnlyWallet((me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateWatchingOnlyWalletResponse>) responseObserver);
          break;
        case METHODID_OPEN_WALLET:
          serviceImpl.openWallet((me.exrates.service.decred.walletrpc.WalletApi.OpenWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.OpenWalletResponse>) responseObserver);
          break;
        case METHODID_CLOSE_WALLET:
          serviceImpl.closeWallet((me.exrates.service.decred.walletrpc.WalletApi.CloseWalletRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CloseWalletResponse>) responseObserver);
          break;
        case METHODID_START_CONSENSUS_RPC:
          serviceImpl.startConsensusRpc((me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StartConsensusRpcResponse>) responseObserver);
          break;
        case METHODID_DISCOVER_ADDRESSES:
          serviceImpl.discoverAddresses((me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.DiscoverAddressesResponse>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS:
          serviceImpl.subscribeToBlockNotifications((me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SubscribeToBlockNotificationsResponse>) responseObserver);
          break;
        case METHODID_FETCH_HEADERS:
          serviceImpl.fetchHeaders((me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.FetchHeadersResponse>) responseObserver);
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

  private static final class WalletLoaderServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.walletrpc.WalletApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WalletLoaderServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WalletLoaderServiceDescriptorSupplier())
              .addMethod(METHOD_WALLET_EXISTS)
              .addMethod(METHOD_CREATE_WALLET)
              .addMethod(METHOD_CREATE_WATCHING_ONLY_WALLET)
              .addMethod(METHOD_OPEN_WALLET)
              .addMethod(METHOD_CLOSE_WALLET)
              .addMethod(METHOD_START_CONSENSUS_RPC)
              .addMethod(METHOD_DISCOVER_ADDRESSES)
              .addMethod(METHOD_SUBSCRIBE_TO_BLOCK_NOTIFICATIONS)
              .addMethod(METHOD_FETCH_HEADERS)
              .build();
        }
      }
    }
    return result;
  }
}
