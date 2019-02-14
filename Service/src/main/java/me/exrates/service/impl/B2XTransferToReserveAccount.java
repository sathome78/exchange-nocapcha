package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class B2XTransferToReserveAccount {

    private final static String RESERVE_ADDRESS = "1P6fgfBhPtcuLPrG2tScVY4QG5owiHnzst";

    @Autowired
    @Qualifier("b2xServiceImpl")
    private BitcoinService bitcoinService;

    public void transferToReserveAccountFromNode(int countTransactions, String amount){
        try {
            List<String> allTx = new ArrayList<>();
            for (int i = 0; i < countTransactions; i++) {
                Map<String, String> txId = bitcoinService.withdraw(WithdrawMerchantOperationDto.builder().accountTo(RESERVE_ADDRESS).amount(amount).build());
                allTx.add(txId.get("hash"));
            }

            saveToExcelFile(allTx);

            log.debug("B2X TRANSFER | Transactions count: {}", allTx.size());
        }catch (Exception e){
            log.error("B2X TRANSFER | ERROR {}", e);
        }

    }

    public void getBlockForBitcoin(){
        log.debug("***** B2X TRANSFER | ACTUAL FEE: {}", bitcoinService.getActualFee());

        log.debug("***** B2X TRANSFER | BALANCE: {} | какой-то там баланс №1: {} | какой-то там баланс №2: {} | TRANSACTION COUNT: {}",
                bitcoinService.getWalletInfo().getBalance(), bitcoinService.getWalletInfo().getUnconfirmedBalance(),
                bitcoinService.getWalletInfo().getConfirmedNonSpendableBalance(), bitcoinService.getWalletInfo().getTransactionCount());

        log.debug("***** BITCOIN | NEW ADDRESS: {}", bitcoinService.getNewAddressForAdmin());
    }

    private void saveToExcelFile(List<String> transactionsId){
        try {
            B2XTransferReportTransactionId.generate(transactionsId);
        } catch (Exception e) {
            log.error("B2X TRANSFER | Save to excel ERROR file: {}", e);
        }
    }

}
