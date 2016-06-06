package me.exrates.service;

import me.exrates.model.ExOrder;
import me.exrates.model.ReferralLevel;
import me.exrates.model.ReferralTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralService {

    String generateReferral(String userEmail);

    Optional<Integer> reduceReferralRef(String ref);

    void processReferral(ExOrder exOrder, final BigDecimal commissionAmount, int currencyId, int userId);

    List<ReferralTransaction> findAll(int userId);

    List<ReferralLevel> findAllReferralLevels();

    String getParentEmail(int childId);

    int updateReferralLevel(int level, int oldLevelId, BigDecimal percent);

    void bindChildAndParent(int childUserId, int parentUserId);

}
