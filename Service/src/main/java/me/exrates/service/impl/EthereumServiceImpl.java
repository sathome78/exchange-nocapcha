package me.exrates.service.impl;

import me.exrates.service.EthereumService;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

import java.util.concurrent.TimeUnit;

/**
 * Created by ajet on 28.03.2017.
 */
@Service
//@PropertySource("classpath:/merchants/ethereum.properties")
public class EthereumServiceImpl implements EthereumService{

    @Override
    public void start() throws Exception {

        Web3j web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        Web3ClientVersion web3ClientVersion = null;
        web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();

        Subscription subscription = web3.blockObservable(false).subscribe(block -> {
            System.out.println("Sweet, block number " + block.getBlock().getNumber() + " has just been created");
        }, Throwable::printStackTrace);

        TimeUnit.MINUTES.sleep(2);
        subscription.unsubscribe();



//        String password = "12345";
//        String destinationDir = "C:\\Etherum1\\keystore";
////        String destinationDir = "//data//ethereum//keystore";
//        File destination = new File(destinationDir);
//
//        String fileName = "";
//        fileName = WalletUtils.generateLightNewWalletFile(password, destination);

    }
}
