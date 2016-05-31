package me.exrates.dao;

import me.exrates.model.News;

import java.util.List;
import java.util.Locale;

/**
 * Created by Valk on 27.05.2016.
 */

public interface NewsDao {
    List<News> getNewsBriefList(Integer offset, Integer limit, Locale locale);

    News getNews(final Integer newsId, Locale locale);

    int addNews(News news);

    int addNewsVariant(News news);
}
