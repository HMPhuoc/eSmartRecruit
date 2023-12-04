package com.example.eSmartRecruit.services.impl;

import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.exception.ApplicationException;
import com.example.eSmartRecruit.exception.PositionException;
import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.models.Report;
import com.example.eSmartRecruit.repositories.ReportRepos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class ReportServiceTest {

    @Mock
    private ReportService reportService;
    @Mock
    private ReportRepos reportRepos;
    @BeforeEach
    public void setup(){
        reportRepos = mock(ReportRepos.class);
        reportService = new ReportService(reportRepos);
    }

    @Test
    void testReportInterviewSession_Successful() {
        Report report = new Report();
        String result = reportService.reportInterviewSession(report);
        verify(reportRepos, times(1)).save(report);
        assertEquals("Report Successfully!", result);
    }
    @Test
    void testReportInterviewSession_Exception() {
        Report report = new Report();
        String errorMessage = "Error occurred";
        // when(reportRepos.save(report)).thenThrow(new RuntimeException(errorMessage));
        doThrow(new RuntimeException(errorMessage)).when(reportRepos).save(report);
        String result = reportService.reportInterviewSession(report);
        verify(reportRepos, times(1)).save(report);
        assertEquals(errorMessage, result);
    }
    @Test
    void testgetReportBySessionId_Report() throws Exception {
        Report report = new Report();
        when(reportRepos.findBySessionID(1)).thenReturn(report);
        Report result = reportService.getReportBySessionId(1);

        verify(reportRepos, times(1)).findBySessionID(1);
        assertEquals(report, result);
    }
}