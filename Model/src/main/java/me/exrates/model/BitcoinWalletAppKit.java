package me.exrates.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.*;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Arrays;

import static java.util.Arrays.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
@PropertySource("classpath:bitcoinj.properties")
public class BitcoinWalletAppKit {

    private final Logger LOG = LogManager.getLogger("merchant");

    private @Value("${bitcoinj.wallet_dump_dir}") String dumpDir;
    private @Value("${bitcoinj.wallet_file_prefix}") String filePrefix;
    private @Value("${bitcoinj.net}") String net;

    private WalletAppKit kit;


    @PostConstruct
    public void startupWallet() {
        BriefLogFormatter.init();
        try {
            final Context context = new Context(net.equals("main") ? MainNetParams.get() : TestNet3Params.get());
            kit = new WalletAppKit(context, new File(dumpDir), filePrefix);
            if (context.getParams().equals(MainNetParams.get())) { // If running main bitcoin network - restore deterministic wallet
                kit.restoreWalletFromSeed(
                        new DeterministicSeed(
                                "cool surface park flavor theory liberty action donor unlock toy subway gain",
                                MnemonicCode.toSeed(Arrays.asList("cool", "surface", "park", "flavor", "theory", "liberty", "action", "donor", "unlock", "toy", "subway", "gain"), null),
                                "", // Empty passphrase
                                1469197851L
                        ));
            }
            kit.startAsync();
            kit.awaitRunning();
            LOG.info("Wallet balance in Satoshi : " + kit.wallet().getBalance().longValue());
        } catch (final Exception e) {
            LOG.fatal(e);
            throw new BeanInitializationException("Could not instantiate bitcoin wallet");
        }
    }

    @PreDestroy
    public void cleanUp() {
        kit.stopAsync();
        kit.awaitTerminated();
    }

    public WalletAppKit kit() {
        return kit;
    }
}