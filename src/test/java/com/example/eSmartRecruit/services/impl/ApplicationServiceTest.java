package com.example.eSmartRecruit.services.impl;

import com.example.eSmartRecruit.exception.ApplicationException;
import com.example.eSmartRecruit.exception.NotFoundException;
import com.example.eSmartRecruit.models.Application;
import com.example.eSmartRecruit.models.enumModel.ApplicationStatus;
import com.example.eSmartRecruit.repositories.ApplicationRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContextException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ApplicationServiceTest {
    private ApplicationService applicationService;
    private ApplicationRepos applicationRepository;
    @BeforeEach
    void setUp() {
        applicationRepository = mock(ApplicationRepos.class);
        applicationService = new ApplicationService(applicationRepository);
    }
    @Test
    void testApply_SuccessfulApplication() {
        // Arrange
        Application application = new Application();
        application.setCandidateID(1);
        application.setPositionID(1);

        // Mocking behavior to simulate a successful application
        when(applicationRepository.findByCandidateIDAndPositionID(1, 1)).thenReturn(java.util.Optional.empty());
        when(applicationRepository.save(application)).thenReturn(application);

        // Act
        String result = applicationService.apply(application);

        // Assert
        assertEquals("Successfully applied!", result);
        verify(applicationRepository, times(1)).findByCandidateIDAndPositionID(1, 1);
        verify(applicationRepository, times(1)).save(application);
    }
    @Test
    void testApply_AlreadyApplied() {
        // Arrange
        Application application = new Application();
        application.setCandidateID(1);
        application.setPositionID(1);

        // Mocking behavior to simulate an already applied situation
        when(applicationRepository.findByCandidateIDAndPositionID(1, 1)).thenReturn(java.util.Optional.of(application));

        // Act
        String result = applicationService.apply(application);

        // Assert
        assertEquals("You have already applied for this position!", result);
        verify(applicationRepository, times(1)).findByCandidateIDAndPositionID(1, 1);
        verify(applicationRepository, never()).save(any());
    }
    @Test
    void testIsApplied_ApplicationExists() {
        // Arrange
        Integer candidateID = 1;
        Integer positionID = 1;
        when(applicationRepository.findByCandidateIDAndPositionID(candidateID, positionID))
                .thenReturn(Optional.of(new Application()));

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        boolean result = applicationService.isApplied(candidateID, positionID);

        // Assert
        assertTrue(result);
    }
    @Test
    void testIsApplied_ApplicationDoesNotExist() {
        // Arrange
        Integer candidateID = 1;
        Integer positionID = 1;
        when(applicationRepository.findByCandidateIDAndPositionID(candidateID, positionID)).thenReturn(Optional.empty());

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        boolean result = applicationService.isApplied(candidateID, positionID);

        // Assert
        assertFalse(result);
    }
    @Test
    void testGetApplicationsByCandidateId() throws ApplicationException {
        // Arrange
        Integer candidateID = 1;
        List<Application> expectedApplications = Arrays.asList(new Application(), new Application());
        when(applicationRepository.findByCandidateID(candidateID)).thenReturn(expectedApplications);

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        List<Application> result = applicationService.getApplicationsByCandidateId(candidateID);

        // Assert
        assertEquals(expectedApplications, result);
    }
    @Test
    void testGetApplicationById_ApplicationExists() throws ApplicationException, ApplicationException {
        // Arrange
        Integer applicationID = 1;
        Integer candidateID = 1;
        Application expectedApplication = new Application();
        when(applicationRepository.findById(applicationID)).thenReturn(Optional.of(expectedApplication));

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        Application result = applicationService.getApplicationById(applicationID);

        // Assert
        assertEquals(expectedApplication, result);
    }
    @Test
    void testGetApplicationById_ApplicationDoesNotExist() {
        // Arrange
        Integer applicationID = 1;
        Integer candidateID = 1;
        when(applicationRepository.findById(applicationID)).thenReturn(Optional.empty());

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act and Assert
        assertThrows(ApplicationException.class, () -> applicationService.getApplicationById(applicationID));
    }
    @Test
    void testUpdate_Success() {
        // Arrange
        Integer candidateId = 1;
        Integer applicationId = 1;
        Application newApplication = new Application();
        newApplication.setCv("Updated CV");

        Application existingApplication = new Application();
        existingApplication.setCandidateID(candidateId);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(existingApplication));

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        String result = applicationService.update(candidateId, newApplication, applicationId);

        // Assert
        assertEquals("Updated Successfully", result);
        assertEquals("Updated CV", existingApplication.getCv());
    }
    @Test
    void testUpdate_ApplicationNotFound() {
        // Arrange
        Integer candidateId = 1;
        Integer applicationId = 1;
        Application newApplication = new Application();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        String result = applicationService.update(candidateId, newApplication, applicationId);

        // Assert
        assertTrue(result.contains("Application not found!"));
    }
    @Test
    void testUpdate_NotYourApplication() {
        // Arrange
        Integer candidateId = 1;
        Integer applicationId = 1;
        Application newApplication = new Application();
        newApplication.setCv("Updated CV");

        Application existingApplication = new Application();
        existingApplication.setCandidateID(2);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(existingApplication));

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        String result = applicationService.update(candidateId, newApplication, applicationId);

        // Assert
        assertTrue(result.contains("This is not your application!"));
    }

    @Test
    public void testAdminUpdate_StatusApproved() {
        // Arrange
        int applicationId = 1;
        ApplicationStatus status = ApplicationStatus.Approved;
        Application expectedApplication = new Application(applicationId, 1, "gsdfgsdgf");
        expectedApplication.setStatus(status);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(expectedApplication));

        // Act
        String result = applicationService.adminUpdate(applicationId, status);

        // Assert
        verify(applicationRepository, times(1)).findById(applicationId);
        verify(applicationRepository, times(1)).save(expectedApplication);
        assertEquals("Approve application successfully!", result);
    }

    @Test
    void testIsPresent_ApplicationFound() {
        // Arrange
        Integer applicationId = 1;

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(new Application()));

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        boolean result = applicationService.isPresent(applicationId);

        // Assert
        assertTrue(result);
    }
    @Test
    void testIsPresent_ApplicationNotFound() {
        // Arrange
        Integer applicationId = 1;

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        boolean result = applicationService.isPresent(applicationId);

        // Assert
        assertFalse(result);
    }
    @Test
    void testIsPresent_ApplicationException() {
        // Arrange
        Integer applicationId = 1;

        when(applicationRepository.findById(applicationId)).thenThrow(new NotFoundException("Cant find this application!"));

        ApplicationService applicationService = new ApplicationService(applicationRepository);

        // Act
        boolean result = applicationService.isPresent(applicationId);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testDeleteJob_SuccessfulDeletion() throws ApplicationException {
        // Arrange
        int candidateId = 123;
        int jobId = 1;

        Application application = new Application();
        application.setCandidateID(candidateId);

        // Mock the repository method calls
        when(applicationRepository.findById(anyInt())).thenReturn(Optional.of(application));
//        when(applicationRepository.existsById(anyInt())).thenReturn(true);

        // Act
        String result = applicationService.deletejob(candidateId, jobId);

        // Assert
        verify(applicationRepository, times(2)).findById(anyInt());
        verify(applicationRepository, times(1)).deleteById(anyInt());

        // Assert the result message
        assertEquals("Deleted Success", result);
    }

    @Test
    public void testDeleteJob_ApplicationNotFound() throws ApplicationException {
        // Arrange
        int candidateId = 123;
        int jobId = 1;

        // Mock the repository method calls
        when(applicationRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act
        String result = applicationService.deletejob(candidateId, jobId);

        // Assert
        verify(applicationRepository, times(1)).findById(jobId);
        verify(applicationRepository, never()).deleteById(anyInt());

        // Assert the result message
        assertEquals("Cant find this application!", result);
    }

    @Test
    public void testDeleteJob_NotYourApplication() throws ApplicationException {
        // Arrange
        int candidateId = 123;
        int jobId = 1;

        Application application = new Application();
        application.setCandidateID(456);

        // Mock the repository method calls
        when(applicationRepository.findById(anyInt())).thenReturn(Optional.of(application));

        // Act
        String result = applicationService.deletejob(candidateId, jobId);

        // Assert
        verify(applicationRepository, times(2)).findById(jobId);
        verify(applicationRepository, never()).deleteById(anyInt());

        // Assert the result message
        assertEquals("This is not your application!", result);
    }

    @Test
    public void testDeleteJob_ExceptionThrown() throws ApplicationException {
        // Arrange
        int candidateId = 123;
        int jobId = 1;

        // Mock the repository method calls
        when(applicationRepository.findById(anyInt())).thenThrow(new NotFoundException("Cant find this application!"));
//        doAnswer(new ApplicationException("Something went wrong!")).when(applicationRepository).findById(jobId);
        // Act
        String result = applicationService.deletejob(candidateId, jobId);

        // Assert
        verify(applicationRepository, times(1)).findById(jobId);
        verify(applicationRepository, never()).deleteById(anyInt());

        // Assert the result message
        assertEquals("Cant find this application!", result);
    }

    @Test
    public void testGetCountApplication() {
        // Arrange
        long expectedCount = 10;
        when(applicationRepository.count()).thenReturn(expectedCount);

        // Act
        long actualCount = applicationService.getcountApplication();

        // Assert
        verify(applicationRepository, times(1)).count();
        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void testFindById_ApplicationFound() throws ApplicationException {
        // Arrange
        int applicationId = 1;
        Application expectedApplication = new Application(applicationId, 1, "sdfgdsfg");

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(expectedApplication));

        // Act
        Application actualApplication = applicationService.findById(applicationId);

        // Assert
        verify(applicationRepository, times(1)).findById(applicationId);
        assertEquals(expectedApplication, actualApplication);
    }

    @Test
    public void testFindById_ApplicationNotFound() {
        // Arrange
        int applicationId = 1;

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ApplicationContextException.class, () -> applicationService.findById(applicationId));
    }
}