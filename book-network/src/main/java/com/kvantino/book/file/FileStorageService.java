package com.kvantino.book.file;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@Nonnull MultipartFile sourceFile,
                           @Nonnull Long userId) {

        final String fileUploadSubPath = "user" + separator + userId;

        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile sourceFile,
                              @Nonnull String fileUploadSubPath) {

        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder");
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());

        //Example of file path and the name of targetFilePath: ./upload/users/1/7334802365.jpg
        String targetFilePath = finalUploadPath + separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved successfully to: {}", targetFilePath);
            //return targetFilePath;
        } catch (IOException e) {
            log.error("File wasn't saved", e);
        }

        return targetFilePath;
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty())
            return "";

        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1)
            return "";

        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }
}
