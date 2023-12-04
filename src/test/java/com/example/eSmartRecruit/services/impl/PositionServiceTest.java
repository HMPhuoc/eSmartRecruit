package com.example.eSmartRecruit.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.eSmartRecruit.exception.PositionException;
import com.example.eSmartRecruit.models.Position;
import com.example.eSmartRecruit.repositories.PositionRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionServiceTest {

    private PositionService positionService;
    private PositionRepos positionRepository;

    @BeforeEach
    public void setup() {
        positionRepository = mock(PositionRepos.class);
        positionService = new PositionService(positionRepository);
    }

    @Test
    void testGetSelectedPosition_Successful() throws PositionException {
        // Arrange
        int positionId = 1;
        Position expectedPosition = new Position();
        expectedPosition.setId(positionId);
        expectedPosition.setTitle("Software Engineer");
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(expectedPosition));

        // Act
        Position result = positionService.getSelectedPosition(positionId);

        // Assert
        assertEquals(expectedPosition, result);
    }

    @Test
    void testGetSelectedPosition_PositionNotFound() {
        // Arrange
        int positionId = 1;
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(PositionException.class, () -> positionService.getSelectedPosition(positionId));
    }

    @Test
    void testIsPresent_PositionNotExpired() throws PositionException {
        // Arrange
        int positionId = 1;
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Position position = new Position();
        position.setId(positionId);
        position.setTitle("Software Engineer");
        position.setExpireDate(Date.valueOf(futureDate));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(position));

        // Act
        boolean result = positionService.isPresent(positionId);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsPresent_PositionExpired() throws PositionException {
        // Arrange
        int positionId = 1;
        LocalDate pastDate = LocalDate.now().minusDays(1);
        Position position = new Position();
        position.setId(positionId);
        position.setTitle("Software Engineer");
        position.setExpireDate(Date.valueOf(pastDate));
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(position));

        // Act
        boolean result = positionService.isPresent(positionId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetAllPosition() throws PositionException {
        // Arrange
        List<Position> expectedPositions = new ArrayList<>();
        Position position = new Position();
        position.setId(1);
        position.setTitle("Software Engineer");
//        expectedPositions.add(new Position(1, "Software Engineer"));
        position.setId(2);
        position.setTitle("Data Scientist");
//        expectedPositions.add(new Position(2, "Data Scientist"));
        when(positionRepository.findAll()).thenReturn(expectedPositions);

        // Act
        List<Position> result = positionService.getAllPosition();

        // Assert
        assertEquals(expectedPositions, result);
    }

    @Test
    void testSearchPositions() throws Exception {
        // Arrange
        String keyword = "Engineer";
        List<Position> expectedPositions = new ArrayList<>();
        Position position = new Position();
        position.setId(1);
        position.setTitle("Software Engineer");
//        expectedPositions.add(new Position(1, "Software Engineer"));
        when(positionRepository.findByTitleContaining(keyword)).thenReturn(expectedPositions);

        // Act
        List<Position> result = positionService.searchPositions(keyword);

        // Assert
        assertEquals(expectedPositions, result);
    }
    @Test
    void testEditPosition_SuccessfulEdit() {
        // Arrange
        Integer positionId = 1;
        Position newPosition = new Position(1,"Software Engineer", "Build web applications", "3 years of experience", BigDecimal.valueOf(5000),Date.valueOf("2023-10-10"),Date.valueOf("2023-10-30"),Date.valueOf("2023-10-10"), "FPT, Thu Duc City");
        Position existingPosition = new Position(1,"Data Engneer", "Build DataBase", "1 years of experience", BigDecimal.valueOf(5000), Date.valueOf("2023-10-20"),Date.valueOf("2023-10-10"),Date.valueOf("2023-10-10"), "FPT, HCM City");

        // Mocking the behavior of findById
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(existingPosition));

        // Mocking the behavior of positionRepository.save
        when(positionRepository.save(any())).thenReturn(existingPosition);

        // Act
        String result = positionService.editPosition(positionId, newPosition);

        // Assert
        assertEquals("Updated Successfully", result);
        assertEquals("Software Engineer", existingPosition.getTitle());
        assertEquals("Build web applications", existingPosition.getJobDescription());
        assertEquals("3 years of experience", existingPosition.getJobRequirements());
        assertEquals(BigDecimal.valueOf(5000), existingPosition.getSalary());
        assertEquals(Date.valueOf("2023-10-30"), existingPosition.getExpireDate());
        assertEquals("FPT, Thu Duc City", existingPosition.getLocation());

        // Verify that findById was called exactly once with the correct positionId
        verify(positionRepository, times(1)).findById(positionId);

        // Verify that positionRepository.save was called exactly once
        verify(positionRepository, times(1)).save(existingPosition);
    }
    @Test
    void testEditPosition_PositionNotFound() {
        // Arrange
        Integer positionId = 1;
        Position newPosition = new Position(1,"Software Engineer", "Build web applications", "3 years of experience", BigDecimal.valueOf(5000),Date.valueOf("2023-10-10"),Date.valueOf("2023-10-30"),Date.valueOf("2023-10-10"), "FPT, Thu Duc City");

        // Mocking the behavior of findById to return Optional.empty()
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act
        String result = positionService.editPosition(positionId, newPosition);

        // Assert
        assertEquals("Updated Fail: Position not found", result);

        // Verify that findById was called exactly once with the correct positionId
        verify(positionRepository, times(1)).findById(positionId);

        // Verify that positionRepository.save was never called
        verify(positionRepository, never()).save(any());
    }
    @Test
    void testDeletePosition_SuccessfulDelete() throws PositionException {
        // Arrange
        Integer positionId = 1;
        Position existingPosition = new Position(1,"Data Engneer", "Build DataBase", "1 years of experience", BigDecimal.valueOf(5000), Date.valueOf("2023-10-20"),Date.valueOf("2023-10-10"),Date.valueOf("2023-10-10"), "FPT, HCM City");

        // Mocking the behavior of findById
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(existingPosition));

        // Act
        positionService.deletePosition(positionId);

        // Verify that findById was called exactly once with the correct positionId
        verify(positionRepository, times(1)).findById(positionId);

        // Verify that positionRepository.delete was called exactly once with the correct existingPosition
        verify(positionRepository, times(1)).delete(existingPosition);
    }
    @Test
    void testDeletePosition_PositionNotFound() {
        // Arrange
        Integer positionId = 1;

        // Mocking the behavior of findById to return Optional.empty()
        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(PositionException.class, () -> positionService.deletePosition(positionId));

        // Verify that findById was called exactly once with the correct positionId
        verify(positionRepository, times(1)).findById(positionId);

        // Verify that positionRepository.delete was never called
        verify(positionRepository, never()).delete(any());
    }
    @Test
    void testGetCountPosition() {
        // Arrange
        long expectedCount = 5L;

        // Mocking the behavior of positionRepository.count
        when(positionRepository.count()).thenReturn(expectedCount);

        // Act
        long actualCount = positionService.getcountPosition();

        // Assert
        assertEquals(expectedCount, actualCount);

        // Verify that positionRepository.count was called exactly once
        // You can adjust the number of invocations based on your implementation
        // For example, if count is called multiple times in the service method, you might need to adjust this accordingly.
        verify(positionRepository, times(1)).count();
    }
    @Test
    void testCreatePost() {
        // Arrange
        Position inputPosition = new Position(1,"Data Engneer", "Build DataBase", "1 years of experience", BigDecimal.valueOf(5000), Date.valueOf("2023-10-20"),Date.valueOf("2023-10-10"),Date.valueOf("2023-10-10"), "FPT, HCM City");
        Position savedPosition = new Position(1,"Data Engneer", "Build DataBase", "1 years of experience", BigDecimal.valueOf(5000), Date.valueOf("2023-10-20"),Date.valueOf("2023-10-10"),Date.valueOf("2023-10-10"), "FPT, HCM City");

        // Mocking the behavior of positionRepository.save
        when(positionRepository.save(inputPosition)).thenReturn(savedPosition);

        // Act
        Position createdPosition = positionService.createPost(inputPosition);

        // Assert
        assertNotNull(createdPosition);
        // You may add more assertions based on your requirements

        // Verify that positionRepository.save was called exactly once with the correct inputPosition
        verify(positionRepository, times(1)).save(inputPosition);
    }
}