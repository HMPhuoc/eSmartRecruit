package com.example.eSmartRecruit.controllers.interviewer;

import com.example.eSmartRecruit.config.ExtractUser;
import com.example.eSmartRecruit.config.JwtService;
import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.ReportRequest;
import com.example.eSmartRecruit.controllers.request_reponse.request.UserRequest;
import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.models.*;
import com.example.eSmartRecruit.models.enumModel.*;
import com.example.eSmartRecruit.services.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@PrepareForTest({ExtractUser.class})
class InterviewerControllerTest {
    @InjectMocks
    private InterviewerController interviewerController;
    @Mock
    private InterviewSessionService interviewSessionService;
    @Mock
    private ReportService reportService;
    @Mock
    private UserService userService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private PositionService positionService;
    private JwtService jwtService;
    // Biến instance để lưu trữ
    private MockMultipartFile mockFile;
    private User mockInterviewer;
    private HttpServletRequest mockRequest;
    List<InterviewSession> mockSessions;
    InterviewSession mockSession1;
    InterviewSession mockSession2;
    ReportRequest mockReportRequest;
    User mockCandidate;

    private Position mockPos1;
    private Application mockApp1;

    @BeforeEach
    void setUp() throws Exception {
        // file đúng chuẩn pdf
        mockFile = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cv data".getBytes());

        // Setup interview đăng nhập
        mockInterviewer = new User();
        mockInterviewer.setId(1);
        mockInterviewer.setUsername("lien");
        mockInterviewer.setPassword("$2a$10$ZrKkagiTQZc0tcTAO7ETQOFi8S90ZGxC60OJfEuCr9a8G61uLXTdq");
        mockInterviewer.setEmail("bichlien@gmail.com");
        mockInterviewer.setPhoneNumber("0999999999");
        mockInterviewer.setRoleName(Role.Interviewer);
        mockInterviewer.setStatus(UserStatus.Active);
        mockInterviewer.setCreateDate(Date.valueOf("2023-11-02"));
        mockInterviewer.setUpdateDate(Date.valueOf("2023-11-02"));

        jwtService = new JwtService();
        var jwtToken = jwtService.generateToken(mockInterviewer);

        // lenient().when giả lập một phương thức mà bạn không quan tâm đến việc nó có được gọi hay không,
        // và bạn chỉ muốn trả về giá trị mặc định nếu nó được gọi.
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(1);
        lenient().when(userService.isEnabled(1)).thenReturn(true);
        lenient().when(userService.getUserById(1)).thenReturn(mockInterviewer);
        mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        // Mocking interview session data
        mockSession1 = new InterviewSession();
        mockSession1.setId(1);
        mockSession1.setInterviewerID(1);
        mockSession1.setApplicationID(1);
        mockSession1.setDate(Date.valueOf("2023-11-10"));
        mockSession1.setLocation("Vo Van Ngan");
        mockSession1.setStatus(SessionStatus.Already);
        mockSession1.setResult(SessionResult.Good);
        mockSession1.setNotes("Tốt");

        mockSession2 = new InterviewSession();
        mockSession2.setId(2);
        mockSession2.setInterviewerID(1);
        mockSession1.setApplicationID(2);
        mockSession1.setDate(Date.valueOf("2023-11-12"));
        mockSession1.setLocation("Vo Van Ngan");
        mockSession1.setStatus(SessionStatus.Already);
        mockSession1.setResult(SessionResult.Good);
        mockSession1.setNotes("Tốt");

        mockSessions = Arrays.asList(mockSession1, mockSession2);


        // Mocking report request
        mockReportRequest = new ReportRequest();
        mockReportRequest.setReportName("Mock Report");
        mockReportRequest.setReportData("Mock Report Data");


        // mock candidate
        mockCandidate = new User();
        mockCandidate.setId(2);
        mockCandidate.setUsername("thang");
        mockCandidate.setPassword("$2a$10$T7/ttTcvqqo3pFkqztnTmODYJvpPeHIjyUPmeKlkjwW8XSRG6q/CK");
        mockCandidate.setEmail("thang1234@gmail.com");
        mockCandidate.setPhoneNumber("0999996789");
        mockCandidate.setRoleName(Role.Candidate);
        mockCandidate.setStatus(UserStatus.Active);
        mockCandidate.setCreateDate(Date.valueOf("2023-11-08"));
        mockCandidate.setUpdateDate(Date.valueOf("2023-11-08"));


        // setup position
        mockPos1 = new Position();
        mockPos1.setId(1);
        mockPos1.setTitle("Software Engineer");
        mockPos1.setJobDescription("Build web applications");
        mockPos1.setJobRequirements("3 years of experience");
        mockPos1.setSalary(BigDecimal.valueOf(5000));
        mockPos1.setPostDate(Date.valueOf("2023-10-10"));
        mockPos1.setExpireDate(Date.valueOf("2023-10-30"));
        mockPos1.setUpdateDate(Date.valueOf("2023-10-10"));
        mockPos1.setLocation("FPT, Thu Duc City");


        mockApp1 = new Application();
        mockApp1.setId(1);
        mockApp1.setCandidateID(4);
        mockApp1.setPositionID(1);
        mockApp1.setStatus(ApplicationStatus.Pending);
        mockApp1.setCv("application1.pdf");
        mockApp1.setCreateDate(Date.valueOf("2023-11-5"));
        mockApp1.setUpdateDate(Date.valueOf("2023-11-5"));

    }

