package me.exrates.service.impl;

import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.TransferVoucherFreeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class TransferVoucherFreeServiceImpl implements TransferVoucherFreeService {

  private static final Logger logger = LogManager.getLogger("merchant");

  @Autowired
  private AlgorithmService algorithmService;

  @Override
  public Map<String, String> transfer(TransferRequestCreateDto transferRequestCreateDto) {
    String hash = algorithmService.sha256(new StringJoiner(":")
        .add(transferRequestCreateDto.getId().toString())
        .add(transferRequestCreateDto.getAmount().toString())
        .add(transferRequestCreateDto.getCurrencyName())
        .add(transferRequestCreateDto.getRecipient())
        .toString()
        .toUpperCase());
    return new HashMap<String, String>() {{
      put("hash", hash);
    }};
  }

}
