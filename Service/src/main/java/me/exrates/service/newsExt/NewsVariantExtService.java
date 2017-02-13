package me.exrates.service.newsExt;


import me.exrates.model.dto.newsDto.NewsTopicDto;
import me.exrates.model.enums.NewsTypeEnum;

import java.util.Locale;

public interface NewsVariantExtService {
  NewsTopicDto getMaterialPageContent(NewsTypeEnum newsTypeEnum, String resource, Locale locale);

  void deleteNewsVariant(final Integer id);
}
