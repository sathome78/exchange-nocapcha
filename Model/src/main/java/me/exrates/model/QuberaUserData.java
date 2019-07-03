package me.exrates.model;

import me.exrates.model.dto.AccountCreateDto;
import org.apache.commons.lang3.StringUtils;

public class QuberaUserData {

    private static final String SLASH = "/";

    private int userId;
    private String email;
    private String accountNumber;
    private String iban;
    private int currencyId;
    private String countryCode;
    private String firsName;
    private String lastName;
    private String address;
    private String city;

    public QuberaUserData() {
    }

    public static QuberaUserData of(AccountCreateDto accountCreateDto, int userId, int currencyId) {
        QuberaUserData quberaUserData = new QuberaUserData();
        quberaUserData.setUserId(userId);
        quberaUserData.setEmail(accountCreateDto.getEmail());
        quberaUserData.setAddress(accountCreateDto.getAddress());
        quberaUserData.setCity(accountCreateDto.getCity());
        quberaUserData.setCountryCode(accountCreateDto.getCountryCode());
        quberaUserData.setCurrencyId(currencyId);
        quberaUserData.setFirsName(accountCreateDto.getFirstName());
        quberaUserData.setLastName(accountCreateDto.getLastName());

        return quberaUserData;
    }

    public String buildAccountString() {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(firsName).append(StringUtils.SPACE).append(lastName).append(SLASH);
        nameBuilder.append(countryCode).append(SLASH);
        nameBuilder.append(userId).append(SLASH);
        nameBuilder.append(address).append(SLASH);
        nameBuilder.append(city);
        return nameBuilder.toString();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFirsName() {
        return firsName;
    }

    public void setFirsName(String firsName) {
        this.firsName = firsName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
