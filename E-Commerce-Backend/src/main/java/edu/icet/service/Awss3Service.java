package edu.icet.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class Awss3Service {


    private final String bucketName = "e-commerce-onlineshopping";

    @Value("${aws.s3.access}")
    private String accessKey;

    @Value("${aws.s3.secret}")
    private String secretKey;

    public String saveImageToS3(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo file cannot be null or empty.");
        }

        try {
            String s3FileName = photo.getOriginalFilename();

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.EU_NORTH_1)
                    .build();

            InputStream inputStream = photo.getInputStream();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(photo.getContentType());
            metadata.setContentLength(photo.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

            return s3Client.getUrl(bucketName, s3FileName).toString();

        } catch (IOException e) {
            log.error("Unable to upload image to S3 bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to upload image to S3 bucket: " + e.getMessage());
        }
    }
}