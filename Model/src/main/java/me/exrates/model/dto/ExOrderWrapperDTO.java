package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import me.exrates.model.ExOrder;

@Data
@Builder
public class ExOrderWrapperDTO {

    private ExOrder exOrder;
    private String message;
    private int userId;
}
