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
public final class WalletServiceGrpc {

  private WalletServiceGrpc() {}

  public static final String SERVICE_NAME = "me.exrates.service.decred.walletrpc.WalletService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.PingRequest,
      me.exrates.service.decred.walletrpc.WalletApi.PingResponse> METHOD_PING =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.PingRequest, me.exrates.service.decred.walletrpc.WalletApi.PingResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "Ping"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PingRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PingResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest,
      me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse> METHOD_NETWORK =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest, me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "Network"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest,
      me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse> METHOD_ACCOUNT_NUMBER =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest, me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "AccountNumber"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse> METHOD_ACCOUNTS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest, me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "Accounts"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest,
      me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse> METHOD_BALANCE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest, me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "Balance"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest,
      me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse> METHOD_GET_TRANSACTION =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest, me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "GetTransaction"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse> METHOD_GET_TRANSACTIONS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest, me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "GetTransactions"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse> METHOD_GET_TICKETS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest, me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "GetTickets"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest,
      me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse> METHOD_TICKET_PRICE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest, me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "TicketPrice"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest,
      me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse> METHOD_STAKE_INFO =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest, me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "StakeInfo"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest,
      me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse> METHOD_BLOCK_INFO =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest, me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "BlockInfo"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest,
      me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse> METHOD_BEST_BLOCK =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest, me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "BestBlock"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse> METHOD_TRANSACTION_NOTIFICATIONS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest, me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "TransactionNotifications"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse> METHOD_ACCOUNT_NOTIFICATIONS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest, me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "AccountNotifications"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsResponse> METHOD_CONFIRMATION_NOTIFICATIONS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsRequest, me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "ConfirmationNotifications"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest,
      me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse> METHOD_CHANGE_PASSPHRASE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest, me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "ChangePassphrase"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest,
      me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse> METHOD_RENAME_ACCOUNT =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest, me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "RenameAccount"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.RescanRequest,
      me.exrates.service.decred.walletrpc.WalletApi.RescanResponse> METHOD_RESCAN =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.RescanRequest, me.exrates.service.decred.walletrpc.WalletApi.RescanResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "Rescan"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.RescanRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.RescanResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest,
      me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse> METHOD_NEXT_ACCOUNT =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest, me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "NextAccount"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest,
      me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse> METHOD_NEXT_ADDRESS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest, me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "NextAddress"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest,
      me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse> METHOD_IMPORT_PRIVATE_KEY =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest, me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "ImportPrivateKey"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest,
      me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse> METHOD_IMPORT_SCRIPT =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest, me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "ImportScript"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest,
      me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse> METHOD_FUND_TRANSACTION =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest, me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "FundTransaction"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse> METHOD_UNSPENT_OUTPUTS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest, me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "UnspentOutputs"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest,
      me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse> METHOD_CONSTRUCT_TRANSACTION =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest, me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "ConstructTransaction"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse> METHOD_SIGN_TRANSACTION =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest, me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "SignTransaction"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse> METHOD_SIGN_TRANSACTIONS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest, me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "SignTransactions"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest,
      me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse> METHOD_CREATE_SIGNATURE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest, me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "CreateSignature"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest,
      me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse> METHOD_PUBLISH_TRANSACTION =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest, me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "PublishTransaction"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse> METHOD_PUBLISH_UNMINED_TRANSACTIONS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest, me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "PublishUnminedTransactions"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse> METHOD_PURCHASE_TICKETS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest, me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "PurchaseTickets"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse> METHOD_REVOKE_TICKETS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest, me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "RevokeTickets"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest,
      me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse> METHOD_LOAD_ACTIVE_DATA_FILTERS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest, me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "LoadActiveDataFilters"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse> METHOD_SIGN_MESSAGE =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest, me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "SignMessage"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest,
      me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse> METHOD_SIGN_MESSAGES =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest, me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "SignMessages"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest,
      me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse> METHOD_VALIDATE_ADDRESS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest, me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "ValidateAddress"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest,
      me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse> METHOD_COMMITTED_TICKETS =
      io.grpc.MethodDescriptor.<me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest, me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "me.exrates.service.decred.walletrpc.WalletService", "CommittedTickets"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WalletServiceStub newStub(io.grpc.Channel channel) {
    return new WalletServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WalletServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new WalletServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WalletServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new WalletServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class WalletServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public void ping(me.exrates.service.decred.walletrpc.WalletApi.PingRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PING, responseObserver);
    }

