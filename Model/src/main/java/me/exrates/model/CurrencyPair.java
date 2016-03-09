package me.exrates.model;

public class CurrencyPair {

    private Currency currency1;
    private Currency currency2;
    String name;

    public CurrencyPair() {
    }

    public Currency getCurrency1() {
        return currency1;
    }

    public void setCurrency1(Currency currency1) {
        this.currency1 = currency1;
    }

    public Currency getCurrency2() {
        return currency2;
    }

    public void setCurrency2(Currency currency2) {
        this.currency2 = currency2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyPair that = (CurrencyPair) o;

        if (currency1 != null ? !currency1.equals(that.currency1) : that.currency1 != null) return false;
        if (currency2 != null ? !currency2.equals(that.currency2) : that.currency2 != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = currency1 != null ? currency1.hashCode() : 0;
        result = 31 * result + (currency2 != null ? currency2.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CurrencyPair{" +
                "currency1=" + currency1 +
                ", currency2=" + currency2 +
                ", name='" + name + '\'' +
                '}';
    }
}
