package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
public class OpenApiTokenPublicDto {
    private Long id;
    private String alias;
    private Integer userId;
    private String publicKey;
    private Boolean allowTrade;
    private Boolean allowWithdraw;
    @JsonIgnore
    private Boolean allowAcceptById;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime generationDate;
}
