package com.tidy.tidy.infrastructure.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

@Service
@Profile({"local", "dev"})   // 둘 다 S3 사용
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(String key, byte[] bytes) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);

        amazonS3.putObject(bucket, key, new ByteArrayInputStream(bytes), metadata);

        return key;  // DB에는 key만 저장 (URL 아님)
    }

    @Override
    public void delete(String key) {
        amazonS3.deleteObject(bucket, key);
    }

    /**
     * 주어진 key에 대한 presigned URL 생성 (GET용)
     */
    public String generatePresignedUrl(String key, int expireMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expireMinutes * 60L * 1000L);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(request);
        return url.toString();
    }
}
