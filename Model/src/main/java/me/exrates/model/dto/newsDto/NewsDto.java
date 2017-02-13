package me.exrates.model.dto.newsDto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.NewsTypeEnum;

import java.util.List;

/**
 * Created by ValkSam
 */
@Getter @Setter
public abstract class NewsDto {
    protected Integer id;
    protected String resource;
    protected NewsTypeEnum newsType;
    protected Integer newsVariantId;
    protected String date;
    protected String title;
    protected String titleImageSource;
    protected String brief;
    protected String language;
    protected String referenceToNewstopic;
    protected List<String> tagList;
    protected String ytVideoId;
    protected Integer showsCount;
    protected String calendarDate;
    protected Boolean noTitleImg;
}
