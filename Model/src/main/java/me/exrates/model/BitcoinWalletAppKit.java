package me.exrates.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Context;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.DeterministicSeed;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;

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

    public void startupWallet() {
        BriefLogFormatter.init();
        try {
            final Context context = new Context(net.equals("main") ? MainNetParams.get() : TestNet3Params.get());
            final long creationTime;
            final String mnemonic;
            final DeterministicSeed seed;
            if (context.getParams().equals(MainNetParams.get())) {
                creationTime =  1469197851L;
                mnemonic = "comfort man panic blush parrot truck place degree cloth fiscal common usage";
            } else {
                creationTime = 1470405562L;
                mnemonic = "cake diesel gain private room lazy tank online miracle manual economy final";
            }
            kit = new WalletAppKit(context, new File(dumpDir), filePrefix);
            kit.restoreWalletFromSeed(
                    new DeterministicSeed(
                            mnemonic, // mnemonic code
                            null, // Should be null, mnemonic code above
                            "", // Empty passphrase
                            creationTime // Creation time (Unix time)
                    ));
            kit.startAsync();
            kit.awaitRunning();
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
