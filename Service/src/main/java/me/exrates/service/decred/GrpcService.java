package me.exrates.service.decred;


import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import me.exrates.service.decred.rpc.Api;
import me.exrates.service.decred.rpc.WalletServiceGrpc;
import org.apache.commons.codec.binary.Base64;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

public class GrpcService {

        private static ManagedChannel channel = null;




        public static void main(String[] args) {
            try {
                ClassLoader loader = GrpcService.class.getClassLoader();
                InputStream streamCert = loader.getResourceAsStream("ca.crt");
                channel = NettyChannelBuilder.forAddress("localhost", 9111)
                        .sslContext(GrpcSslContexts
                                .forClient()
                                .trustManager(streamCert)
                                .build())
                        .build();
            } catch (Exception e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
            getTransactions();
            channel.shutdown();
        }

    private static void newAddress() {
        WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(channel);
        Api.NextAddressResponse response = stub.nextAddress(Api.NextAddressRequest
                .newBuilder()
                .setKind(Api.NextAddressRequest.Kind.BIP0044_INTERNAL)
                .setAccount(0)
                .setGapPolicy(Api.NextAddressRequest.GapPolicy.GAP_POLICY_IGNORE)
                .build());
        System.out.println("address " + response.getAddress() + " pk " + response.getPublicKey());
    }

        private static void getBalance() {
            WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(channel);
            Api.BalanceResponse response = stub.balance(Api.BalanceRequest.getDefaultInstance());
            System.out.println(response);
            System.out.println("spendable " + response.getSpendable() + " total " + response.getTotal() + " unconfirmed " + response.getUnconfirmed());
        }

        private static void getTransactions() {
            WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(channel);
            Iterator<Api.GetTransactionsResponse> response = stub.getTransactions(Api.GetTransactionsRequest
                    .newBuilder()
                    .setStartingBlockHeight(255561)
                    .build());

            while (response.hasNext())
            {
                Api.GetTransactionsResponse txResp = response.next();
                List<Api.TransactionDetails> transactionDetails = txResp.getMinedTransactions().getTransactionsList();
                Api.BlockDetails blockDetails = txResp.getMinedTransactions();
                int block = blockDetails.getHeight();
                System.out.println(block);
                transactionDetails.forEach(tr-> {
                    Api.TransactionDetails.TransactionType transactionType = tr.getTransactionType();
                    System.out.println("tx type " + transactionType.name());
                    tr.getCreditsList().forEach(dl-> {
                        System.out.println(dl);
                        String address = dl.getAddress();
                        System.out.println("address " + address);
                        long amount = dl.getAmount();
                        System.out.println("amount credit " + amount);
                    });
                    ByteString hash = tr.getHash();
                    tr.getDescriptorForType();
                    try {
                        Base64 base64 = new Base64();
                        System.out.println(base64.encodeToString(hash.toString("ISO-8859-1").getBytes()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    System.out.println("-__________________________-");
                });
            }
        }
}
