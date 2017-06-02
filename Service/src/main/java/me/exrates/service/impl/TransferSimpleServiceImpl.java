package me.exrates.service.impl;

import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.service.TransferSimpleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransferSimpleServiceImpl implements TransferSimpleService {

  private static final Logger logger = LogManager.getLogger("merchant");

  @Override
  public Map<String, String> transfer(TransferRequestCreateDto transferRequestCreateDto) {
    return new HashMap<String, String>();
  }

}
