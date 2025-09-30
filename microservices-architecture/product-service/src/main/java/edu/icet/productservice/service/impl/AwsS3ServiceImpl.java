package edu.icet.productservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import edu.icet.productservice.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3ServiceImpl implements AwsS3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String saveImageToS3(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo file cannot be null or empty.");
        }

        try {
            // Generate unique filename to avoid conflicts
            String originalFilename = photo.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String s3FileName = UUID.randomUUID().toString() + fileExtension;

            InputStream inputStream = photo.getInputStream();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(photo.getContentType());
            metadata.setContentLength(photo.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, metadata);
            amazonS3Client.putObject(putObjectRequest);

            String imageUrl = amazonS3Client.getUrl(bucketName, s3FileName).toString();
            log.info("Successfully uploaded image to S3: {}", imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("Unable to upload image to S3 bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to upload image to S3 bucket: " + e.getMessage());
        }
    }
}
