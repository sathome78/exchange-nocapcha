package me.exrates.service.util;

import me.exrates.service.Rep;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * Created by Maks on 14.09.2017.
 */
public class EthTokensWrapperGenerator {

    private EthTokensWrapperGenerator() {
    }

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "d:/eth/rep.bin",
                "d:/eth/rep.abi",
                "-o",
                "c:/Users/Maks/IdeaProjects/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service"});
    }


    private void exeprimental() throws IOException, CipherException {
        String url = "http://46.37.202.178:56639/";
        Web3j web3j = Web3j.build(new HttpService(url));
        Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/walletfile");
        Rep contract = Rep.load("0xE94327D07Fc17907b4DB788E5aDf2ed424adDff6", web3j, credentials, GAS_PRICE, GAS_LIMIT);
        rx.Observable<Rep.TransferEventResponse> observable = contract.transferEventObservable();
        observable.subscribe(s->System.out.println(s));
    }
}
