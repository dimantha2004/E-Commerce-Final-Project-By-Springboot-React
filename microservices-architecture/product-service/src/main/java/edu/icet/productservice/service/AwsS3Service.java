package edu.icet.productservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface AwsS3Service {
    String saveImageToS3(MultipartFile photo);
}
