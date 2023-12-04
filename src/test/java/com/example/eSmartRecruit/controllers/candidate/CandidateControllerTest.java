package com.example.eSmartRecruit.controllers.candidate;

import com.example.eSmartRecruit.config.ExtractUser;
import com.example.eSmartRecruit.config.JwtService;
import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.UserRequest;
import com.example.eSmartRecruit.exception.FileUploadException;
import com.example.eSmartRecruit.models.Application;
import com.example.eSmartRecruit.models.Position;
import com.example.eSmartRecruit.models.User;
import com.example.eSmartRecruit.models.enumModel.ApplicationStatus;
import com.example.eSmartRecruit.models.enumModel.Role;
import com.example.eSmartRecruit.models.enumModel.UserStatus;
import com.example.eSmartRecruit.services.IStorageService;
import com.example.eSmartRecruit.services.impl.ApplicationService;
import com.example.eSmartRecruit.services.impl.PositionService;
import com.example.eSmartRecruit.services.impl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@ExtendWith(MockitoExtension.class)
@PrepareForTest({ExtractUser.class})
class CandidateControllerTest {
    @InjectMocks
    private CandidateController candidateController;
    @Mock
    private PositionService positionService;
    @Mock
    private UserService userService;
    @Mock
    private IStorageService storageService;
    @Mock
    private ApplicationService applicationService;
    private JwtService jwtService;

    // Biến instance để lưu trữ
    private MockMultipartFile mockFile;
    private User mockUser;
    private Position mockPos1;
    private Position mockPos2;
    private Application mockApp1;
    private Application mockApp2;
    private List<Position> mockPositions;
    private HttpServletRequest mockRequest;
    private List<Application> mockApplications;

    @BeforeEach
    void setUp() throws Exception {
        // file đúng chuẩn pdf
        mockFile = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "cv data".getBytes());

        // Setup candidate đăng nhập
        mockUser = new User();
        mockUser.setId(4);
        mockUser.setUsername("khang");
        mockUser.setPassword("$2a$10$S5x1eUGgsbXA4RJfrnc07ueCheYAVNMXsqw23/HfivFQJsaowrTXW");
        mockUser.setEmail("khang123@gmail.com");
        mockUser.setPhoneNumber("0999999999");
        mockUser.setRoleName(Role.Candidate);
        mockUser.setStatus(UserStatus.Active);
        mockUser.setCreateDate(Date.valueOf("2023-11-02"));
        mockUser.setUpdateDate(Date.valueOf("2023-11-02"));

        jwtService = new JwtService();
        var jwtToken = jwtService.generateToken(mockUser);

