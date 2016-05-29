package me.exrates.service;

import me.exrates.model.ReferralLevel;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralService {

    List<ReferralLevel> findAllReferralLevels();

    int updateReferralLevel(int level, int oldLevelId, BigDecimal percent);

    void bindChildAndParent(int childUserId, int parentUserId);
}
