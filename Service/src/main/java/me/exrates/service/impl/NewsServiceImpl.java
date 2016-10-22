package me.exrates.service.impl;

import me.exrates.dao.NewsDao;
import me.exrates.model.News;
import me.exrates.model.dto.onlineTableDto.NewsDto;
import me.exrates.model.vo.CacheData;
import me.exrates.service.NewsService;
import me.exrates.service.exception.FileLoadingException;
import me.exrates.service.exception.NewsCreationException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Valk on 28.05.2016.
 */
@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOG = LogManager.getLogger(NewsServiceImpl.class);

    @Autowired
    private NewsDao newsDao;

    @Override
    public List<NewsDto> getNewsBriefList(CacheData cacheData, Integer offset, Integer limit, Locale locale) {
        List<NewsDto> result = newsDao.getNewsBriefList(offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<NewsDto>() {{
                add(new NewsDto(false));
            }};
        }
        return result;
    }

    @Override
    public News getNews(Integer newsId, Locale locale) {
        return newsDao.getNews(newsId, locale);
    }

    @Override
    public News getNewsWithContent(Integer newsId, Locale locale, String locationDir) {
        News news = getNews(newsId, locale);
        String contentPathString = locationDir + news.getResource() +
                new StringJoiner("/").add(String.valueOf(news.getId())).add(news.getNewsVariant()).add("newstopic.html").toString();
        Path contentPath = Paths.get(contentPathString);
        LOG.debug(contentPathString);
        if (contentPath.toFile().exists()) {
            try {
                StringBuilder fileContent = new StringBuilder();
                Files.lines(contentPath).forEach(line -> fileContent.append(line).append('\n'));
                news.setContent(fileContent.toString().substring(0, fileContent.length() - 1));
                LOG.debug(news.getContent());
            } catch (IOException e) {
                throw new FileLoadingException(e.getLocalizedMessage());
            }

        } else {
            throw new FileLoadingException("Content does not exist!");
        }
        return news;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uploadNews(Collection<News> variants, MultipartFile multipartFile, String newsLocationDir) {
        News news = variants.iterator().next();
        if (news.getId() == null) {
            int id = newsDao.addNews(news);
            if (id == 0) {
                throw new NewsCreationException("");
            }
            for (News n : variants) {
                n.setId(id);
            }
        }
        String newsRootContentPath = new StringBuilder()
                .append(newsLocationDir)
                .append(news.getResource())
                .append(news.getResource().matches(".+\\/$") ? "" : "/")
                .append(news.getId())
                .append("/")
                .toString();
        try {
            for (News v : variants) {
                newsDao.addNewsVariant(v);
            }
            Path op = Paths.get(newsRootContentPath);
            if (!op.toFile().exists()) {
                Files.createDirectories(op);
            }
            ZipInputStream zis = new ZipInputStream(multipartFile.getInputStream(), Charset.forName("CP866"));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                String fn = newsRootContentPath + ze.getName();
                op = Paths.get(fn);
                if (fn.matches(".+\\/$")) {
                    if (!op.toFile().exists()) {
                        Files.createDirectories(op);
                    }
                } else {
                    if (op.toFile().exists()) {
                        op.toFile().delete();
                    }
                    Files.copy(zis, op);
                }
                zis.closeEntry();
            }
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileLoadingException(e.getLocalizedMessage());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public News createNewsVariant(News news, String locationDir, Locale locale) {
        Integer id = news.getId();

        if (id == null) {
            id = newsDao.addNews(news);
            news.setId(id);
        }

        try {
            if (news.getNewsVariant() == null || news.getNewsVariant().isEmpty()) {
                news.setNewsVariant(locale.getLanguage());
                newsDao.addNewsVariant(news);
            }
            String newsRootContentPath = new StringJoiner("")
                    .add(locationDir)
                    .add(news.getResource())
                    .add(String.valueOf(id)).add("/")
                    .add(news.getNewsVariant()).add("/")
                    .toString();
            LOG.debug(newsRootContentPath);
            Path newsDirPath = Paths.get(newsRootContentPath);
            if (!newsDirPath.toFile().exists()) {
                Files.createDirectories(newsDirPath);
            }
            Path contentPath = Paths.get(newsRootContentPath + "newstopic.html");
            Path titlePath = Paths.get(newsRootContentPath + "title.md");
            Path briefPath = Paths.get(newsRootContentPath + "brief.md");
            Files.write(contentPath, Collections.singleton(news.getContent()));
            Files.write(titlePath, Collections.singleton(news.getTitle()));
            Files.write(briefPath, Collections.singleton(news.getBrief()));


        } catch (IOException e) {
            e.printStackTrace();
            throw new FileLoadingException(e.getLocalizedMessage());
        }
        return news;
    }

    @Override
    public int deleteNewsVariant(News news) {
        return newsDao.deleteNewsVariant(news);
    }

    @Override
    public int deleteNews(News news) {
        return newsDao.deleteNews(news);
    }

    @Override
    public List<News> findAllNewsVariants() {
        return newsDao.findAllNewsVariants();
    }



}
