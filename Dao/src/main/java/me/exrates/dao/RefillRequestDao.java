package me.exrates.dao;

import me.exrates.model.dto.RefillRequestCreateDto;

/**
 * created by ValkSam
 */
public interface RefillRequestDao {

  int findActiveRequestsByMerchantIdAndUserIdForCurrentDate(Integer merchantId, Integer userId);

  int create(RefillRequestCreateDto request);
}
