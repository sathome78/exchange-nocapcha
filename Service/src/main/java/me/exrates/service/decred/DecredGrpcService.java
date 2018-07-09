package me.exrates.service.decred;

import me.exrates.service.decred.rpc.Api;

public interface DecredGrpcService {
    Api.NextAddressResponse getNewAddress();
}
