package me.exrates.model;

public class MerchantImage {

    private int Id;
    private int merchantId;
    private int currencyId;
    private String image_name;
    private String image_path;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getImage_name() {
        return image_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MerchantImage that = (MerchantImage) o;

        if (Id != that.Id) return false;
        if (merchantId != that.merchantId) return false;
        if (currencyId != that.currencyId) return false;
        if (image_name != null ? !image_name.equals(that.image_name) : that.image_name != null) return false;
        return image_path != null ? image_path.equals(that.image_path) : that.image_path == null;

    }

    @Override
    public int hashCode() {
        int result = Id;
        result = 31 * result + merchantId;
        result = 31 * result + currencyId;
        result = 31 * result + (image_name != null ? image_name.hashCode() : 0);
        result = 31 * result + (image_path != null ? image_path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MerchantImage{" +
                "Id=" + Id +
                ", merchantId=" + merchantId +
                ", currencyId=" + currencyId +
                ", image_name='" + image_name + '\'' +
                ", image_path='" + image_path + '\'' +
                '}';
    }
}