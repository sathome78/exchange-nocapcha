package me.exrates.dao;

import java.util.List;

/**
 * Created by ValkSam
 */
public interface PhraseTemplateDao {
  List<String> findByTopic(Integer topicId);
}
