package me.exrates.model.dto.merchants.qtum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.merchants.neo.JsonRpcResponseError;

@Getter @Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class QtumJsonRpcResponse<T> {
    private T result;
    private JsonRpcResponseError error;
}
