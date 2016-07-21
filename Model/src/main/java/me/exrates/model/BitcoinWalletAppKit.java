package me.exrates.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Context;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.utils.BriefLogFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
@PropertySource("classpath:bitcoinj.properties")
public class BitcoinWalletAppKit {

    private final Logger LOG = LogManager.getLogger("merchant");
    private final Context context;

    private @Value("${bitcoinj.wallet_dump_dir}") String dumpDir;
    private @Value("${bitcoinj.wallet_file_prefix}") String filePrefix;

    private WalletAppKit kit;

    @Autowired
    public BitcoinWalletAppKit(final Context context) {
        this.context = context;
    }

    @PostConstruct
    public void startupWallet() {
        BriefLogFormatter.init();
        kit = new WalletAppKit(context, new File(dumpDir), filePrefix);
        kit.setAutoSave(true);
        kit.startAsync();
        kit.awaitRunning();
        LOG.info("Wallet balance: " + kit.wallet().getBalance().longValue());
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