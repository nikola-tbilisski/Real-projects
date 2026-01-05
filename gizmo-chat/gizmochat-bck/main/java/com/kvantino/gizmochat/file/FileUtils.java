package com.kvantino.gizmochat.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FileUtils {

    private FileUtils() {
    }

//    public static byte[] readFileFromLocation(String filePath) {
//        if (StringUtils.isBlank(filePath))
//            return new byte[0];
//
//        try {
//            Path file = new File(filePath).toPath();
//            return Files.readAllBytes(file);
//        } catch (IOException e) {
//            log.warn("No file found at {}", filePath);
//        }
//        return new byte[0];
//    }

    public static byte[] readFileFromLocation(String filePath) {
        if (StringUtils.isBlank(filePath))
            return new byte[0];

        int bufferSize = 65536;
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        Path path = Paths.get(filePath);

        try (FileInputStream fis = new FileInputStream(path.toFile());
             BufferedInputStream bis = new BufferedInputStream(fis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize)) {

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();

        } catch (IOException e) {
            log.warn("No file found at {}", filePath);
        }

        return new byte[0];
    }
}
