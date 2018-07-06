package me.exrates.service.decred;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcService {

        public static void main(String[] args) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9111).useTransportSecurity().
                    .usePlaintext(true)
                    .build();

            HelloServiceGrpc.HelloServiceBlockingStub stub
                    = HelloServiceGrpc.newBlockingStub(channel);

            HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
                    .setFirstName("Baeldung")
                    .setLastName("gRPC")
                    .build());

            channel.shutdown();
        }
}
