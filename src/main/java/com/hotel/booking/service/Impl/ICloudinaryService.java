package com.hotel.booking.service.Impl;

import com.cloudinary.Cloudinary;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.util.ByteMultipartFile;
import com.hotel.booking.util.FileUtil;
import com.hotel.booking.util.ImageUtil;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ICloudinaryService implements CloudinaryService {
    private final Cloudinary cloudinary;
    @Override
    public Map<?, ?> uploadFile(MultipartFile file, String folderName) {
        try{
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            Map<?,?> uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            String resourceType = (String) uploadedFile.get("resource_type");
            return uploadedFile;

        }catch (IOException | java.io.IOException e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public String uploadImage(MultipartFile file,String folderName) throws java.io.IOException {
        HashMap<Object, Object> options = new HashMap<>();
        options.put("folder", folderName);
        Map<?,?> uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
        return uploadedFile.get("url").toString();
    }

    @Override
    public CompletableFuture<String> uploadImage(MultipartFile imageFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HashMap<Object, Object> options = new HashMap<>();
                options.put("folder", "rooms");
                Map<?, ?> uploadedFile = cloudinary.uploader().upload(imageFile.getBytes(), options);
                return uploadedFile.get("url").toString();
            } catch (Exception e) {
                throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<List<String>> uploadImageList(List<MultipartFile> imageFileList) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile imageFile : imageFileList) {
                try {
                    HashMap<Object, Object> options = new HashMap<>();
                    options.put("folder", "rooms");
                    Map<?, ?> uploadedFile = cloudinary.uploader().upload(imageFile.getBytes(), options);
                    String imageUrl = uploadedFile.get("url").toString();
                    System.out.println("imageUrl: " + imageUrl);
                    imageUrls.add(imageUrl);
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
                }
            }
            return imageUrls;
        });
    }

    @Override
    public String getURLPictureAndUploadToCloudinary(String base64Content) {
        try {
            byte[] fileBytes = FileUtil.base64ToBytes(base64Content);
            MultipartFile multipartFile = new ByteMultipartFile(fileBytes);
            Tika tika = new Tika();
            String mimetype = tika.detect(fileBytes);
            if (mimetype.contains("image")) {
                Map<?, ?> map = uploadFile(multipartFile, "Product");
                return (String) map.get("secure_url");
            } else
                return ImageUtil.urlImage;
        } catch (Exception exception) {
            return null;
        }

    }
}
