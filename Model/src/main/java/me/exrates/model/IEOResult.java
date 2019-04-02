package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class IEOResult {
    private int claimId;
    private IEOResultStatus status;

    public enum IEOResultStatus {
        success, fail, none
    }
}
