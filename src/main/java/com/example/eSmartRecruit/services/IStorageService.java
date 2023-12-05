package com.example.eSmartRecruit.services;

import com.example.eSmartRecruit.exception.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;


public interface IStorageService {
    public String storeFile(MultipartFile file)throws FileUploadException;
    public Stream<Path> loadAll();
    public byte[] readFileContent(String fileName);
    public void deleteAllFiles();
    public boolean isPDF(MultipartFile file);

}
