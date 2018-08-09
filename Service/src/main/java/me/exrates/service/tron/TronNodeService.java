package me.exrates.service.tron;

import me.exrates.model.TronTransactionResponseDto;
import me.exrates.model.dto.TronNewAddressDto;
import me.exrates.model.dto.TronTransferDto;

public interface TronNodeService {

    TronNewAddressDto getNewAddress();

    TronTransactionResponseDto transferFunds(TronTransferDto tronTransferDto);
}
