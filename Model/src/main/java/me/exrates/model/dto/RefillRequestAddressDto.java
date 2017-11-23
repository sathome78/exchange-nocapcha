package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CreditsOperation;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * @author Ajet
 */
@Getter @Setter
@ToString
public class RefillRequestAddressDto {
  private Integer id;
  private Integer currencyId;
  private Integer merchantId;
  private String address;
  private Integer userId;
  private String privKey;
  private String pubKey;
  private String brainPrivKey;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime dateGeneration;
  private Integer confirmedTxOffset;
  private boolean needTransfer;

}