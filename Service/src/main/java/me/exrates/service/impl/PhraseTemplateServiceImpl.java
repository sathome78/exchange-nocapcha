package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.PhraseTemplateDao;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.service.PhraseTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Log4j2
public class PhraseTemplateServiceImpl implements PhraseTemplateService {

  @Autowired
  private PhraseTemplateDao phraseTemplateDao;

  @Autowired
  private MessageSource messageSource;

  @Override
  @Transactional(readOnly = true)
  public List<String> getAllByTopic(UserCommentTopicEnum topic) {
    return phraseTemplateDao.findByTopic(topic.getCode());
  }


}
