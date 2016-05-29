package me.exrates.service.impl;

import me.exrates.dao.ReferralLevelDao;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.model.ReferralLevel;
import me.exrates.service.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class ReferralServiceImpl implements ReferralService {

    private final ReferralLevelDao referralLevelDao;
    private final ReferralUserGraphDao referralUserGraphDao;

    /**
     * Maximum amount of percents
     */
    private final BigDecimal HUNDREDTH = BigDecimal.valueOf(100L);

    @Autowired
    public ReferralServiceImpl(final ReferralLevelDao referralLevelDao, final ReferralUserGraphDao referralUserGraphDao) {
        this.referralLevelDao = referralLevelDao;
        this.referralUserGraphDao = referralUserGraphDao;
    }

    @Override
    public List<ReferralLevel> findAllReferralLevels() {
        return referralLevelDao.findAll();
    }

    @Override
    public int updateReferralLevel(final int level, final int oldLevelId, final BigDecimal percent) {
        final BigDecimal oldLevelPercent = referralLevelDao.findById(oldLevelId).getPercent();
        System.out.println("OLD LEVEL PERCENT " + oldLevelPercent);
        if (referralLevelDao.getTotalLevelsPercent().subtract(oldLevelPercent).add(percent).compareTo(HUNDREDTH) > 0) {
            throw new IllegalStateException("The total amount of percents at all levels should not exceed 100%");
        }
        if (percent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Percent should be positive");
        }
        final ReferralLevel referralLevel = new ReferralLevel();
        referralLevel.setLevel(level);
        referralLevel.setPercent(percent);
        return referralLevelDao.create(referralLevel);
    }

    @Override
    public void bindChildAndParent(final int childUserId, final int parentUserId) {
        referralUserGraphDao.create(childUserId, parentUserId);
    }
}
