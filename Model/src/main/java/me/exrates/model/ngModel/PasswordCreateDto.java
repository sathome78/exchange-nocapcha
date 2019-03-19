package me.exrates.model.ngModel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
public class PasswordCreateDto {

    @NotNull
    private String tempToken;

    @NotNull
    private String password;
}
