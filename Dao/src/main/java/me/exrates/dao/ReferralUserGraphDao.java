package me.exrates.dao;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralUserGraphDao {

    void create(int child, int parent);

    Integer getParent(Integer child);
}
