package me.exrates.service.decred;


import com.sun.net.ssl.internal.ssl.Provider;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import me.exrates.service.decred.walletrpc.*;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.codec.Base64;

import javax.net.ssl.SSLException;
import javax.net.ssl.X509ExtendedKeyManager;
import java.io.File;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;


public class GrpcService {

        public static void main(String[] args) {
            ManagedChannel channel = null;
            try {
                ClassLoader loader = GrpcService.class.getClassLoader();
                /*InputStream stream = loader.getResourceAsStream("key.pem");*/
                InputStream streamCert = loader.getResourceAsStream("rpc.cert");
                /*InputStream streamKey = loader.getResourceAsStream("rpc.key");*/


                System.out.println(streamCert.available());

               /* byte[] targetArray = new byte[streamKey.available()];
                System.out.println(targetArray.length);
                CertificateFactory cf = CertificateFactory.getInstance("X509");

                X509Certificate cert=(X509Certificate)cf.generateCertificate(stream);*/

                /*
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(targetArray);
                KeyFactory factory = KeyFactory.getInstance("EC");
                PrivateKey privateKey = factory.generatePrivate(spec);*/

               /* KeyPairGenerator kpGen = KeyPairGenerator.getInstance("EC");
                kpGen.initialize(new ECGenParameterSpec("secp384r1"));
                KeyPair ecKP = kpGen.generateKeyPair();*/

                /*channel = NettyChannelBuilder.forAddress("localhost", 9111)
                        .sslContext(GrpcSslContexts.configure(SslContextBuilder.forClient().startTls(true).trustManager(streamCert)).build())
                        .build();*/
                channel = NettyChannelBuilder.forAddress("127.0.0.1", 9111)
                        .sslContext(GrpcSslContexts
                                .forClient()
                                /*.startTls(true)
                                .clientAuth(ClientAuth.OPTIONAL)*/
                                .trustManager(streamCert).build())
                        .build();
            } catch (Exception e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
            System.out.println("connected");

            /*WalletServiceGrpc.WalletServiceBlockingStub stub = WalletServiceGrpc.newBlockingStub(channel);*/
            /*WalletApi.NextAddressResponse response = stub.nextAddress(
                    WalletApi.NextAddressRequest.newBuilder()
                            .setAccount(0)
                            .setGapPolicy(WalletApi.NextAddressRequest.GapPolicy.GAP_POLICY_IGNORE)
                            .setKind(WalletApi.NextAddressRequest.Kind.BIP0044_EXTERNAL)
                            .build());
            System.out.println(response.getAddress());*/
            VersionServiceGrpc.VersionServiceBlockingStub versionStub = VersionServiceGrpc.newBlockingStub(channel);
            System.out.println("send request");
            WalletApi.VersionResponse response = versionStub.version(WalletApi.VersionRequest.newBuilder().build());
            System.out.println(response);
            channel.shutdown();
        }
}
