package com.hotel.booking.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.List;

public interface CloudinaryService {
    Map<?, ?> uploadFile(MultipartFile file, String folderName);
    String getURLPictureAndUploadToCloudinary(String base64Content);
    String uploadImage(MultipartFile file,String folderName) throws IOException;
    CompletableFuture<String> uploadImage(MultipartFile imageFile);
    CompletableFuture<List<String>> uploadImageList(List<MultipartFile> imageFileList);
}