    /**
     */
    public void network(me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NETWORK, responseObserver);
    }

    /**
     */
    public void accountNumber(me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ACCOUNT_NUMBER, responseObserver);
    }

    /**
     */
    public void accounts(me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ACCOUNTS, responseObserver);
    }

    /**
     */
    public void balance(me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_BALANCE, responseObserver);
    }

    /**
     */
    public void getTransaction(me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_TRANSACTION, responseObserver);
    }

    /**
     */
    public void getTransactions(me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_TRANSACTIONS, responseObserver);
    }

    /**
     */
    public void getTickets(me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_TICKETS, responseObserver);
    }

    /**
     */
    public void ticketPrice(me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_TICKET_PRICE, responseObserver);
    }

    /**
     */
    public void stakeInfo(me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_STAKE_INFO, responseObserver);
    }

    /**
     */
    public void blockInfo(me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_BLOCK_INFO, responseObserver);
    }

    /**
     */
    public void bestBlock(me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_BEST_BLOCK, responseObserver);
    }

    /**
     * <pre>
     * Notifications
     * </pre>
     */
    public void transactionNotifications(me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_TRANSACTION_NOTIFICATIONS, responseObserver);
    }

    /**
     */
    public void accountNotifications(me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ACCOUNT_NOTIFICATIONS, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsRequest> confirmationNotifications(
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_CONFIRMATION_NOTIFICATIONS, responseObserver);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public void changePassphrase(me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CHANGE_PASSPHRASE, responseObserver);
    }

    /**
     */
    public void renameAccount(me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_RENAME_ACCOUNT, responseObserver);
    }

    /**
     */
    public void rescan(me.exrates.service.decred.walletrpc.WalletApi.RescanRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RescanResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_RESCAN, responseObserver);
    }

    /**
     */
    public void nextAccount(me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NEXT_ACCOUNT, responseObserver);
    }

    /**
     */
    public void nextAddress(me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NEXT_ADDRESS, responseObserver);
    }

    /**
     */
    public void importPrivateKey(me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_IMPORT_PRIVATE_KEY, responseObserver);
    }

    /**
     */
    public void importScript(me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_IMPORT_SCRIPT, responseObserver);
    }

    /**
     */
    public void fundTransaction(me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FUND_TRANSACTION, responseObserver);
    }

    /**
     */
    public void unspentOutputs(me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_UNSPENT_OUTPUTS, responseObserver);
    }

    /**
     */
    public void constructTransaction(me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CONSTRUCT_TRANSACTION, responseObserver);
    }

    /**
     */
    public void signTransaction(me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SIGN_TRANSACTION, responseObserver);
    }

    /**
     */
    public void signTransactions(me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SIGN_TRANSACTIONS, responseObserver);
    }

    /**
     */
    public void createSignature(me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_SIGNATURE, responseObserver);
    }

    /**
     */
    public void publishTransaction(me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PUBLISH_TRANSACTION, responseObserver);
    }

    /**
     */
    public void publishUnminedTransactions(me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PUBLISH_UNMINED_TRANSACTIONS, responseObserver);
    }

    /**
     */
    public void purchaseTickets(me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PURCHASE_TICKETS, responseObserver);
    }

    /**
     */
    public void revokeTickets(me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_REVOKE_TICKETS, responseObserver);
    }

    /**
     */
    public void loadActiveDataFilters(me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LOAD_ACTIVE_DATA_FILTERS, responseObserver);
    }

    /**
     */
    public void signMessage(me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SIGN_MESSAGE, responseObserver);
    }

    /**
     */
    public void signMessages(me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SIGN_MESSAGES, responseObserver);
    }

    /**
     */
    public void validateAddress(me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_VALIDATE_ADDRESS, responseObserver);
    }

    /**
     */
    public void committedTickets(me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_COMMITTED_TICKETS, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_PING,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.PingRequest,
                me.exrates.service.decred.walletrpc.WalletApi.PingResponse>(
                  this, METHODID_PING)))
          .addMethod(
            METHOD_NETWORK,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest,
                me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse>(
                  this, METHODID_NETWORK)))
          .addMethod(
            METHOD_ACCOUNT_NUMBER,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest,
                me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse>(
                  this, METHODID_ACCOUNT_NUMBER)))
          .addMethod(
            METHOD_ACCOUNTS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse>(
                  this, METHODID_ACCOUNTS)))
          .addMethod(
            METHOD_BALANCE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest,
                me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse>(
                  this, METHODID_BALANCE)))
          .addMethod(
            METHOD_GET_TRANSACTION,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest,
                me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse>(
                  this, METHODID_GET_TRANSACTION)))
          .addMethod(
            METHOD_GET_TRANSACTIONS,
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse>(
                  this, METHODID_GET_TRANSACTIONS)))
          .addMethod(
            METHOD_GET_TICKETS,
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse>(
                  this, METHODID_GET_TICKETS)))
          .addMethod(
            METHOD_TICKET_PRICE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest,
                me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse>(
                  this, METHODID_TICKET_PRICE)))
          .addMethod(
            METHOD_STAKE_INFO,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest,
                me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse>(
                  this, METHODID_STAKE_INFO)))
          .addMethod(
            METHOD_BLOCK_INFO,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest,
                me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse>(
                  this, METHODID_BLOCK_INFO)))
          .addMethod(
            METHOD_BEST_BLOCK,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest,
                me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse>(
                  this, METHODID_BEST_BLOCK)))
          .addMethod(
            METHOD_TRANSACTION_NOTIFICATIONS,
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse>(
                  this, METHODID_TRANSACTION_NOTIFICATIONS)))
          .addMethod(
            METHOD_ACCOUNT_NOTIFICATIONS,
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse>(
                  this, METHODID_ACCOUNT_NOTIFICATIONS)))
          .addMethod(
            METHOD_CONFIRMATION_NOTIFICATIONS,
            asyncBidiStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsResponse>(
                  this, METHODID_CONFIRMATION_NOTIFICATIONS)))
          .addMethod(
            METHOD_CHANGE_PASSPHRASE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest,
                me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse>(
                  this, METHODID_CHANGE_PASSPHRASE)))
          .addMethod(
            METHOD_RENAME_ACCOUNT,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest,
                me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse>(
                  this, METHODID_RENAME_ACCOUNT)))
          .addMethod(
            METHOD_RESCAN,
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.RescanRequest,
                me.exrates.service.decred.walletrpc.WalletApi.RescanResponse>(
                  this, METHODID_RESCAN)))
          .addMethod(
            METHOD_NEXT_ACCOUNT,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest,
                me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse>(
                  this, METHODID_NEXT_ACCOUNT)))
          .addMethod(
            METHOD_NEXT_ADDRESS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest,
                me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse>(
                  this, METHODID_NEXT_ADDRESS)))
          .addMethod(
            METHOD_IMPORT_PRIVATE_KEY,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest,
                me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse>(
                  this, METHODID_IMPORT_PRIVATE_KEY)))
          .addMethod(
            METHOD_IMPORT_SCRIPT,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest,
                me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse>(
                  this, METHODID_IMPORT_SCRIPT)))
          .addMethod(
            METHOD_FUND_TRANSACTION,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest,
                me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse>(
                  this, METHODID_FUND_TRANSACTION)))
          .addMethod(
            METHOD_UNSPENT_OUTPUTS,
            asyncServerStreamingCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse>(
                  this, METHODID_UNSPENT_OUTPUTS)))
          .addMethod(
            METHOD_CONSTRUCT_TRANSACTION,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest,
                me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse>(
                  this, METHODID_CONSTRUCT_TRANSACTION)))
          .addMethod(
            METHOD_SIGN_TRANSACTION,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse>(
                  this, METHODID_SIGN_TRANSACTION)))
          .addMethod(
            METHOD_SIGN_TRANSACTIONS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse>(
                  this, METHODID_SIGN_TRANSACTIONS)))
          .addMethod(
            METHOD_CREATE_SIGNATURE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest,
                me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse>(
                  this, METHODID_CREATE_SIGNATURE)))
          .addMethod(
            METHOD_PUBLISH_TRANSACTION,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest,
                me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse>(
                  this, METHODID_PUBLISH_TRANSACTION)))
          .addMethod(
            METHOD_PUBLISH_UNMINED_TRANSACTIONS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse>(
                  this, METHODID_PUBLISH_UNMINED_TRANSACTIONS)))
          .addMethod(
            METHOD_PURCHASE_TICKETS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse>(
                  this, METHODID_PURCHASE_TICKETS)))
          .addMethod(
            METHOD_REVOKE_TICKETS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse>(
                  this, METHODID_REVOKE_TICKETS)))
          .addMethod(
            METHOD_LOAD_ACTIVE_DATA_FILTERS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest,
                me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse>(
                  this, METHODID_LOAD_ACTIVE_DATA_FILTERS)))
          .addMethod(
            METHOD_SIGN_MESSAGE,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse>(
                  this, METHODID_SIGN_MESSAGE)))
          .addMethod(
            METHOD_SIGN_MESSAGES,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest,
                me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse>(
                  this, METHODID_SIGN_MESSAGES)))
          .addMethod(
            METHOD_VALIDATE_ADDRESS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest,
                me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse>(
                  this, METHODID_VALIDATE_ADDRESS)))
          .addMethod(
            METHOD_COMMITTED_TICKETS,
            asyncUnaryCall(
              new MethodHandlers<
                me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest,
                me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse>(
                  this, METHODID_COMMITTED_TICKETS)))
          .build();
    }
  }

  /**
   */
  public static final class WalletServiceStub extends io.grpc.stub.AbstractStub<WalletServiceStub> {
    private WalletServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public void ping(me.exrates.service.decred.walletrpc.WalletApi.PingRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void network(me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NETWORK, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void accountNumber(me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ACCOUNT_NUMBER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void accounts(me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ACCOUNTS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void balance(me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_BALANCE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTransaction(me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_TRANSACTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTransactions(me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_GET_TRANSACTIONS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTickets(me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_GET_TICKETS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ticketPrice(me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_TICKET_PRICE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stakeInfo(me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_STAKE_INFO, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void blockInfo(me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_BLOCK_INFO, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void bestBlock(me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_BEST_BLOCK, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Notifications
     * </pre>
     */
    public void transactionNotifications(me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_TRANSACTION_NOTIFICATIONS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void accountNotifications(me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_ACCOUNT_NOTIFICATIONS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsRequest> confirmationNotifications(
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(METHOD_CONFIRMATION_NOTIFICATIONS, getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public void changePassphrase(me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CHANGE_PASSPHRASE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void renameAccount(me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_RENAME_ACCOUNT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void rescan(me.exrates.service.decred.walletrpc.WalletApi.RescanRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RescanResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_RESCAN, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nextAccount(me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NEXT_ACCOUNT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void nextAddress(me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NEXT_ADDRESS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void importPrivateKey(me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_IMPORT_PRIVATE_KEY, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void importScript(me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_IMPORT_SCRIPT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fundTransaction(me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FUND_TRANSACTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void unspentOutputs(me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_UNSPENT_OUTPUTS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void constructTransaction(me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CONSTRUCT_TRANSACTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signTransaction(me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SIGN_TRANSACTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signTransactions(me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SIGN_TRANSACTIONS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createSignature(me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_SIGNATURE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void publishTransaction(me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PUBLISH_TRANSACTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void publishUnminedTransactions(me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PUBLISH_UNMINED_TRANSACTIONS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void purchaseTickets(me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PURCHASE_TICKETS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void revokeTickets(me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REVOKE_TICKETS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void loadActiveDataFilters(me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LOAD_ACTIVE_DATA_FILTERS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signMessage(me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SIGN_MESSAGE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void signMessages(me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SIGN_MESSAGES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void validateAddress(me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_VALIDATE_ADDRESS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void committedTickets(me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest request,
        io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_COMMITTED_TICKETS, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class WalletServiceBlockingStub extends io.grpc.stub.AbstractStub<WalletServiceBlockingStub> {
    private WalletServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public me.exrates.service.decred.walletrpc.WalletApi.PingResponse ping(me.exrates.service.decred.walletrpc.WalletApi.PingRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PING, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse network(me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NETWORK, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse accountNumber(me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ACCOUNT_NUMBER, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse accounts(me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ACCOUNTS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse balance(me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_BALANCE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse getTransaction(me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_TRANSACTION, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse> getTransactions(
        me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_GET_TRANSACTIONS, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse> getTickets(
        me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_GET_TICKETS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse ticketPrice(me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_TICKET_PRICE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse stakeInfo(me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_STAKE_INFO, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse blockInfo(me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_BLOCK_INFO, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse bestBlock(me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_BEST_BLOCK, getCallOptions(), request);
    }

    /**
     * <pre>
     * Notifications
     * </pre>
     */
    public java.util.Iterator<me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse> transactionNotifications(
        me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_TRANSACTION_NOTIFICATIONS, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse> accountNotifications(
        me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_ACCOUNT_NOTIFICATIONS, getCallOptions(), request);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse changePassphrase(me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CHANGE_PASSPHRASE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse renameAccount(me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_RENAME_ACCOUNT, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.walletrpc.WalletApi.RescanResponse> rescan(
        me.exrates.service.decred.walletrpc.WalletApi.RescanRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_RESCAN, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse nextAccount(me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NEXT_ACCOUNT, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse nextAddress(me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NEXT_ADDRESS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse importPrivateKey(me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_IMPORT_PRIVATE_KEY, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse importScript(me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_IMPORT_SCRIPT, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse fundTransaction(me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FUND_TRANSACTION, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse> unspentOutputs(
        me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_UNSPENT_OUTPUTS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse constructTransaction(me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CONSTRUCT_TRANSACTION, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse signTransaction(me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SIGN_TRANSACTION, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse signTransactions(me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SIGN_TRANSACTIONS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse createSignature(me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_SIGNATURE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse publishTransaction(me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PUBLISH_TRANSACTION, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse publishUnminedTransactions(me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PUBLISH_UNMINED_TRANSACTIONS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse purchaseTickets(me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PURCHASE_TICKETS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse revokeTickets(me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REVOKE_TICKETS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse loadActiveDataFilters(me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LOAD_ACTIVE_DATA_FILTERS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse signMessage(me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SIGN_MESSAGE, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse signMessages(me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SIGN_MESSAGES, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse validateAddress(me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_VALIDATE_ADDRESS, getCallOptions(), request);
    }

    /**
     */
    public me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse committedTickets(me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_COMMITTED_TICKETS, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class WalletServiceFutureStub extends io.grpc.stub.AbstractStub<WalletServiceFutureStub> {
    private WalletServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WalletServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.PingResponse> ping(
        me.exrates.service.decred.walletrpc.WalletApi.PingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse> network(
        me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NETWORK, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse> accountNumber(
        me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ACCOUNT_NUMBER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse> accounts(
        me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ACCOUNTS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse> balance(
        me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_BALANCE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse> getTransaction(
        me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_TRANSACTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse> ticketPrice(
        me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_TICKET_PRICE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse> stakeInfo(
        me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_STAKE_INFO, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse> blockInfo(
        me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_BLOCK_INFO, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse> bestBlock(
        me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_BEST_BLOCK, getCallOptions()), request);
    }

    /**
     * <pre>
     * Control
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse> changePassphrase(
        me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CHANGE_PASSPHRASE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse> renameAccount(
        me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_RENAME_ACCOUNT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse> nextAccount(
        me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NEXT_ACCOUNT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse> nextAddress(
        me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NEXT_ADDRESS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse> importPrivateKey(
        me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_IMPORT_PRIVATE_KEY, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse> importScript(
        me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_IMPORT_SCRIPT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse> fundTransaction(
        me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FUND_TRANSACTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse> constructTransaction(
        me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CONSTRUCT_TRANSACTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse> signTransaction(
        me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SIGN_TRANSACTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse> signTransactions(
        me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SIGN_TRANSACTIONS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse> createSignature(
        me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_SIGNATURE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse> publishTransaction(
        me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PUBLISH_TRANSACTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse> publishUnminedTransactions(
        me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PUBLISH_UNMINED_TRANSACTIONS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse> purchaseTickets(
        me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PURCHASE_TICKETS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse> revokeTickets(
        me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REVOKE_TICKETS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse> loadActiveDataFilters(
        me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LOAD_ACTIVE_DATA_FILTERS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse> signMessage(
        me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SIGN_MESSAGE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse> signMessages(
        me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SIGN_MESSAGES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse> validateAddress(
        me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_VALIDATE_ADDRESS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse> committedTickets(
        me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_COMMITTED_TICKETS, getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_NETWORK = 1;
  private static final int METHODID_ACCOUNT_NUMBER = 2;
  private static final int METHODID_ACCOUNTS = 3;
  private static final int METHODID_BALANCE = 4;
  private static final int METHODID_GET_TRANSACTION = 5;
  private static final int METHODID_GET_TRANSACTIONS = 6;
  private static final int METHODID_GET_TICKETS = 7;
  private static final int METHODID_TICKET_PRICE = 8;
  private static final int METHODID_STAKE_INFO = 9;
  private static final int METHODID_BLOCK_INFO = 10;
  private static final int METHODID_BEST_BLOCK = 11;
  private static final int METHODID_TRANSACTION_NOTIFICATIONS = 12;
  private static final int METHODID_ACCOUNT_NOTIFICATIONS = 13;
  private static final int METHODID_CHANGE_PASSPHRASE = 14;
  private static final int METHODID_RENAME_ACCOUNT = 15;
  private static final int METHODID_RESCAN = 16;
  private static final int METHODID_NEXT_ACCOUNT = 17;
  private static final int METHODID_NEXT_ADDRESS = 18;
  private static final int METHODID_IMPORT_PRIVATE_KEY = 19;
  private static final int METHODID_IMPORT_SCRIPT = 20;
  private static final int METHODID_FUND_TRANSACTION = 21;
  private static final int METHODID_UNSPENT_OUTPUTS = 22;
  private static final int METHODID_CONSTRUCT_TRANSACTION = 23;
  private static final int METHODID_SIGN_TRANSACTION = 24;
  private static final int METHODID_SIGN_TRANSACTIONS = 25;
  private static final int METHODID_CREATE_SIGNATURE = 26;
  private static final int METHODID_PUBLISH_TRANSACTION = 27;
  private static final int METHODID_PUBLISH_UNMINED_TRANSACTIONS = 28;
  private static final int METHODID_PURCHASE_TICKETS = 29;
  private static final int METHODID_REVOKE_TICKETS = 30;
  private static final int METHODID_LOAD_ACTIVE_DATA_FILTERS = 31;
  private static final int METHODID_SIGN_MESSAGE = 32;
  private static final int METHODID_SIGN_MESSAGES = 33;
  private static final int METHODID_VALIDATE_ADDRESS = 34;
  private static final int METHODID_COMMITTED_TICKETS = 35;
  private static final int METHODID_CONFIRMATION_NOTIFICATIONS = 36;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final WalletServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WalletServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((me.exrates.service.decred.walletrpc.WalletApi.PingRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PingResponse>) responseObserver);
          break;
        case METHODID_NETWORK:
          serviceImpl.network((me.exrates.service.decred.walletrpc.WalletApi.NetworkRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NetworkResponse>) responseObserver);
          break;
        case METHODID_ACCOUNT_NUMBER:
          serviceImpl.accountNumber((me.exrates.service.decred.walletrpc.WalletApi.AccountNumberRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountNumberResponse>) responseObserver);
          break;
        case METHODID_ACCOUNTS:
          serviceImpl.accounts((me.exrates.service.decred.walletrpc.WalletApi.AccountsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountsResponse>) responseObserver);
          break;
        case METHODID_BALANCE:
          serviceImpl.balance((me.exrates.service.decred.walletrpc.WalletApi.BalanceRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BalanceResponse>) responseObserver);
          break;
        case METHODID_GET_TRANSACTION:
          serviceImpl.getTransaction((me.exrates.service.decred.walletrpc.WalletApi.GetTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionResponse>) responseObserver);
          break;
        case METHODID_GET_TRANSACTIONS:
          serviceImpl.getTransactions((me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTransactionsResponse>) responseObserver);
          break;
        case METHODID_GET_TICKETS:
          serviceImpl.getTickets((me.exrates.service.decred.walletrpc.WalletApi.GetTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.GetTicketsResponse>) responseObserver);
          break;
        case METHODID_TICKET_PRICE:
          serviceImpl.ticketPrice((me.exrates.service.decred.walletrpc.WalletApi.TicketPriceRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TicketPriceResponse>) responseObserver);
          break;
        case METHODID_STAKE_INFO:
          serviceImpl.stakeInfo((me.exrates.service.decred.walletrpc.WalletApi.StakeInfoRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.StakeInfoResponse>) responseObserver);
          break;
        case METHODID_BLOCK_INFO:
          serviceImpl.blockInfo((me.exrates.service.decred.walletrpc.WalletApi.BlockInfoRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BlockInfoResponse>) responseObserver);
          break;
        case METHODID_BEST_BLOCK:
          serviceImpl.bestBlock((me.exrates.service.decred.walletrpc.WalletApi.BestBlockRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.BestBlockResponse>) responseObserver);
          break;
        case METHODID_TRANSACTION_NOTIFICATIONS:
          serviceImpl.transactionNotifications((me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.TransactionNotificationsResponse>) responseObserver);
          break;
        case METHODID_ACCOUNT_NOTIFICATIONS:
          serviceImpl.accountNotifications((me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.AccountNotificationsResponse>) responseObserver);
          break;
        case METHODID_CHANGE_PASSPHRASE:
          serviceImpl.changePassphrase((me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ChangePassphraseResponse>) responseObserver);
          break;
        case METHODID_RENAME_ACCOUNT:
          serviceImpl.renameAccount((me.exrates.service.decred.walletrpc.WalletApi.RenameAccountRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RenameAccountResponse>) responseObserver);
          break;
        case METHODID_RESCAN:
          serviceImpl.rescan((me.exrates.service.decred.walletrpc.WalletApi.RescanRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RescanResponse>) responseObserver);
          break;
        case METHODID_NEXT_ACCOUNT:
          serviceImpl.nextAccount((me.exrates.service.decred.walletrpc.WalletApi.NextAccountRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NextAccountResponse>) responseObserver);
          break;
        case METHODID_NEXT_ADDRESS:
          serviceImpl.nextAddress((me.exrates.service.decred.walletrpc.WalletApi.NextAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.NextAddressResponse>) responseObserver);
          break;
        case METHODID_IMPORT_PRIVATE_KEY:
          serviceImpl.importPrivateKey((me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ImportPrivateKeyResponse>) responseObserver);
          break;
        case METHODID_IMPORT_SCRIPT:
          serviceImpl.importScript((me.exrates.service.decred.walletrpc.WalletApi.ImportScriptRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ImportScriptResponse>) responseObserver);
          break;
        case METHODID_FUND_TRANSACTION:
          serviceImpl.fundTransaction((me.exrates.service.decred.walletrpc.WalletApi.FundTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.FundTransactionResponse>) responseObserver);
          break;
        case METHODID_UNSPENT_OUTPUTS:
          serviceImpl.unspentOutputs((me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.UnspentOutputResponse>) responseObserver);
          break;
        case METHODID_CONSTRUCT_TRANSACTION:
          serviceImpl.constructTransaction((me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConstructTransactionResponse>) responseObserver);
          break;
        case METHODID_SIGN_TRANSACTION:
          serviceImpl.signTransaction((me.exrates.service.decred.walletrpc.WalletApi.SignTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionResponse>) responseObserver);
          break;
        case METHODID_SIGN_TRANSACTIONS:
          serviceImpl.signTransactions((me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignTransactionsResponse>) responseObserver);
          break;
        case METHODID_CREATE_SIGNATURE:
          serviceImpl.createSignature((me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CreateSignatureResponse>) responseObserver);
          break;
        case METHODID_PUBLISH_TRANSACTION:
          serviceImpl.publishTransaction((me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PublishTransactionResponse>) responseObserver);
          break;
        case METHODID_PUBLISH_UNMINED_TRANSACTIONS:
          serviceImpl.publishUnminedTransactions((me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PublishUnminedTransactionsResponse>) responseObserver);
          break;
        case METHODID_PURCHASE_TICKETS:
          serviceImpl.purchaseTickets((me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.PurchaseTicketsResponse>) responseObserver);
          break;
        case METHODID_REVOKE_TICKETS:
          serviceImpl.revokeTickets((me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.RevokeTicketsResponse>) responseObserver);
          break;
        case METHODID_LOAD_ACTIVE_DATA_FILTERS:
          serviceImpl.loadActiveDataFilters((me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.LoadActiveDataFiltersResponse>) responseObserver);
          break;
        case METHODID_SIGN_MESSAGE:
          serviceImpl.signMessage((me.exrates.service.decred.walletrpc.WalletApi.SignMessageRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignMessageResponse>) responseObserver);
          break;
        case METHODID_SIGN_MESSAGES:
          serviceImpl.signMessages((me.exrates.service.decred.walletrpc.WalletApi.SignMessagesRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.SignMessagesResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_ADDRESS:
          serviceImpl.validateAddress((me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ValidateAddressResponse>) responseObserver);
          break;
        case METHODID_COMMITTED_TICKETS:
          serviceImpl.committedTickets((me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsRequest) request,
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.CommittedTicketsResponse>) responseObserver);
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
        case METHODID_CONFIRMATION_NOTIFICATIONS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.confirmationNotifications(
              (io.grpc.stub.StreamObserver<me.exrates.service.decred.walletrpc.WalletApi.ConfirmationNotificationsResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class WalletServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return me.exrates.service.decred.walletrpc.WalletApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WalletServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WalletServiceDescriptorSupplier())
              .addMethod(METHOD_PING)
              .addMethod(METHOD_NETWORK)
              .addMethod(METHOD_ACCOUNT_NUMBER)
              .addMethod(METHOD_ACCOUNTS)
              .addMethod(METHOD_BALANCE)
              .addMethod(METHOD_GET_TRANSACTION)
              .addMethod(METHOD_GET_TRANSACTIONS)
              .addMethod(METHOD_GET_TICKETS)
              .addMethod(METHOD_TICKET_PRICE)
              .addMethod(METHOD_STAKE_INFO)
              .addMethod(METHOD_BLOCK_INFO)
              .addMethod(METHOD_BEST_BLOCK)
              .addMethod(METHOD_TRANSACTION_NOTIFICATIONS)
              .addMethod(METHOD_ACCOUNT_NOTIFICATIONS)
              .addMethod(METHOD_CONFIRMATION_NOTIFICATIONS)
              .addMethod(METHOD_CHANGE_PASSPHRASE)
              .addMethod(METHOD_RENAME_ACCOUNT)
              .addMethod(METHOD_RESCAN)
              .addMethod(METHOD_NEXT_ACCOUNT)
              .addMethod(METHOD_NEXT_ADDRESS)
              .addMethod(METHOD_IMPORT_PRIVATE_KEY)
              .addMethod(METHOD_IMPORT_SCRIPT)
              .addMethod(METHOD_FUND_TRANSACTION)
              .addMethod(METHOD_UNSPENT_OUTPUTS)
              .addMethod(METHOD_CONSTRUCT_TRANSACTION)
              .addMethod(METHOD_SIGN_TRANSACTION)
              .addMethod(METHOD_SIGN_TRANSACTIONS)
              .addMethod(METHOD_CREATE_SIGNATURE)
              .addMethod(METHOD_PUBLISH_TRANSACTION)
              .addMethod(METHOD_PUBLISH_UNMINED_TRANSACTIONS)
              .addMethod(METHOD_PURCHASE_TICKETS)
              .addMethod(METHOD_REVOKE_TICKETS)
              .addMethod(METHOD_LOAD_ACTIVE_DATA_FILTERS)
              .addMethod(METHOD_SIGN_MESSAGE)
              .addMethod(METHOD_SIGN_MESSAGES)
              .addMethod(METHOD_VALIDATE_ADDRESS)
              .addMethod(METHOD_COMMITTED_TICKETS)
              .build();
        }
      }
    }
    return result;
  }
}
