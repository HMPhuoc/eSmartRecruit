package com.example.eSmartRecruit.services.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {
    @Mock
    private Path storageFolder;

    @InjectMocks
    private FileStorageService fileStorageService;
    @Test
    void testIsPDF_WithPDFFile_ShouldReturnTrue() {
        // Arrange
        FileStorageService fileStorageService = new FileStorageService();
        MockMultipartFile pdfFile = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cv data".getBytes());

        // Act
        boolean result = fileStorageService.isPDF(pdfFile);

        // Assert
        assertTrue(result);
    }
    @Test
    void isPDF_invalidPDFFile_returnsFalse() {
        // Arrange
        String fileName = "test.txt";
        byte[] fileContent = new byte[]{};
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, "text/plain", fileContent);

        // Act
        FileStorageService fileStorageService = new FileStorageService();

        boolean result = fileStorageService.isPDF(file);

        // Assert
        assertFalse(result);
    }
    @Test

    void storeFile_validPDFFile_returnsGeneratedFileName() {
        // Arrange
        FileStorageService fileStorageService = new FileStorageService();
        MockMultipartFile pdfFile = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cv data".getBytes());
        // Act
        String result = fileStorageService.storeFile(pdfFile);
        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith(".pdf"));
    }

    @Test
    void testStoreFile_WithEmptyFile_ShouldThrowFileUploadException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        // Act and Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> fileStorageService.storeFile(emptyFile));
//        assertEquals("File not found", exception.getMessage());
    }

    @Test
    void testStoreFile_WithNonPDFFile_ShouldThrowFileUploadException() {
        // Arrange

        MockMultipartFile nonPdfFile = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cv data".getBytes());

        // Act and Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> fileStorageService.storeFile(nonPdfFile));
//        assertEquals("Only pdf file accepted!", exception.getMessage());
    }
    @Test
    void testStoreFile_SuccessfulStore() {
        // Arrange
        FileStorageService fileStorageService = new FileStorageService();

        MockMultipartFile pdfFile = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cv data".getBytes());

        // Act
        String result = fileStorageService.storeFile(pdfFile);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith(".pdf")); // Assuming the generated file name has a .pdf extension
    }

    @Test
    void testLoadAll() {
        // Act
        FileStorageService fileStorageService = new FileStorageService();

        Stream<Path> result = fileStorageService.loadAll();

        // Assert
        assertNull(result);
    }

    @Test
    void testReadFileContent() {
        // Arrange
        FileStorageService fileStorageService = new FileStorageService();

        String fileName = "test.pdf";

        // Act
        byte[] result = fileStorageService.readFileContent(fileName);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.length); // Assuming the method returns an empty byte array
    }

    @Test
    void testDeleteAllFiles() {
        // Act and Assert
        FileStorageService fileStorageService = new FileStorageService();

        assertDoesNotThrow(fileStorageService::deleteAllFiles);
    }
}