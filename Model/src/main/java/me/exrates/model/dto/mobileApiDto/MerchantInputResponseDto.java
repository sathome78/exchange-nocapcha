package me.exrates.model.dto.mobileApiDto;

import me.exrates.model.enums.MerchantApiResponseType;

import java.net.HttpCookie;
import java.util.List;

/**
 * Created by OLEG on 19.10.2016.
 */
public class MerchantInputResponseDto {
    private MerchantApiResponseType type;
    private String walletNumber;
    private Object data;
    private String qr;

    public MerchantApiResponseType getType() {
        return type;
    }

    public void setType(MerchantApiResponseType type) {
        this.type = type;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    @Override
    public String toString() {
        return "MerchantInputResponseDto{" +
                "type='" + type + '\'' +
                ", walletNumber='" + walletNumber + '\'' +
                ", data='" + data + '\'' +
                ", qr='" + qr + '\'' +
                '}';
    }
}
