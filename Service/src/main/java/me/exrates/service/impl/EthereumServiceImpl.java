package me.exrates.service.impl;

import me.exrates.service.EthereumService;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

/**
 * Created by ajet on 28.03.2017.
 */
@Service
//@PropertySource("classpath:/merchants/ethereum.properties")
public class EthereumServiceImpl implements EthereumService{

    @Override
    public void start() {

        Web3j web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();


        String password = "12345";
//        String destinationDir = "C:\\Etherum1\\keystore";
        String destinationDir = "//data//Etherum//keystore";
        File destination = new File(destinationDir);

        String fileName = "";
        try {
            fileName = WalletUtils.generateLightNewWalletFile(password, destination);

        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        System.out.println(fileName);

    }
}
