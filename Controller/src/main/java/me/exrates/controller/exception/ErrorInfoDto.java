package me.exrates.controller.exception;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Created by ValkSam
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorInfoDto {
    public String error;
    public String detail;

    public ErrorInfoDto(String error) {
        this.error = error;
    }

    public ErrorInfoDto(Exception exception) {
        this.error = exception.getClass().getSimpleName();
        this.detail = exception.getLocalizedMessage();
    }
}
