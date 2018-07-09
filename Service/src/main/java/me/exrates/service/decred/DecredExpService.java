package me.exrates.service.decred;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslProvider;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.decred.walletrpc.VersionServiceGrpc;
import me.exrates.service.decred.walletrpc.WalletApi;
import me.exrates.service.decred.walletrpc.WalletServiceGrpc;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.io.File;
import java.io.InputStream;

@Service
@Log4j2(topic = "decred")
public class DecredExpService {

    @PostConstruct
    private void init() {
        connect();
    }

    private void connect() {
        log.debug("connect");
        ManagedChannel channel = null;
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            InputStream stream = loader.getResourceAsStream("rpc.cert");
            log.debug("stream size {}", stream.available());
            channel = NettyChannelBuilder.forAddress("localhost", 9111)
                    .usePlaintext(false)
                    .directExecutor()
                    .sslContext(GrpcSslContexts.forClient().sslProvider(SslProvider.OPENSSL).trustManager(stream).build())
                    .build();
        } catch (Exception e) {
            System.out.println(e);
            log.error(e);
            throw new RuntimeException(e);
        }
        log.debug("channel created");
        /*WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(channel);
            WalletApi.NextAddressResponse response = stub.nextAddress(
                    WalletApi.NextAddressRequest.newBuilder()
                            .setAccount(0)
                            .setGapPolicy(WalletApi.NextAddressRequest.GapPolicy.GAP_POLICY_IGNORE)
                            .setKind(WalletApi.NextAddressRequest.Kind.BIP0044_EXTERNAL)
                            .build());
            System.out.println(response.getAddress());*/
        VersionServiceGrpc.VersionServiceBlockingStub versionStub = VersionServiceGrpc.newBlockingStub(channel);
        log.debug("stub created, reqest...");
        WalletApi.VersionResponse response = versionStub.version(WalletApi.VersionRequest.newBuilder().build());
        log.debug("response");
        System.out.println(response);
        log.debug(response);
        channel.shutdown();
    }
}
