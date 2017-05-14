package me.exrates.model.dto.onlineTableDto;

/**
 * Created by Valk
 */
public class MyWalletsDetailedDto extends OnlineTableDto {
    private Integer id;
    private Integer userId;
    private Integer currencyId;
    private String currencyName;
    private String activeBalance;
    private String onConfirmation;
    private String onConfirmationStage;
    private String onConfirmationCount;
    private String reservedBalance;
    private String reservedByOrders;
    private String reservedByMerchant;

    public MyWalletsDetailedDto() {
        this.needRefresh = true;
    }

    public MyWalletsDetailedDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
        result = 31 * result + (onConfirmation != null ? onConfirmation.hashCode() : 0);
        result = 31 * result + (onConfirmationStage != null ? onConfirmationStage.hashCode() : 0);
        result = 31 * result + (reservedBalance != null ? reservedBalance.hashCode() : 0);
        result = 31 * result + (reservedByOrders != null ? reservedByOrders.hashCode() : 0);
        result = 31 * result + (reservedByMerchant != null ? reservedByMerchant.hashCode() : 0);
        return result;
    }
    /*getters setters*/

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getOnConfirmation() {
        return onConfirmation;
    }

    public void setOnConfirmation(String onConfirmation) {
        this.onConfirmation = onConfirmation;
    }

    public String getOnConfirmationStage() {
        return onConfirmationStage;
    }

    public void setOnConfirmationStage(String onConfirmationStage) {
        this.onConfirmationStage = onConfirmationStage;
    }

    public String getOnConfirmationCount() {
        return onConfirmationCount;
    }

    public void setOnConfirmationCount(String onConfirmationCount) {
        this.onConfirmationCount = onConfirmationCount;
    }

    public String getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(String reservedBalance) {
        this.reservedBalance = reservedBalance;
    }

    public String getReservedByOrders() {
        return reservedByOrders;
    }

    public void setReservedByOrders(String reservedByOrders) {
        this.reservedByOrders = reservedByOrders;
    }

    public String getReservedByMerchant() {
        return reservedByMerchant;
    }

    public void setReservedByMerchant(String reservedByMerchant) {
        this.reservedByMerchant = reservedByMerchant;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
}
