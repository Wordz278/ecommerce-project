package com.adrian.ecommerceproject.service.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.adrian.ecommerceproject.service.FileUploadService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    private String uploadFolderPath = "C:\\Users\\A238545\\Desktop\\pathimages\\uploaded_";

    @Override
    public void uploadToLocal(MultipartFile file){
        try {
            byte[] data = file.getBytes();
            Path path = Paths.get(uploadFolderPath + file.getOriginalFilename());
            Files.write(path, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