        // lenient().when giả lập một phương thức mà bạn không quan tâm đến việc nó có được gọi hay không,
        // và bạn chỉ muốn trả về giá trị mặc định nếu nó được gọi.
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(4);
        lenient().when(userService.isEnabled(4)).thenReturn(true);
        lenient().when(userService.getUserById(4)).thenReturn(mockUser);
        mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);


        // setup list position
        mockPositions = new ArrayList<>();

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
        mockPositions.add(mockPos1);

        mockPos2 = new Position();
        mockPos2.setId(2);
        mockPos2.setTitle("Security Engineer");
        mockPos2.setJobDescription("Responsible for web application security");
        mockPos2.setJobRequirements("3 years of experience");
        mockPos2.setSalary(BigDecimal.valueOf(5000));
        mockPos2.setPostDate(Date.valueOf("2023-10-10"));
        mockPos2.setExpireDate(Date.valueOf("2023-10-30"));
        mockPos2.setUpdateDate(Date.valueOf("2023-10-10"));
        mockPos2.setLocation("FPT, Thu Duc City");
        mockPositions.add(mockPos2);

        //setup applications
        mockApplications = new ArrayList<>();
        mockApp1 = new Application();
        mockApp1.setId(1);
        mockApp1.setCandidateID(4);
        mockApp1.setPositionID(1);
        mockApp1.setStatus(ApplicationStatus.Pending);
        mockApp1.setCv("application1.pdf");
        mockApp1.setCreateDate(Date.valueOf("2023-11-5"));
        mockApp1.setUpdateDate(Date.valueOf("2023-11-5"));
        mockApplications.add(mockApp1);

        mockApp2 = new Application();
        mockApp2.setId(1);
        mockApp2.setCandidateID(4);
        mockApp2.setPositionID(2);
        mockApp2.setStatus(ApplicationStatus.Pending);
        mockApp2.setCv("application2.pdf");
        mockApp2.setCreateDate(Date.valueOf("2023-11-5"));
        mockApp2.setUpdateDate(Date.valueOf("2023-11-5"));
        mockApplications.add(mockApp2);
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

    @Test
    void home_success() throws Exception {
        // Mocking positionService để trả về mockPositions
        when(positionService.getAllPosition()).thenReturn(mockPositions);

        ResponseEntity<ResponseObject> responseEntity = candidateController.home();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.LIST_SUCCESS, responseObject.getMessage());

        List<Position> returnedData = (List<Position>) responseObject.getData();
        assertNotNull(returnedData);
        assertEquals(2, returnedData.size());

        verify(positionService, times(1)).getAllPosition();
    }

    @Test
    void home_internalServerError() throws Exception {
        // Mock hành vi của positionService.getAllPosition() để ném ra một exception
        when(positionService.getAllPosition()).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        // Gọi API
        ResponseEntity<ResponseObject> responseEntity = candidateController.home();

        // Xác nhận phản hồi
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ResponseObject responseObject = responseEntity.getBody();
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
        // Đảm bảo rằng data là null hoặc không có trong trường hợp lỗi
        assertNull(responseObject.getData());

        // Xác nhận rằng positionService.getAllPosition() được gọi một lần
        verify(positionService, times(1)).getAllPosition();
    }

    ///////////////
    @Test
    void getDetailPosition_success() throws Exception {
        when(positionService.getSelectedPosition(1)).thenReturn(mockPos1);
        ResponseEntity<ResponseObject> responseEntity = candidateController.getDetailPosition(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        Position returnedPosition = (Position) responseObject.getData();
        assertNotNull(returnedPosition);
        assertEquals(1, returnedPosition.getId());
        assertEquals(mockPos1.getTitle(), returnedPosition.getTitle());

        verify(positionService, times(1)).getSelectedPosition(1);
    }

    @Test
    void getDetailPosition_notFound() throws Exception {
        // chỉ có id 1,2, mặc định gọi id nào thì cũng trả về null
        when(positionService.getSelectedPosition(anyInt())).thenReturn(null);
        ResponseEntity<ResponseObject> responseEntity = candidateController.getDetailPosition(1);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.POSITION_NOT_FOUND, responseObject.getMessage());
        assertNull(responseObject.getData());

        verify(positionService, times(1)).getSelectedPosition(1);
    }

    @Test
    void getDetailPosition_internalServerError() throws Exception {
        // Mock hành vi của positionService.getSelectedPosition() để ném ra một exception
        when(positionService.getSelectedPosition(anyInt())).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        // gọi api
        ResponseEntity<ResponseObject> responseEntity = candidateController.getDetailPosition(1);

        // xác nhận phản hồi
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
        // xác nhận body null
        assertNull(responseObject.getData());

        // kiểm tra chắc chắc hàm getSelectedPosition được gọi 1 lần
        verify(positionService, times(1)).getSelectedPosition(1);
    }

    /////////////////////////
    @Test
    void applyForPosition_success() throws Exception {
        when(storageService.storeFile(mockFile)).thenReturn("generatedFileName");
        when(positionService.isPresent(1)).thenReturn(true);
        when(applicationService.apply(any(Application.class))).thenReturn(ResponseObject.APPLY_SUCCESS);

        ResponseEntity<ResponseObject> responseEntity = candidateController.applyForPosition(1, mockRequest, mockFile);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.APPLY_SUCCESS, responseObject.getMessage());

        verify(storageService, times(1)).storeFile(mockFile);
        verify(positionService, times(1)).isPresent(1);
        verify(applicationService, times(1)).apply(any(Application.class));
    }


    @Test
    void applyForPosition_badRequest_whenCVNotEnabled() {
        ResponseEntity<ResponseObject> responseEntity = candidateController.applyForPosition(1, mockRequest, null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.CV_NOT_FOUND, responseObject.getMessage());
    }

    @Test
    void applyForPosition_badRequest_whenPositionNotOpen() throws Exception {
        // mock positon - false
        when(positionService.isPresent(1)).thenReturn(false);

        ResponseEntity<ResponseObject> responseEntity = candidateController.applyForPosition(1, mockRequest, mockFile);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.NOT_OPEN, responseObject.getMessage());
    }

    @Test
    void applyForPosition_ErrorNotPDF() throws Exception {
        // file không phải là pdf
        MockMultipartFile mockFile = new MockMultipartFile("cv", "cv.docx", "application/pdf", "cv data".getBytes());

        lenient().when(storageService.isPDF(mockFile)).thenReturn(false);
        when(storageService.storeFile(mockFile)).thenThrow(new FileUploadException("Only pdf file accepted!"));
        lenient().when(positionService.isPresent(1)).thenReturn(true);
        lenient().when(applicationService.apply(any(Application.class))).thenReturn("Only pdf file accepted!");

        ResponseEntity<ResponseObject> responseEntity = candidateController.applyForPosition(1, mockRequest, mockFile);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals("ERROR", responseObject.getStatus());
        assertEquals("Only pdf file accepted!", responseObject.getMessage());
    }

    //////////////////////////////////
    @Test
    void getMyApplications_success() throws Exception {

        when(applicationService.getApplicationsByCandidateId(4)).thenReturn(mockApplications);
        when(positionService.getSelectedPosition(1)).thenReturn(mockPos1);
        when(positionService.getSelectedPosition(2)).thenReturn(mockPos2);

        ResponseEntity<ResponseObject> responseEntity = candidateController.getMyApplications(mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        List<Map<String, Object>> expectedData = (List<Map<String, Object>>) responseObject.getData();
        assertNotNull(expectedData);
        assertEquals(2, expectedData.size());

        Map<String, Object> applicationMap = expectedData.get(0);
        assertNotNull(applicationMap);
        assertEquals(mockApp1.getId(), applicationMap.get("applicationID"));
        assertEquals(positionService.getSelectedPosition(mockApp1.getPositionID()).getTitle(), applicationMap.get("positionTitle"));
        assertEquals(mockApp1.getStatus(), applicationMap.get("status"));
        assertNotNull(applicationMap.get("applicationDate"));
    }

//    @Test
//    void getMyApplications_whenUserNotEnabled() throws Exception {
//        UserNotEnabled();
//
//        ResponseEntity<ResponseObject> responseEntity = candidateController.getMyApplications(mockRequest1);//
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void getMyApplications_notFound() throws Exception {
        when(applicationService.getApplicationsByCandidateId(anyInt())).thenReturn(Collections.emptyList());

        ResponseEntity<ResponseObject> responseEntity = candidateController.getMyApplications(mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.APPLICATION_NOT_FOUND, responseObject.getMessage());
        assertEquals(Collections.emptyList(), responseEntity.getBody().getData());
    }

    @Test
    void getMyApplications_internalServerError() throws Exception {
        when(applicationService.getApplicationsByCandidateId(anyInt())).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        ResponseEntity<ResponseObject> responseEntity = candidateController.getMyApplications(mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
        assertNull(responseObject.getData());
    }

    ////////////////
    @Test
    void getApplicationDetails_success() throws Exception {
        // Giả lập dữ liệu trả về từ service
        when(applicationService.getApplicationById(1)).thenReturn(mockApp1);
        when(positionService.getSelectedPosition(1)).thenReturn(mockPos1);

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = candidateController.getApplicationDetails(1, mockRequest);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        // Kiểm tra dữ liệu trả về
        Map<String, Object> expectedData = new LinkedHashMap<>();
        expectedData.put("applicationID", 1);
        expectedData.put("candidateName", mockUser.getUsername());
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

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = candidateController.getApplicationDetails(1, mockRequest);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }

    /////////////////
    @Test
    void getDetailUser_success() {
        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = candidateController.getDetailUser(mockRequest);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        Map<String, String> expectedData = new LinkedHashMap<>();
        expectedData.put("username", "khang");
        expectedData.put("email", "khang123@gmail.com");
        expectedData.put("phonenumber", "0999999999");

        // So sánh dữ liệu trả về với dữ liệu mong đợi
        assertEquals(expectedData, responseObject.getData());
    }


    @Test
    void getDetailUser_internalServerError() throws Exception {
        when(userService.isEnabled(4)).thenReturn(true);
        when(userService.getUserById(4)).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = candidateController.getDetailUser(mockRequest);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }

    ////////////////
    @Test
    void updateUser_success() throws Exception {
        // Giả lập dữ liệu người dùng cần cập nhật
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("newemail@gmail.com");
        userRequest.setPhoneNumber("987654321");

        // Giả lập người dùng sau khi cập nhật
        User updatedUser = new User();
        updatedUser.setEmail("newemail@gmail.com");
        updatedUser.setPhoneNumber("987654321");

        when(userService.updateUser(userRequest, 4)).thenReturn(updatedUser);

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = candidateController.updateUser(mockRequest, userRequest);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());

        Map<String, Object> expectedData = new LinkedHashMap<>();
        expectedData.put("email", "newemail@gmail.com");
        expectedData.put("phoneNumber", "987654321");

        // Kiểm tra lại dữ liệu trả về
        assertEquals(expectedData, responseObject.getData());
    }


    @Test
    void updateUser_internalServerError() throws Exception {
        // Giả lập ngoại lệ từ service
        when(userService.updateUser(any(UserRequest.class), eq(4))).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = candidateController.updateUser(mockRequest, new UserRequest());

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }


    ////////////////
    @Test
    void updateApplication_success() throws Exception {
        lenient().when(storageService.storeFile(mockFile)).thenReturn("generatedFileName.pdf");
        lenient().when(positionService.isPresent(1)).thenReturn(true);

        var newApplication = new Application("generatedFileName.pdf");
        lenient().when(applicationService.update(4, newApplication, 1)).thenReturn(ResponseObject.UPDATED_SUCCESS);

        ResponseEntity<ResponseObject> responseEntity = candidateController.updateApplyPosition(1, mockRequest, mockFile);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.UPDATED_SUCCESS, responseObject.getMessage());
    }

    @Test
    void updateApplyPosition_internalServerError() throws Exception {
        // Giả lập ngoại lệ từ service
        when(storageService.storeFile(mockFile)).thenReturn("generatedFileName");
        when(applicationService.update(4, new Application("generatedFileName"), 1)).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        // Gọi API để nhận response
        ResponseEntity<ResponseObject> responseEntity = candidateController.updateApplyPosition(1, mockRequest, mockFile);

        // Kiểm tra HTTP status code
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        // Kiểm tra response body
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());
    }

    @Test
    void deleteApplication_success() {
        lenient().when(applicationService.deletejob(4, 1)).thenReturn(ResponseObject.DELETED_SUCCESS);

        ResponseEntity<ResponseObject> responseEntity = candidateController.deleteApplyPosition(1, mockRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals("SUCCESS", responseObject.getStatus());
        assertEquals(ResponseObject.DELETED_SUCCESS, responseObject.getMessage());

        verify(applicationService, times(1)).deletejob(4, 1);
    }

    @Test
    public void deleteApplyPosition_internalServerError() throws Exception {
        when(applicationService.deletejob(4, 1)).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));

        ResponseEntity<ResponseObject> responseEntity = candidateController.deleteApplyPosition(1, mockRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseObject.getMessage());

        verify(applicationService, times(1)).deletejob(4, 1);
    }

}