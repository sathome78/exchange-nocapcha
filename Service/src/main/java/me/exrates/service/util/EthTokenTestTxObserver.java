package me.exrates.service.util;

import me.exrates.service.ethTokensWrappers.Rep;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

public class EthTokenTestTxObserver {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, CipherException {
        String data = "0xa9059cbb000000000000000000000000967975346803fbd816c41ad23a7edc67c5b547dc000000000000000000000000000000000000000000000018cdae8c9afa0b0c00";


        String url = "http://163.172.77.155:8545/";
        Web3j web3j = Web3j.build(new HttpService(url));
        System.out.println(web3j.ethAccounts().sendAsync().get().getAccounts());
        Credentials credentials = WalletUtils.loadCredentials("qwerty123",
                "H:/data/ethereum/main/keystore/UTC--2017-09-16T07-18-09.232818100Z--43b7e43cd130d0a26057e9081c6ffb4c1d808956");
        System.out.println("address: " + credentials.getAddress());
        Rep contract = Rep.load("0xE94327D07Fc17907b4DB788E5aDf2ed424adDff6", web3j, credentials, GAS_PRICE, GAS_LIMIT);
        System.out.println(contract);
        Future<Bool> future = contract.initialized();
        System.out.println("contract initialized " + future.get().getValue());
        System.out.println(contract.balanceOf(new Address(credentials.getAddress())).get().getValue());
        rx.Observable<Rep.TransferEventResponse> observable = contract.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST);
        observable.subscribe(p-> {
            System.out.println(p.from.toString() + " " + p.to.toString() + " " + p.value.getValue() + " " + p.txHash);
        });
    }
}
