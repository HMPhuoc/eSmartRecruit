package com.example.eSmartRecruit.services.impl;

import com.example.eSmartRecruit.exception.FileUploadException;
import com.example.eSmartRecruit.services.IStorageService;
import jakarta.activation.FileTypeMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageService implements IStorageService {
    private final Path storageFolder = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(storageFolder);
        }catch (IOException e){
            throw new RuntimeException("Path error",e);
        }
    }

    public boolean isPDF(MultipartFile file){
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList(new String[]{"pdf"})
                .contains(fileExtension.trim().toLowerCase());
    }
    @Override
    public String storeFile(MultipartFile file) {
        try{
            if(file.isEmpty()){
                throw new FileUploadException("File not found");
            }
            if(!isPDF(file)){
                //return "Only pdf file accepted!";
                throw new FileUploadException("Only pdf file accepted!");
            }
            float fileSizeInMegabytes = file.getSize()/1000000;
            if (fileSizeInMegabytes>5){
                throw new FileSizeLimitExceededException("Only accept file less than 5MB", file.getSize(), 5);
            }
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String generatedFileName = UUID.randomUUID().toString().replace("-","");
            generatedFileName = generatedFileName+"."+fileExtension;
            Path destinationFilePath = this.storageFolder.resolve(Paths.get(generatedFileName))
                                        .normalize().toAbsolutePath();

            Path absPath = this.storageFolder.toAbsolutePath();
            if(!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath())){
                throw new FileUploadException("Cant save outside original path!");
            }
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return generatedFileName;
        }
        catch (IOException e){
            throw new RuntimeException("Failed to save file!", e);
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public byte[] readFileContent(String fileName) {
        return new byte[0];
    }

    @Override
    public void deleteAllFiles() {

    }
}
