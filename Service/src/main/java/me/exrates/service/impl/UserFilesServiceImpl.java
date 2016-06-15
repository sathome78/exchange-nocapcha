package me.exrates.service.impl;

import me.exrates.service.UserFilesService;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/uploadfiles.properties")
public class UserFilesServiceImpl implements UserFilesService {

    private @Value("${upload.userFilesDir}") String userFilesDir;
    private @Value("${upload.userFilesLogicalDir}") String userFilesLogicalDir;

    private final UserService userService;
    private final Set<String> contentTypes;

    private static final Logger LOG = LogManager.getLogger(UserFilesServiceImpl.class);

    @Autowired
    public UserFilesServiceImpl(final UserService userService) {
        this.userService = userService;
        contentTypes = new HashSet<>();
        contentTypes.addAll(asList("image/jpg", "image/jpeg", "image/png"));
    }

    /**
     * Removes empty files and files with invalid extension from an input array
     * @param files - Uploaded files
     * @return - ArrayList with valid uploaded files
     */
    @Override
    public List<MultipartFile> reduceInvalidFiles(final MultipartFile[] files) {
        return Stream.of(files)
                .filter(file -> !file.isEmpty() && contentTypes.contains(extractContentType(file)))
                .collect(Collectors.toList());
    }

    /**
     * Moves uploaded files to user dir on the server ({@link UserFilesServiceImpl#userFilesDir} + userId) and persist File names in DB (table USER_DOC)
     * If only one file failed to move - deletes all uploaded files and throws IOException
     * @param userId - UserId who uploads the files
     * @param files - uploaded files
     * @throws IOException
     */
    @Override
    public void createUserFiles(final int userId, final List<MultipartFile> files) throws IOException {
        final Path path = Paths.get(userFilesDir + userId);
        final List<Path> logicalPaths = new ArrayList<>();
        final List<Path> realPaths = new ArrayList<>();
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        try {
            for (final MultipartFile file : files) {
                final String name = UUID.randomUUID().toString() + "." + extractFileExtension(file);
                final Path target = Paths.get(path.toString(), name);
                Files.write(target, file.getBytes());
                realPaths.add(target);
                logicalPaths.add(Paths.get(userFilesLogicalDir, String.valueOf(userId), name));
            }
        } catch (final IOException e) {
            if (!realPaths.isEmpty()) {
                final List<IOException> exceptions = new ArrayList<>();
                try {
                    for (final Path toRemove : realPaths) {
                        Files.delete(toRemove);
                    }
                } catch (final IOException ex) {
                    ex.initCause(e);
                    exceptions.add(ex);
                }
                if (!exceptions.isEmpty()) {
                    LOG.error("Exceptions during deleting uploaded files " + exceptions);
                }
            }
            throw e;
        }
        userService.createUserFile(userId, logicalPaths);
    }

    @Override
    public void deleteUserFile(final String filename, final int userId) throws IOException {
        final Path path = Paths.get(userFilesDir, String.valueOf(userId), filename);
        Files.delete(path);
    }

    private String extractContentType(final MultipartFile file) {
        return file.getContentType().toLowerCase();
    }

    /**
     * @param file - Uploaded file
     * @return - Uploaded files extension
     */
    private String extractFileExtension (final MultipartFile file) {
        return extractContentType(file).substring(6); //Index of dash in Content-Type
    }
}
