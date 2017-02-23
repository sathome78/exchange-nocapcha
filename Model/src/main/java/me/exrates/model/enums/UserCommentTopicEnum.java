package me.exrates.model.enums;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.exceptions.UnsupportedUserCommentTopicIdException;
import me.exrates.model.exceptions.UnsupportedUserCommentTopicNameException;

import java.util.Arrays;

/**
 * Created by ValkSam
 */
@Log4j2
public enum UserCommentTopicEnum {
  GENERAL(1),
  INVOICE_DECLINE(2);

  private Integer code;

  UserCommentTopicEnum(Integer code) {
    this.code = code;
  }

  public static UserCommentTopicEnum convert(int id) {
    return Arrays.stream(UserCommentTopicEnum.class.getEnumConstants())
        .filter(e -> e.code == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedUserCommentTopicIdException(String.valueOf(id)));
  }

  public static UserCommentTopicEnum convert(String name) {
    return Arrays.stream(UserCommentTopicEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedUserCommentTopicNameException(name));
  }

  public Integer getCode() {
    return code;
  }
}
