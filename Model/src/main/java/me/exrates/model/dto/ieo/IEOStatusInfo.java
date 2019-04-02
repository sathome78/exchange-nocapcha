package me.exrates.model.dto.ieo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IEOStatusInfo {
    private boolean kycCheck;
    private boolean needSomePolicy;
    private boolean someField;
}
