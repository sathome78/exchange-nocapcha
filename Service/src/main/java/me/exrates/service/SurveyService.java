package me.exrates.service;

import me.exrates.model.dto.SurveyDto;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ValkSam on 26.05.2017.
 */
public interface SurveyService {
  void savePollAsDoneByUser(String email);

  boolean checkPollIsDoneByUser(String email);

  SurveyDto getFirstActiveSurveyByLang(String lang);
}
