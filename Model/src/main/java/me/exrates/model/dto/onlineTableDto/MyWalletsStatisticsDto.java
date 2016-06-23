package me.exrates.model.dto.onlineTableDto;

import me.exrates.model.dto.onlineTableDto.OnlineTableDto;

/**
 * Created by Valk
 */
public class MyWalletsStatisticsDto extends OnlineTableDto {
    private String currencyName;
    private String activeBalance;

    public MyWalletsStatisticsDto() {
        this.needRefresh = true;
    }

    public MyWalletsStatisticsDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/
    @Override
    public int hashCode() {
        int result = currencyName != null ? currencyName.hashCode() : 0;
        result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
        return result;
    }
    /*getters setters*/

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getActiveBalance() {
        return activeBalance;
    }

    public void setActiveBalance(String activeBalance) {
        this.activeBalance = activeBalance;
    }
}
