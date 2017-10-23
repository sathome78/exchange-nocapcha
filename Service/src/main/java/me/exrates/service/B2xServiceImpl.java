package me.exrates.service;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.Map;

/**
 * Created by Maks on 23.10.2017.
 */
public class B2xServiceImpl implements IRefillable, IWithdrawable {

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException();
    }

    @Override
    public Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    public Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        throw new RuntimeException();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new RuntimeException();
    }

    @Override
    public Boolean createdRefillRequestRecordNeeded() {
        return false;
    }

    @Override
    public Boolean needToCreateRefillRequestRecord() {
        return false;
    }

    @Override
    public Boolean toMainAccountTransferringConfirmNeeded() {
        return false;
    }

    @Override
    public Boolean generatingAdditionalRefillAddressAvailable() {
        return true;
    }

    @Override
    public Boolean additionalFieldForRefillIsUsed() {
        return false;
    }
}