    // thiết lập user cho trường hợp user k tồn tại
    User mockUser1;
    ExtractUser mockUserInfo1;
    JwtService jwtService1 = new JwtService();
    HttpServletRequest mockRequest1;

    // mock đối tượng user không tồn tại
    // mỗi khi sử dụng hàm này thì đổi mockRequest -> mockRequest1
    void UserNotEnabled() throws Exception {
        // Mock user
        mockUser1 = new User();
        mockUser1.setId(6);
        mockUserInfo1 = mock(ExtractUser.class);
        lenient().when(mockUserInfo1.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo1.getUserId()).thenReturn(6);
        lenient().when(userService.isEnabled(8)).thenReturn(true);// tìm 8 thay v 6

        jwtService1 = new JwtService();
        var jwtToken1 = jwtService1.generateToken(mockUser1);
        mockRequest1 = mock(HttpServletRequest.class);
        lenient().when(mockRequest1.getHeader("Authorization")).thenReturn("Bearer " + jwtToken1);
    }


    ////////////////
    @Test
    void getInterviewerSession_success() throws Exception {

        when(interviewSessionService.findByInterviewerID(anyInt())).thenReturn(mockSessions);
        ResponseEntity<ResponseObject> responseEntity = interviewerController.getInterviewerSession(mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals("SUCCESS", responseObject.getStatus());

        List<InterviewSession> expectedData = (List<InterviewSession>) responseEntity.getBody().getData();
        assertNotNull(expectedData);
        assertEquals(2, expectedData.size());

        InterviewSession sessionMap1 = expectedData.get(0);
        assertEquals(mockSession1, sessionMap1);
    }

//    @Test
//    void getInterviewerSession_whenUserNotEnabled() throws Exception {
//        UserNotEnabled();
//
//        ResponseEntity<ResponseObject> responseEntity = interviewerController.getInterviewerSession(mockRequest1);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void getInterviewerSession_noSessionsFound() throws Exception {
        when(interviewSessionService.findByInterviewerID(1)).thenReturn(Collections.emptyList());

        ResponseEntity<ResponseObject> responseEntity = interviewerController.getInterviewerSession(mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals("SUCCESS", responseObject.getStatus());
        assertEquals(Collections.emptyList(), responseObject.getData());
    }

    @Test
    void getInterviewerSession_internalServerError() throws Exception {
        when(interviewSessionService.findByInterviewerID(anyInt())).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        ResponseEntity<ResponseObject> responseEntity = interviewerController.getInterviewerSession(mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }

    ////////////////////////////////
    @Test
    void findByInterviewSessionID_success() throws Exception {
        when(interviewSessionService.findByID(anyInt())).thenReturn(mockSession1);

        ResponseEntity<ResponseObject> responseEntity = interviewerController.findByInterviewSessionID(1, mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(mockSession1, responseObject.getData());
    }

//    @Test
//    void findByInterviewSessionID_whenUserNotEnabled() throws Exception {
//        UserNotEnabled();
//
//        ResponseEntity<ResponseObject> responseEntity = interviewerController.findByInterviewSessionID(1, mockRequest1);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void findByInterviewSessionID_internalServerError() throws Exception {
        when(interviewSessionService.findByID(anyInt())).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        ResponseEntity<ResponseObject> responseEntity = interviewerController.findByInterviewSessionID(1, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }

    ///////////////////
    @Test
    void reportInterviewSession_success() throws Exception {
        when(interviewSessionService.isAlready(anyInt())).thenReturn(true);

        ResponseEntity<ResponseObject> responseEntity = interviewerController.reportInterviewSession(1, mockRequest, mockReportRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
    }

//    @Test
//    void reportInterviewSession_whenUserNotEnabled() throws Exception {
//        UserNotEnabled();
//
//        ResponseEntity<ResponseObject> responseEntity = interviewerController.reportInterviewSession(1, mockRequest1, mockReportRequest);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void reportInterviewSession_interviewSessionNotDone() throws Exception {
        when(interviewSessionService.isAlready(anyInt())).thenReturn(false);

        ResponseEntity<ResponseObject> responseEntity = interviewerController.reportInterviewSession(1, mockRequest, mockReportRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERVIEW_SESSION, responseObject.getMessage());
    }

    @Test
    void reportInterviewSession_internalServerError() throws Exception {
        when(interviewSessionService.isAlready(anyInt())).thenReturn(true);
        when(reportService.reportInterviewSession(any(Report.class)))
                .thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        ResponseEntity<ResponseObject> responseEntity = interviewerController.reportInterviewSession(1, mockRequest, mockReportRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }

    /////////////////
    @Test
    void getApplicationDetails_success() throws Exception{
        // Giả lập dữ liệu trả về từ service
        when(applicationService.getApplicationById(anyInt())).thenReturn(mockApp1);
        when(userService.getUserById(anyInt())).thenReturn(mockCandidate);
        when(positionService.getSelectedPosition(mockApp1.getPositionID())).thenReturn(mockPos1);

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = interviewerController.getApplicationDetails(1, mockRequest);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        // Kiểm tra dữ liệu trả về
        Map<String, Object> expectedData = new LinkedHashMap<>();
        expectedData.put("applicationID", 1);
        expectedData.put("candidateName", mockCandidate.getUsername());
        expectedData.put("positionTitle", mockPos1.getTitle());
        expectedData.put("status", mockApp1.getStatus());
        expectedData.put("cv", mockApp1.getCv());
        expectedData.put("applicationDate", mockApp1.getCreateDate().toString());

        // So sánh dữ liệu trả về với dữ liệu mong đợi
        assertEquals(expectedData, responseEntity.getBody().getData());
    }

    @Test
    void getApplicationDetails_internalServerError() throws Exception {
        // Giả lập một ngoại lệ từ service
        when(applicationService.getApplicationById(1)).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));
        lenient().when(userService.getUserById(2)).thenReturn(mockCandidate);

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = interviewerController.getApplicationDetails(1, mockRequest);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }


    //////////////////
    @Test
    void getDetailUserInterviewer_success() throws JSONException, UserException {
        ResponseEntity<ResponseObject> responseEntity = interviewerController.getDetailUserInterviewer(mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        Map<String, String> expectedData = new HashMap<>();
        expectedData.put("username", mockInterviewer.getUsername());
        expectedData.put("email", mockInterviewer.getEmail());
        expectedData.put("phonenumber", mockInterviewer.getPhoneNumber());

        assertEquals(expectedData, responseObject.getData());
    }

//    @Test
//    void getDetailUserInterviewer_whenUserNotEnabled() throws Exception {
//        UserNotEnabled();
//
//        ResponseEntity<ResponseObject> responseEntity = interviewerController.getDetailUserInterviewer(mockRequest1);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void getDetailUserInterviewer_internalServerError() throws UserException, JSONException {
        when(userService.getUserById(anyInt())).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        ResponseEntity<ResponseObject> responseEntity = interviewerController.getDetailUserInterviewer(mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }


    ///////////////////////
    @Test
    void updateUserInterviewer_success() throws Exception {
        // mock user update (do chỉ được thay đổi mail + sdt nên chỉ set lại 2 cái này)
        User updateMockUser = mockInterviewer;
        updateMockUser.setEmail("newEmail@example.com");
        updateMockUser.setPhoneNumber("0912345678");

        // mock userRequest
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("newEmail@example.com");
        userRequest.setPhoneNumber("0912345678");

        lenient().when(userService.updateUser(userRequest, mockInterviewer.getId())).thenReturn(updateMockUser);

        ResponseEntity<ResponseObject> responseEntity = interviewerController.updateUserInterviewer(mockRequest, userRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", updateMockUser.getUsername());
        data.put("email", updateMockUser.getEmail());
        data.put("phoneNumber", updateMockUser.getPhoneNumber());

        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(data, responseObject.getData());
    }

//    @Test
//    void updateUserInterviewer_whenUserNotEnabled() throws Exception {
//        UserNotEnabled();
//
//        // mock userRequest
//        UserRequest userRequest = new UserRequest();
//        userRequest.setEmail("newEmail@example.com");
//        userRequest.setPhoneNumber("0912345678");
//
//        ResponseEntity<ResponseObject> responseEntity = interviewerController.updateUserInterviewer(mockRequest1, userRequest);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void updateUserInterviewer_internalServerError() throws Exception {
        when(userService.updateUser(any(), anyInt())).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        // mock userRequest
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("newEmail@example.com");
        userRequest.setPhoneNumber("0912345678");

        ResponseEntity<ResponseObject> responseEntity = interviewerController.updateUserInterviewer(mockRequest, userRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }


    /////////////////
    @Test
    void getCandidateInformation() throws UserException, JSONException {
        User mockUser = new User();
        mockUser.setId(5);
        mockUser.setUsername("thang1");
        mockUser.setPassword("$2a$10$T7/ttTcvqqo3pFkqztnTmODYJvpPeHIjyUPmeKlkjwW8XSRG6q/CK");
        mockUser.setEmail("thang1234@gmail.com");
        mockUser.setPhoneNumber("0999996789");
        mockUser.setRoleName(Role.Candidate);
        mockUser.setStatus(UserStatus.Active);
        mockUser.setCreateDate(Date.valueOf("2023-11-08"));
        mockUser.setUpdateDate(Date.valueOf("2023-11-08"));

        var jwtToken = jwtService.generateToken(mockUser);
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(4);
        when(userService.isEnabled(5)).thenReturn(true);
        when(userService.getUserById(5)).thenReturn(mockUser);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        ResponseEntity<ResponseObject> responseEntity = interviewerController.getCandidateInformation(5, mockRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        Map<String, String> data = new LinkedHashMap<>();
        data.put("username", "thang1");
        data.put("email", "thang1234@gmail.com");
        data.put("phonenumber", "0999996789");


        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(data, responseObject.getData());
    }


    @Test
    void getCandidateInformation_success() throws Exception{
        when(userService.getUserById(mockCandidate.getId())).thenReturn(mockCandidate);

        ResponseEntity<ResponseObject> responseEntity = interviewerController.getCandidateInformation(2, mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        Map<String, String> data = new LinkedHashMap<>();
        data.put("username", "thang");
        data.put("email", "thang1234@gmail.com");
        data.put("phonenumber", "0999996789");

        assertEquals(data, responseObject.getData());
        verify(userService, times(1)).getUserById(mockCandidate.getId());
    }

//    @Test
//    void getCandidateInformation_whenUserNotEnabled() throws Exception {
//        UserNotEnabled();
//
//        ResponseEntity<ResponseObject> responseEntity = interviewerController.getCandidateInformation(1, mockRequest1);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//
//        ResponseObject responseObject = responseEntity.getBody();
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void getCandidateInformation_NotACandidate() throws Exception {
        // Mock user
        User interviewer2 = mockInterviewer;
        interviewer2.setRoleName(Role.Admin);

        when(userService.getUserById(5)).thenReturn(interviewer2);

        ResponseEntity<ResponseObject> responseEntity = interviewerController.getCandidateInformation(5, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.NOT_CANDIDATE, responseObject.getMessage());
    }

    @Test
    void getCandidateInformation_internalServerError() throws Exception {
        when(userService.getUserById(1)).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        ResponseEntity<ResponseObject> responseEntity = interviewerController.getCandidateInformation(1, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ResponseObject responseObject = responseEntity.getBody();
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }

}