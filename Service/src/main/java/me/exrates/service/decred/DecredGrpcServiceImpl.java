package me.exrates.service.decred;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslProvider;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.decred.rpc.Api;
import me.exrates.service.decred.rpc.WalletServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;

@PropertySource("classpath:/merchants/decred.properties")
@Service
@Log4j2(topic = "decred")
public class DecredGrpcServiceImpl implements DecredGrpcService{

    private @Value("${decred.host}")String host;
    private @Value("${decred.port}")String port;

    ManagedChannel channel = null;

    @PostConstruct
    private void init() {
        connect();
    }


    private void connect() {
        log.debug("connect");
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            InputStream stream = loader.getResourceAsStream("ca.crt");
            log.debug("stream size {}", stream.available());
            channel = NettyChannelBuilder.forAddress(host, Integer.valueOf(port))
                    .sslContext(GrpcSslContexts
                        .forClient()
                        .trustManager(stream)
                        .build())
                    .build();
        } catch (Exception e) {
            System.out.println(e);
            log.error(e);
            throw new RuntimeException(e);
        }
        log.debug("channel created");
    }

    private void checkConnect() {
        if (channel.isShutdown()) {
            connect();
        }
    }

    @Override
    public Api.NextAddressResponse getNewAddress() {
        checkConnect();
        WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(channel);
        return stub.nextAddress(Api.NextAddressRequest
                .newBuilder()
                .setKind(Api.NextAddressRequest.Kind.BIP0044_INTERNAL)
                .setAccount(0)
                .setGapPolicy(Api.NextAddressRequest.GapPolicy.GAP_POLICY_IGNORE)
                .build());
    }

    @PreDestroy
    private void destroy() {
        channel.shutdown();
    }
}
