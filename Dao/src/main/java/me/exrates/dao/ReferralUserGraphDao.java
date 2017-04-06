package me.exrates.dao;

import me.exrates.model.dto.ReferralInfoDto;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralUserGraphDao {

    void create(int child, int parent);

    Integer getParent(Integer child);
    
    List<Integer> getChildrenForParentAndBlock(Integer parent);
    
    void changeReferralParent(Integer formerParent, Integer newParent);

    List<ReferralInfoDto> getInfoAboutFirstLevRefs(int userId, int profitUser);
}
