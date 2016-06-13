package me.exrates.controller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.FileLoadingException;
import me.exrates.controller.exception.NewsCreationException;
import me.exrates.controller.exception.NoFileForLoadingException;
import me.exrates.model.News;
import me.exrates.service.NewsService;
import me.exrates.service.UserFilesService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
* DRAFT ONLY //TODO
* */

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

    /*skip resources: img, css, js*/
//    @RequestMapping(value = "/news/**/newstopic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = "/news/**/newstopic.html")
    public String newsSingle(HttpServletRequest request) {
        try {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("news/newstopic");
            String path = request.getServletPath(); //   /news/2015/MAY/27/48/newstopic.html
            int newsId = Integer.valueOf(path.split("\\/{1}[^\\/]*$")[0].split("^.*[\\/]")[1]); // =>  /news/2015/MAY/27/48  => 48
            News news = newsService.getNews(newsId, localeResolver.resolveLocale(request));
            if (news != null) {
                String newsContentPath = new StringBuilder()
                        .append(newsLocationDir)    //    /Users/Public/news/
                        .append(news.getResource()) //                      2015/MAY/27/
                        .append(newsId)             //                                  48
                        .append("/")                //                                     /
                        .append(localeResolver.resolveLocale(request).toString())   //      ru
                        .append("/newstopic.html")  //                                          /newstopic.html
                        .toString();                //  /Users/Public/news/2015/MAY/27/48/ru/newstopic.html
                try {
                    String newsContent = new String(Files.readAllBytes(Paths.get(newsContentPath)), "UTF-8"); //content of the newstopic.html 
                    news.setContent(newsContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String newsContent = messageSource.getMessage("news.absent", null, localeResolver.resolveLocale(request));
                news = new News();
                news.setContent(newsContent);
            }
            return news.getContent();
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "/news/addNewsVariant", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String uploadNewsVariant(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "file", required = false) MultipartFile[] multipartFiles,
                                    @RequestParam(value = "id", required = false) Integer newsId,
                                    @RequestParam(value = "date", required = false) String date,
                                    @RequestParam(value = "resource", required = false) String resource,
                                    @RequestParam(value = "newsVariant", required = false) String newsVariant) {
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

}