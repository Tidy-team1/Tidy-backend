package com.tidy.tidy.infrastructure.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

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
}
