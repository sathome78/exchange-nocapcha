package me.exrates.model;

public class IEOResult {
    private int id;
    private int claimId;
    private IEOResultStatus status;

    public enum IEOResultStatus {
        success, fail, none
    }

    public IEOResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClaimId() {
        return claimId;
    }

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }

    public IEOResultStatus getStatus() {
        return status;
    }

    public void setStatus(IEOResultStatus status) {
        this.status = status;
    }
}
