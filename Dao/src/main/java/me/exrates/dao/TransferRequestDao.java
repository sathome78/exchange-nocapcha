package me.exrates.dao;

import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.enums.invoice.InvoiceStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ValkSam on 02.06.2017.
 */
public interface TransferRequestDao {
  int create(TransferRequestCreateDto transferRequest);

  Optional<TransferRequestFlatDto> getFlatByIdAndBlock(int id);

  Optional<TransferRequestFlatDto> getFlatById(int id);

  Optional<TransferRequestFlatDto> getFlatByHashAndStatus(String hash, Integer requiredStatus, boolean block);

  void setStatusById(Integer id, InvoiceStatus newStatus);

  void setRecipientById(Integer id, Integer recipientId);

  List<TransferRequestFlatDto> findRequestsByStatusAndMerchant(Integer merchantId, List<Integer> statusId);

  void setHashById(Integer id, Map<String, String> params);
}
