package com.hotel.booking.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtils {
    public  static void saveFile(String dirString , String filename , MultipartFile multipartFile) throws IOException, IOException {
        Path uploadPath = Paths.get(dirString);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try(InputStream imInputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            Files.copy(imInputStream,filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            throw new IOException("could not save image file");
        }

    }

}