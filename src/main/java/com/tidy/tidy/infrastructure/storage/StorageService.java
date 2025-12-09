package com.tidy.tidy.infrastructure.storage;

public interface StorageService {
    String upload(String key, byte[] bytes);
    void delete(String key);

    // 🔥 새로 추가 (파일 크기 조회)
    long getFileSize(String key);
}
