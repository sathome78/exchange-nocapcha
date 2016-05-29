package me.exrates.model;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class ReferralTransaction {

    private Transaction transaction;
    private ExOrder exOrder;
    private ReferralLevel referralLevel;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }

    public ExOrder getExOrder() {
        return exOrder;
    }

    public void setExOrder(final ExOrder exOrder) {
        this.exOrder = exOrder;
    }

    public ReferralLevel getReferralLevel() {
        return referralLevel;
    }

    public void setReferralLevel(final ReferralLevel referralLevel) {
        this.referralLevel = referralLevel;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ReferralTransaction that = (ReferralTransaction) o;

        if (transaction != null ? !transaction.equals(that.transaction) : that.transaction != null) return false;
        if (exOrder != null ? !exOrder.equals(that.exOrder) : that.exOrder != null) return false;
        return referralLevel != null ? referralLevel.equals(that.referralLevel) : that.referralLevel == null;

    }

    @Override
    public int hashCode() {
        int result = transaction != null ? transaction.hashCode() : 0;
        result = 31 * result + (exOrder != null ? exOrder.hashCode() : 0);
        result = 31 * result + (referralLevel != null ? referralLevel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReferralTransaction{" +
                "transaction=" + transaction +
                ", exOrder=" + exOrder +
                ", referralLevel=" + referralLevel +
                '}';
    }
}
