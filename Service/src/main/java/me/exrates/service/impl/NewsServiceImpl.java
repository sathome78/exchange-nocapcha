package me.exrates.service.impl;

import me.exrates.dao.NewsDao;
import me.exrates.model.News;
import me.exrates.service.NewsService;
import me.exrates.service.exception.FileLoadingException;
import me.exrates.service.exception.NewsCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Valk on 28.05.2016.
 */
@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsDao newsDao;

    @Override
    public List<News> getNewsBriefList(Integer offset, Integer limit, Locale locale) {
        return newsDao.getNewsBriefList(offset, limit, locale);
    }

    @Override
    public News getNews(Integer newsId, Locale locale) {
        return newsDao.getNews(newsId, locale);
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
                try {
                    newsDao.addNewsVariant(v);
                } catch (DuplicateKeyException e) {
                    //provide an opportunity to update the file
                }
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
}
