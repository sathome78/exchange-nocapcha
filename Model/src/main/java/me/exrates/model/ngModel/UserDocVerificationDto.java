package me.exrates.model.ngModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.ngModel.enums.VerificationDocumentType;

@Data
@Builder
@AllArgsConstructor(suppressConstructorProperties = true)
public class UserDocVerificationDto {

    private Integer userId;
    private VerificationDocumentType documentType;
    private String encoded;
}
