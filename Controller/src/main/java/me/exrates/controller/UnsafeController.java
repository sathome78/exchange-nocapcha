package me.exrates.controller;

import me.exrates.controller.handler.EDCClientWebSocketHandler;
import me.exrates.service.EDCService;
import me.exrates.service.impl.bitcoinWallet.TempBtcCoreService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class UnsafeController {

    private final EDCClientWebSocketHandler blockchainEDC;
    private final EDCService edcService;
    private final TempBtcCoreService tempBtcCoreService;

    private static final Logger LOGGER = LogManager.getLogger(UnsafeController.class);

    @Autowired
    public UnsafeController(final EDCClientWebSocketHandler blockchainEDC, final EDCService edcService) {
        this.blockchainEDC = blockchainEDC;
        this.edcService = edcService;
        this.tempBtcCoreService = new TempBtcCoreService();
    }

    @RequestMapping(value = "unsafe/rescanEDCBlockchain")
    public ResponseEntity<String> rescanEDCBlockchain(@RequestParam("from") final int from, @RequestParam("to") final int to) {
        LOGGER.info("STARTING EDC BLOCKCHAIN RESCAN");
        final Thread job = new Thread(() -> blockchainEDC.rescanBlockchain(Math.abs(from), Math.abs(to)));
        job.start();
        return new ResponseEntity<>("Started blockchain rescan. Checkout merchant.log", HttpStatus.OK);
    }

    @RequestMapping(value = "unsafe/rescanUnusedAccounts")
    public ResponseEntity<String> rescanUnusedAccounts(){
        try {
            LOGGER.info("STARTING EDC RESCAN UNUSED ACCOUNTS");
            final Thread job = new Thread(() -> edcService.rescanUnusedAccounts());
            job.start();
            return new ResponseEntity<>("Started EDC rescan unused accouts. Checkout merchant.log", HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error(e);
            return new ResponseEntity<>("Error EDC rescan unused accouts. Checkout merchant.log", HttpStatus.BAD_REQUEST);
        }
    }
    
    // Both methods below are designed to check btc core node accessibility via Exrates app on lk.exrates.me.
    //TODO remove after BtcCore update is ready
    
    
    @RequestMapping(value = "unsafe/initBtcCore")
    @ResponseBody
    public void initBtcCore() {
        tempBtcCoreService.initClientAndDaemon();
    }
    
    @RequestMapping(value = "unsafe/btcGetInfo")
    @ResponseBody
    public void btcGetInfo() {
        tempBtcCoreService.getInfo();
    }

}
