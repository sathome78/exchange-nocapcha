package me.exrates.controller;

import me.exrates.controller.exception.*;
import me.exrates.model.vo.CacheData;
import me.exrates.service.util.Cache;
import me.exrates.model.News;
import me.exrates.model.dto.NewsDto;
import me.exrates.model.dto.TableParams;
import me.exrates.service.NewsService;
import me.exrates.service.UserFilesService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@PropertySource(value = {"classpath:/news.properties"})
public class NewsControllerRest {
    private static final Logger LOG = LogManager.getLogger(NewsControllerRest.class);
    private final int DEAFAULT_PAGE_SIZE = 20;
    private final String TITLE_DESCRIPTION_FILE_NAME = "title.md";
    private final String BRIEF_DESCRIPTION_FILE_NAME = "brief.md";

    @Autowired
    MessageSource messageSource;
    private
    @Value("${news.locationDir}")
    String newsLocationDir;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserFilesService userFilesService;

    @RequestMapping(value = "/dashboard/news/{tableId}", method = RequestMethod.GET)
    public List<NewsDto> getNewsList(
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @RequestParam(required = false) Integer page,
            HttpServletRequest request) {
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "Не установлены параметры для " + tableId);
        Integer offset = page == null || tableParams.getPageSize() == -1 ? 0 : (page - 1) * tableParams.getPageSize();
        String cacheKey = "newsList";
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        return newsService.getNewsBriefList(cacheData, offset, tableParams.getPageSize(), localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/news/addNewsVariant", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String uploadNewsVariant(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "file", required = false) MultipartFile[] multipartFiles,
                                    @RequestParam(value = "id", required = false) Integer newsId,
                                    @RequestParam(value = "date", required = false) String date,
                                    @RequestParam(value = "resource", required = false) String resource) {
        MultipartFile multipartFile = multipartFiles[0];
        if (multipartFile.isEmpty() ||
                Stream.of(multipartFiles)
                        .filter(file -> file.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1).toUpperCase().equals("ZIP"))
                        .collect(Collectors.toList()).isEmpty()) {
            throw new NoFileForLoadingException(messageSource.getMessage("news.nofilesforloading", null, localeResolver.resolveLocale(request)));
        } else {
            News news = new News();
            news.setId(newsId);
            if (newsId == null) {
                /*new News. Requires date.*/
                try {
                    news.setDate(LocalDate.parse(date));
                } catch (Exception e) {
                    throw new NewsCreationException(messageSource.getMessage("news.dateerror", null, localeResolver.resolveLocale(request)));
                }
                resource = String.valueOf(news.getDate().getYear()) + "/" + String.valueOf(news.getDate().getMonth()) + "/" + String.valueOf(news.getDate().getDayOfMonth()) + "/";
            }
            news.setResource(resource);
            /*populate title, brief and collect locale from archive file*/
            Map<String, News> variants = new HashMap<>();
            try {
                ZipInputStream zis = new ZipInputStream(multipartFile.getInputStream(), Charset.forName("CP866"));
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    if (ze.getName().contains(TITLE_DESCRIPTION_FILE_NAME)) {
                        byte[] buff = new byte[(int) ze.getSize()];
                        zis.read(buff);
                        news.setTitle(new String(buff, "UTF-8"));
                        /**/
                        String locale = ze.getName().split("\\/")[ze.getName().split("\\/").length - 2];
                        News n = variants.get(locale);
                        if (n == null) {
                            try {
                                news.setNewsVariant(locale);
                                variants.put(locale, (News) news.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            n.setTitle(news.getTitle());
                        }
                    }
                    if (ze.getName().contains(BRIEF_DESCRIPTION_FILE_NAME)) {
                        byte[] buff = new byte[(int) ze.getSize()];
                        zis.read(buff);
                        news.setBrief(new String(buff, "UTF-8"));
                        /**/
                        String locale = ze.getName().split("\\/")[ze.getName().split("\\/").length - 2];
                        News n = variants.get(locale);
                        if (n == null) {
                            try {
                                news.setNewsVariant(locale);
                                variants.put(locale, (News) news.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            n.setTitle(news.getBrief());
                        }
                    }
                }
                /**/
                if (variants.isEmpty()) {
                    throw new FileLoadingException("");
                }
                newsService.uploadNews(variants.values(), multipartFile, newsLocationDir);
                /**/
            } catch (NewsCreationException e) {
                throw new NewsCreationException(messageSource.getMessage("news.errorcreate", null, localeResolver.resolveLocale(request)));
            } catch (FileLoadingException | IOException e) {
                throw new FileLoadingException(String.format("%s </br> %s", messageSource.getMessage("news.errorload", null, localeResolver.resolveLocale(request)), e.getLocalizedMessage()));
            }
            return "{\"result\":\"" + messageSource.getMessage("news.successload", null, localeResolver.resolveLocale(request)) + "\"}";
        }
    }

    @RequestMapping(value = "/news/deleteNews", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String uploadNewsVariant(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "id", required = false) Integer newsId,
                                    @RequestParam(value = "removingType", required = false) String removingType,
                                    @RequestParam(value = "variant", required = false) String variant,
                                    @RequestParam(value = "resource", required = false) String resource) {
        try {
            News news = new News();
            news.setId(newsId);
            int result = 0;
            if ("news".equals(removingType)) {
                result = newsService.deleteNews(news);
            } else if ("variant".equals(removingType)) {
                news.setNewsVariant(variant);
                result = newsService.deleteNewsVariant(news);
            }
            if (result <= 0) {
                throw new NewsRemovingException("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NewsRemovingException(messageSource.getMessage("news.errordelete", null, localeResolver.resolveLocale(request)));
        }
        return "{\"result\":\"" + messageSource.getMessage("news.successdelete", null, localeResolver.resolveLocale(request)) + "\"}";
    }


    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NoFileForLoadingException.class)
    @ResponseBody
    public ErrorInfo NoFileForLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(FileLoadingException.class)
    @ResponseBody
    public ErrorInfo FileLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NewsCreationException.class)
    @ResponseBody
    public ErrorInfo NewsCreationExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NewsRemovingException.class)
    @ResponseBody
    public ErrorInfo NewsRemovingException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}