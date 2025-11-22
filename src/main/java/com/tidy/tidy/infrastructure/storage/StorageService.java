package com.tidy.tidy.infrastructure.storage;

public interface StorageService {
    String upload(String key, byte[] bytes);
    void delete(String key);
}
