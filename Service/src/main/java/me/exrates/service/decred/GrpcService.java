package me.exrates.service.decred;


import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import me.exrates.service.decred.rpc.Api;
import me.exrates.service.decred.rpc.WalletServiceGrpc;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class GrpcService {

        ManagedChannel channel = null;


        public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
            ManagedChannel channel = null;
            try {
                ClassLoader loader = GrpcService.class.getClassLoader();
                InputStream streamCert = loader.getResourceAsStream("ca.crt");



                System.out.println(streamCert.available());

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
            System.out.println("connected");

            WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(channel);

            Api.NextAddressResponse response = stub.nextAddress(Api.NextAddressRequest
                    .newBuilder()
                    .setKind(Api.NextAddressRequest.Kind.BIP0044_INTERNAL)
                    .setAccount(0)
                    .setGapPolicy(Api.NextAddressRequest.GapPolicy.GAP_POLICY_IGNORE).build());
            System.out.println(response.getAddress());
            System.out.println("send request");
            System.out.println(response);
            channel.shutdown();
        }
}
