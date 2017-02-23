package me.exrates.service;

import me.exrates.model.enums.UserCommentTopicEnum;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ValkSam
 */
public interface PhraseTemplateService {
  List<String> getAllByTopic(UserCommentTopicEnum topic);
}
