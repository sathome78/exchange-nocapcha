package me.exrates.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface UserFilesService {

    List<MultipartFile> reduceInvalidFiles(MultipartFile[] files);

    void createUserFiles(int userId, List<MultipartFile> files) throws IOException;

    void deleteUserFile(String filename, int userId) throws IOException;
}
