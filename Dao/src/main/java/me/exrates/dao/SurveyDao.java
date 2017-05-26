package me.exrates.dao;

import me.exrates.model.dto.SurveyDto;

/**
 * Created by ValkSam on 26.05.2017.
 */
public interface SurveyDao {
  SurveyDto findFirstActiveByLang(String lang);
}
