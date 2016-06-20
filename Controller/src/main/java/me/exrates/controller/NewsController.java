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

@Controller
@PropertySource(value = {"classpath:/news.properties", "classpath:/captcha.properties"})
public class NewsController {
    private static final Logger LOG = LogManager.getLogger(NewsController.class);
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

    @Value("${captcha.type}")
    String CAPTCHA_TYPE;

    /*skip resources: img, css, js*/
    @RequestMapping("/news/**/newstopic")
    public ModelAndView newsSingle(HttpServletRequest request) {
        try {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("globalPages/newstopic");
            String path = request.getServletPath(); //   /news/2015/MAY/27/48/ru/newstopic.html
            int newsId = Integer.valueOf(path.split("\\/\\p{Alpha}+\\/{1}[^\\/]*$")[0].split("^.*[\\/]")[1]); // =>  /news/2015/MAY/27/48  => 48
//            String locale = path.split("\\/{1}[^\\/]*$")[0].split("^.*[\\/]")[1];
            News news = newsService.getNews(newsId, localeResolver.resolveLocale(request));
            if (news != null) {
                String newsContentPath = new StringBuilder()
                        .append(newsLocationDir)    //    /Users/Public/news/
                        .append(news.getResource()) //                      2015/MAY/27/
                        .append(newsId)             //                                  48
                        .append("/")                //                                     /
                        .append(localeResolver.resolveLocale(request).toString())   //      ru
                        //ignore locale from path and take it from fact locale .append(locale)   //                                                ru
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
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            modelAndView.addObject("news", news);
            return modelAndView;
        } catch (Exception e) {
            return null;
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