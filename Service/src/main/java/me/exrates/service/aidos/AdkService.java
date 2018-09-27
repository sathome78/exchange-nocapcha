package me.exrates.service.aidos;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.service.MerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.List;

public interface AdkService extends IRefillable, IWithdrawable {


    @Override
    default Boolean createdRefillRequestRecordNeeded() {
        return false;
    }

    @Override
    default Boolean needToCreateRefillRequestRecord() {
        return false;
    }

    @Override
    default Boolean toMainAccountTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default Boolean generatingAdditionalRefillAddressAvailable() {
        return false;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return false;
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    Merchant getMerchant();

    Currency getCurrency();

    MerchantService getMerchantService();

    RefillRequestAcceptDto createRequest(TxReceivedByAddressFlatDto transactionDto);

    void putOnBchExam(RefillRequestAcceptDto requestAcceptDto);

    String getBalance();

    List<BtcTransactionHistoryDto> listAllTransactions();

    void unlockWallet(String password);
}
