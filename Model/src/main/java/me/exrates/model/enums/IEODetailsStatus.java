package me.exrates.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IEODetailsStatus {

    PENDING, RUNNING, SUCCEEDED, FAILED
}
