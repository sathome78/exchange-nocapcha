package me.exrates.controller;

import me.exrates.controller.handler.EDCClientWebSocketHandler;
import me.exrates.service.EDCService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class UnsafeController {

    private final EDCClientWebSocketHandler blockchainEDC;
    private final EDCService edcService;

    private static final Logger LOGGER = LogManager.getLogger(UnsafeController.class);

    @Autowired
    public UnsafeController(final EDCClientWebSocketHandler blockchainEDC, final EDCService edcService) {
        this.blockchainEDC = blockchainEDC;
        this.edcService = edcService;
    }

    @RequestMapping(value = "unsafe/rescanEDCBlockchain")
    public ResponseEntity<String> rescanEDCBlockchain(@RequestParam("from") final int from, @RequestParam("to") final int to) {
        LOGGER.info("STARTING EDC BLOCKCHAIN RESCAN");
        final Thread job = new Thread(() -> blockchainEDC.rescanBlockchain(Math.abs(from), Math.abs(to)));
        job.start();
        return new ResponseEntity<>("Started blockchain rescan. Checkout merchant.log", HttpStatus.OK);
    }
}
