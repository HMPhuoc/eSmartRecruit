package com.example.eSmartRecruit.controllers.admin;
import com.example.eSmartRecruit.authentication.request_reponse.RegisterRequest;
import com.example.eSmartRecruit.config.ExtractUser;
import com.example.eSmartRecruit.config.JwtService;
import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.*;
import com.example.eSmartRecruit.models.Application;
import com.example.eSmartRecruit.models.InterviewSession;
import com.example.eSmartRecruit.models.Position;
import com.example.eSmartRecruit.models.User;
import com.example.eSmartRecruit.exception.ApplicationException;
import com.example.eSmartRecruit.exception.InterviewSessionException;
import com.example.eSmartRecruit.exception.PositionException;
import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.models.*;
import com.example.eSmartRecruit.models.enumModel.*;
import com.example.eSmartRecruit.repositories.ApplicationRepos;
import com.example.eSmartRecruit.repositories.InterviewSessionRepos;
import com.example.eSmartRecruit.services.IStorageService;
import com.example.eSmartRecruit.services.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@ExtendWith(MockitoExtension.class)
@PrepareForTest({ExtractUser.class})
class AdminControllerTest {
    @InjectMocks
    private AdminController adminController;

    private JwtService jwtService;
    @Mock
    private IStorageService storageService;
    @Mock
    private UserService userService;

    @Mock
    private ApplicationService applicationService;
    @Mock
    private ApplicationRepos applicationRepository;

    @Mock
    private PositionService positionService;

    @Mock
    InterviewSessionService interviewSessionService;

    @Mock
    InterviewSessionRepos interviewSessionRepos;


    @Mock
    ReportService reportService;

    private HttpServletRequest mockRequest;


    @BeforeEach
    void setUp() throws UserException {

        User mockUser = new User();
        mockUser.setId(2);
        mockUser.setUsername("bcd");
        mockUser.setPassword("$2a$10$SgZX47bsE057V9z4n1NeG.y0hJkv1scG07pmPjPmBovIAnw4RhB7y");
        mockUser.setEmail("b123@gmail.com");
        mockUser.setPhoneNumber("0988888888");
        mockUser.setRoleName(Role.Admin);
        mockUser.setStatus(UserStatus.Inactive);
        mockUser.setCreateDate(Date.valueOf("2023-10-23"));
        mockUser.setUpdateDate(Date.valueOf("2023-10-23"));

        jwtService = new JwtService();
        jwtToken = jwtService.generateToken(mockUser);
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
    }

    private String jwtToken;

    @Test
    void getPosition_shouldThrowUserExceptionWhenAccountNotFound1() throws UserException {

        ResponseEntity<ResponseObject> responseEntity = adminController.getPositionAdmin(mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals("ERROR", responseObject.getStatus());
    }

    @Test
    void getPosition_shouldThrowUserExceptionWhenAccountNotFound() throws UserException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(false);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(false);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        ResponseEntity<ResponseObject> responseEntity = adminController.getPositionAdmin(mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals("Account is not active!", responseObject.getMessage());
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());

    }

    @Test
    void getPosition_shouldReturnErrorWhenPositionNotFound() throws PositionException {

        lenient().when(positionService.getAllPosition()).thenReturn(null);
        // Act
        ResponseEntity<ResponseObject> responseEntity = adminController.getPositionAdmin(mockRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
    }

    @Test
    void getPosition_shouldReturnSuccessWithPositionList() throws UserException, PositionException {
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        List<Position> mockPositions = new ArrayList<>();
        Position pos = new Position();
        pos.setId(1);
        pos.setTitle("Front-end Dev");
        pos.setJobDescription("abc");
        pos.setJobRequirements("abc");
        pos.setSalary(BigDecimal.valueOf(1000.00));
        pos.setPostDate(Date.valueOf("2023-10-26"));
        pos.setExpireDate(Date.valueOf("2023-10-26"));
        pos.setUpdateDate(null);
        pos.setLocation("fpt");
        mockPositions.add(pos);

        Position poss = new Position();
        poss.setId(2);
        poss.setTitle("Back-end Dev");
        poss.setJobDescription("bcd");
        poss.setJobRequirements("bcd");
        poss.setSalary(BigDecimal.valueOf(2000.00));
        poss.setPostDate(Date.valueOf("2023-10-25"));
        poss.setExpireDate(Date.valueOf("2023-10-25"));
        poss.setUpdateDate(null);
        poss.setLocation("fpt");
        mockPositions.add(poss);

        when(positionService.getAllPosition()).thenReturn(mockPositions);
        ResponseEntity<ResponseObject> responseEntity = adminController.getPositionAdmin(mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, responseObject.getMessage());
        assertEquals(mockPositions, responseObject.getData());
    }

    //
    @Test
    void getDetailPosition_shouldReturnSuccessWithPositionList() throws PositionException, UserException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        Position mockPosition = new Position();
        mockPosition.setId(1);
        mockPosition.setTitle("Front-end Dev");
        mockPosition.setJobDescription("abc");
        mockPosition.setJobRequirements("abc");
        mockPosition.setSalary(BigDecimal.valueOf(1000.00));
        mockPosition.setPostDate(Date.valueOf("2023-10-26"));
        mockPosition.setExpireDate(Date.valueOf("2023-10-26"));
        mockPosition.setUpdateDate(null);
        mockPosition.setLocation("fpt");

        when(positionService.getSelectedPosition(1)).thenReturn(mockPosition);
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailPositionAdmin(1, mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, responseObject.getMessage());
        Position returnedPosition = (Position) responseObject.getData();
        assertNotNull(returnedPosition);
        assertEquals(1, returnedPosition.getId());
        assertEquals("Front-end Dev", returnedPosition.getTitle());

        verify(positionService, times(1)).getSelectedPosition(1);
    }

    //
    @Test
    void getDetailPositionAdmin_shouldReturnErrorWhenPositionNotFound() throws PositionException, UserException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        lenient().when(positionService.getSelectedPosition(1)).thenReturn(null);
        // Act
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailPositionAdmin(1, mockRequest);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertNull(responseObject.getData());
    }

    //
//    @Test
//    void getDetailPosition_shouldReturnErrorWithPositionList() throws PositionException, UserException {
//
//        ExtractUser mockUserInfo = mock(ExtractUser.class);
//        lenient().when(mockUserInfo.isEnabled()).thenReturn(false);
//        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
//        lenient().when(userService.isEnabled(2)).thenReturn(false);
//        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
//        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
//
//        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailPositionAdmin(1, mockRequest);
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account is not active!", responseObject.getMessage());
//
//    }

    @Test
    void home_shouldReturnSuccessWithHomeData() throws UserException, JSONException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        when(userService.getcountUser()).thenReturn(Long.valueOf(10));
        when(positionService.getcountPosition()).thenReturn(Long.valueOf(5));
        when(applicationService.getcountApplication()).thenReturn(Long.valueOf(20));
        when(interviewSessionService.getCountInterview()).thenReturn(Long.valueOf(3));

        // Gọi hàm home()
        ResponseEntity<ResponseObject> response = adminController.home(mockRequest);
        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, response.getBody().getStatus());

        verify(userService).getcountUser();
        verify(positionService).getcountPosition();
        verify(applicationService).getcountApplication();
        verify(interviewSessionService).getCountInterview();
    }

    @Test
    void getUsers_shouldReturnSuccessWithUserList() throws JSONException, UserException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        List<User> mockUserList = new ArrayList<>();
        mockUserList.add(User.builder().id(3)
                .username("tien").password("Tien123")
                .email("tien123@gmail.com").phoneNumber(null)
                .roleName(Role.Candidate).status(UserStatus.Active)
                .createDate(Date.valueOf("2023-11-11"))
                .updateDate(Date.valueOf("2023-11-11")).build());
        mockUserList.add(User.builder().id(4)
                .username("a").password("a123")
                .email("a123@gmail.com").phoneNumber(null)
                .roleName(Role.Candidate).status(UserStatus.Active)
                .createDate(Date.valueOf("2023-11-11"))
                .updateDate(Date.valueOf("2023-11-11")).build());
        when(userService.getAllUser()).thenReturn(mockUserList);
        ResponseEntity<ResponseObject> response = adminController.getUsers(mockRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, response.getBody().getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, response.getBody().getMessage());

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().getData();
        assertEquals(mockUserList.size(), dataList.size());

        for (int i = 0; i < mockUserList.size(); i++) {
            User user = mockUserList.get(i);
            Map<String, Object> data = dataList.get(i);

            assertEquals(user.getId(), data.get("id"));
            assertEquals(user.getUsername(), data.get("username"));
            assertEquals(user.getEmail(), data.get("email"));
            assertEquals(user.getPhoneNumber(), data.get("phonenumber"));
            assertEquals(user.getRoleName(), data.get("rolename"));
            assertEquals(user.getStatus(), data.get("status"));
            assertEquals(user.getCreateDate(), data.get("create_date"));
            assertEquals(user.getUpdateDate(), data.get("update_date"));
        }
    }

    @Test
    void getUsers_shouldReturnInternalServerErrorResponseWhenCatchUserException() throws JSONException, UserException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        when(userService.getAllUser()).thenThrow(new UserException("Error getting users"));
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        ResponseEntity<ResponseObject> response = adminController.getUsers(mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
    }

    @Test
    void createUser_shouldReturnInternalServerErrorResponseWhenCatchUserException() throws JSONException, UserException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        lenient().when(userService.getAllUser()).thenThrow(new UserException(ResponseObject.LOAD_FAIL));
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        RegisterRequest registerRequest = new RegisterRequest();
        lenient().when(userService.saveUser(registerRequest)).thenThrow(new UserException(ResponseObject.LOAD_FAIL));

        ResponseEntity<ResponseObject> response = adminController.createUser(mockRequest, registerRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
        assertEquals(ResponseObject.LOAD_FAIL, response.getBody().getMessage());
    }

    //Finish testing createUser() function
    @Test
    void createUser_shouldReturnSuccess() throws UserException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        RegisterRequest registerRequest = new RegisterRequest();
        when(userService.saveUser(registerRequest)).thenReturn(ResponseObject.builder()
                .status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.USER_CREATED).build());

        ResponseEntity<ResponseObject> responseEntity = adminController.createUser(mockRequest, registerRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, responseEntity.getBody().getStatus());
        assertEquals(ResponseObject.USER_CREATED, responseEntity.getBody().getMessage());
    }

    @Test
    public void getDetailApplication_shouldReturnSuccess() throws Exception {

        // Mock user
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(6);
        lenient().when(userService.isEnabled(6)).thenReturn(true);
        lenient().when(userService.getUserRole(6)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        ///
        Integer applicationId = 1;
        Integer candidateId = 1;
        Integer positionId = 1;
        String candidateName = "John Doe";
        String positionTitle = "Software Engineer";
        // Mock ApplicationRepository
        Application mockApplication = new Application();
        mockApplication.setId(applicationId);
        mockApplication.setCandidateID(candidateId);
        mockApplication.setPositionID(positionId);
        mockApplication.setStatus(ApplicationStatus.valueOf("Pending"));
        mockApplication.setCv("MockCV");
        mockApplication.setCreateDate(Date.valueOf("2023-10-10"));
        mockApplication.setUpdateDate(Date.valueOf("2023-10-10"));
        lenient().when(applicationRepository.findById(1)).thenReturn(Optional.of(mockApplication));

        //
        User user = new User();
        user.setId(candidateId);
        user.setUsername(candidateName);

        Position position = new Position();
        position.setId(positionId);
        position.setTitle(positionTitle);

        // Mock getUserById(candidateId), PositionService
        lenient().when(userService.getUserById(candidateId)).thenReturn(user);
        lenient().when(positionService.getSelectedPosition(positionId)).thenReturn(position);
        lenient().when(userService.findById(user.getId())).thenReturn(user); //them moi
        //lenient().when(mockUser.getUsername()).thenReturn(user.getUsername());//them moi
        // Call the method
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailApplication(1, mockRequest);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseEntity.getBody().getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, responseEntity.getBody().getMessage());
    }

    @Test
    public void getDetailApplication_shouldReturnErrorWhenApplicationNotFound() throws Exception {

        // Mock user
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(6);
        lenient().when(userService.isEnabled(6)).thenReturn(true);
        lenient().when(userService.getUserRole(6)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        ///
        Integer applicationId = 1;
        Integer candidateId = 1;
        Integer positionId = 1;
        String candidateName = "John Doe";
        String positionTitle = "Software Engineer";

        // Mock ApplicationRepository
        Application mockApplication = new Application();
        mockApplication.setId(applicationId);
        mockApplication.setCandidateID(candidateId);
        mockApplication.setPositionID(positionId);
        mockApplication.setStatus(ApplicationStatus.valueOf("Pending"));
        mockApplication.setCv("MockCV");
        mockApplication.setCreateDate(Date.valueOf("2023-10-10"));

        lenient().when(applicationRepository.findById(2)).thenReturn(Optional.of(new Application()));

        //
        User user = new User();
        user.setId(candidateId);
        user.setUsername(candidateName);

        Position position = new Position();
        position.setId(positionId);
        position.setTitle(positionTitle);

        // Mock getUserById(candidateId), PositionService
        lenient().when(userService.getUserById(candidateId)).thenReturn(user);
        lenient().when(positionService.getSelectedPosition(positionId)).thenReturn(position);

        // Call the method
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailApplication(1, mockRequest);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.APPLICATION_NOT_FOUND, responseObject.getMessage());
    }

//    @Test
//    public void getDetailApplications_shouldReturnForbiddenWhenUserNotAdmin() throws Exception {
//
//        // Mock user
//        ExtractUser mockUserInfo = mock(ExtractUser.class);
//        lenient().when(mockUserInfo.isEnabled()).thenReturn(false);
//        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
//        lenient().when(userService.isEnabled(2)).thenReturn(false);
//        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
//        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
//        ///
//        Integer applicationId = 1;
//        Integer candidateId = 1;
//        Integer positionId = 1;
//        String candidateName = "John Doe";
//        String positionTitle = "Software Engineer";
//        // Mock ApplicationRepository
//        Application mockApplication = new Application();
//        mockApplication.setId(applicationId);
//        mockApplication.setCandidateID(candidateId);
//        mockApplication.setPositionID(positionId);
//        mockApplication.setStatus(ApplicationStatus.valueOf("Pending"));
//        mockApplication.setCv("MockCV");
//        mockApplication.setCreateDate(Date.valueOf("2023-10-10"));
//        lenient().when(applicationRepository.findById(1)).thenReturn(Optional.of(mockApplication));
//        //
//        User user = new User();
//        user.setId(candidateId);
//        user.setUsername(candidateName);
//
//        Position position = new Position();
//        position.setId(positionId);
//        position.setTitle(positionTitle);
//
//        // Mock getUserById(candidateId), PositionService
//        lenient().when(userService.getUserById(candidateId)).thenReturn(user);
//        lenient().when(positionService.getSelectedPosition(positionId)).thenReturn(position);
//
//        // Call the method
//        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailApplication(1, mockRequest);
//
//        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
//        assertNull(responseEntity.getBody());
//    }


    @Test
    public void getApplications_shouldReturnSuccessWithApplicationList() throws Exception {

        // Mock user
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        ///
        Integer applicationId = 1;
        Integer candidateId = 1;
        Integer positionId = 1;
        String candidateName = "John Doe";
        String positionTitle = "Software Engineer";
        // Mock ApplicationRepository
        List<Application> mockApplications = new ArrayList<>();
        Application application = new Application();
        application.setId(applicationId);
        application.setCandidateID(candidateId);
        application.setPositionID(positionId);
        application.setStatus(ApplicationStatus.valueOf("Pending"));
        application.setCv("MockCV");
        application.setCreateDate(Date.valueOf("2023-10-10"));
        mockApplications.add(application);

        lenient().when(applicationRepository.findAll()).thenReturn(mockApplications);

        //
        User user = new User();
        user.setId(candidateId);
        user.setUsername(candidateName);

        Position position = new Position();
        position.setId(positionId);
        position.setTitle(positionTitle);

        // Mock getUserById(candidateId), PositionService
        lenient().when(userService.getUserById(candidateId)).thenReturn(user);
        lenient().when(positionService.getSelectedPosition(positionId)).thenReturn(position);

        // Call the method
        ResponseEntity<ResponseObject> responseEntity = adminController.getApplications(mockRequest);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseEntity.getBody().getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, responseEntity.getBody().getMessage());
    }

    @Test
    public void getApplications_shouldReturnForbiddenWhenUserNotAdmin() throws Exception {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(false);
        lenient().when(mockUserInfo.getUserId()).thenReturn(6);
        lenient().when(userService.isEnabled(2)).thenReturn(false);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        ///
        Integer applicationId = 1;
        Integer candidateId = 1;
        Integer positionId = 1;
        String candidateName = "John Doe";
        String positionTitle = "Software Engineer";
        // Mock ApplicationRepository
        Application mockApplication = new Application();
        mockApplication.setId(applicationId);
        mockApplication.setCandidateID(candidateId);
        mockApplication.setPositionID(positionId);
        mockApplication.setStatus(ApplicationStatus.valueOf("Pending"));
        mockApplication.setCv("MockCV");
        mockApplication.setCreateDate(Date.valueOf("2023-10-10"));
        lenient().when(applicationRepository.findById(1)).thenReturn(Optional.of(mockApplication));
        User user = new User();
        user.setId(candidateId);
        user.setUsername(candidateName);

        Position position = new Position();
        position.setId(positionId);
        position.setTitle(positionTitle);

        // Mock getUserById(candidateId), PositionService
        lenient().when(userService.getUserById(candidateId)).thenReturn(user);
        lenient().when(positionService.getSelectedPosition(positionId)).thenReturn(position);
        // Call the method
        ResponseEntity<ResponseObject> responseEntity = adminController.getApplications(mockRequest);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("ERROR", responseEntity.getBody().getStatus());
        assertEquals("Account not active!", responseEntity.getBody().getMessage());
    }

    @Test
    void deletePosition_shouldReturnSuccessWhenPositionDeletedSuccessfully() throws JSONException, UserException, PositionException {


        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        Position mockPosition = new Position();
        mockPosition.setId(1);
        mockPosition.setTitle("Front-end Dev");
        mockPosition.setJobDescription("abc");
        mockPosition.setJobRequirements("abc");
        mockPosition.setSalary(BigDecimal.valueOf(1000.00));
        mockPosition.setPostDate(Date.valueOf("2023-10-26"));
        mockPosition.setExpireDate(Date.valueOf("2023-10-26"));
        mockPosition.setUpdateDate(null);
        mockPosition.setLocation("fpt");

        // Assuming positionID is 1
        when(positionService.getSelectedPosition(1)).thenReturn(mockPosition);
        doNothing().when(positionService).deletePosition(1); // Mocking delete success
        // Act
        ResponseEntity<ResponseObject> responseEntity = adminController.deletePosition(1, mockRequest);
        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.DELETED_SUCCESS, responseObject.getMessage());
    }

    @Test
    void deletePosition_shouldReturnErrorWhenPositionExceptionThrown() throws JSONException, UserException, PositionException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        Position mockPosition = new Position();
        mockPosition.setId(1);
        mockPosition.setTitle("Front-end Dev");
        mockPosition.setJobDescription("abc");
        mockPosition.setJobRequirements("abc");
        mockPosition.setSalary(BigDecimal.valueOf(1000.00));
        mockPosition.setPostDate(Date.valueOf("2023-10-26"));
        mockPosition.setExpireDate(Date.valueOf("2023-10-26"));
        mockPosition.setUpdateDate(null);
        mockPosition.setLocation("fpt");

        when(positionService.getSelectedPosition(1)).thenReturn(mockPosition);
        doThrow(new PositionException(ResponseObject.POSITION_NOT_FOUND)).when(positionService).deletePosition(1);

        ResponseEntity<ResponseObject> responseEntity = adminController.deletePosition(1, mockRequest);
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.DELETED_FAIL, responseObject.getMessage());
    }

    //Finish testing createUser() function
//    @Test
//    void deletePosition_shouldReturnBadRequestWhenAccountNotActive() throws JSONException, UserException, PositionException {
//
//
//        ExtractUser mockUserInfo = mock(ExtractUser.class);
//        lenient().when(mockUserInfo.isEnabled()).thenReturn(false);
//        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
//        lenient().when(userService.isEnabled(2)).thenReturn(false);
//        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
//
//        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
//        ResponseEntity<ResponseObject> responseEntity = adminController.deletePosition(1, mockRequest);
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

//    @Test
//    void editPosition_shouldReturnBadRequestWhenAccountNotActive() throws JSONException, UserException, PositionException {
//
//        ExtractUser mockUserInfo = mock(ExtractUser.class);
//        lenient().when(mockUserInfo.isEnabled()).thenReturn(false);
//        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
//        lenient().when(userService.isEnabled(2)).thenReturn(false);
//        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
//
//        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
//
//        Position poss = new Position();
//        poss.setId(2);
//        poss.setTitle("Back-end Dev");
//        poss.setJobDescription("bcd");
//        poss.setJobRequirements("bcd");
//        poss.setSalary(BigDecimal.valueOf(2000.00));
//        poss.setPostDate(Date.valueOf("2023-10-25"));
//        poss.setExpireDate(Date.valueOf("2023-10-25"));
//        poss.setUpdateDate(null);
//        poss.setLocation("fpt");
//
//        // Create a PositionRequest object directly in the test method
//        PositionRequest positionRequest = new PositionRequest();
//        positionRequest.setTitle(poss.getTitle());
//        positionRequest.setJobDescription(poss.getJobDescription());
//        positionRequest.setJobRequirements(poss.getJobRequirements());
//        positionRequest.setSalary(poss.getSalary());
//        positionRequest.setExpireDate(poss.getExpireDate());
//        positionRequest.setLocation(poss.getLocation());
//
//        // Add more attributes if needed
//
//        // Act
//        ResponseEntity<ResponseObject> responseEntity = adminController.editPosition(1, positionRequest, mockRequest);
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        ResponseObject responseObject = responseEntity.getBody();
//        assertNotNull(responseObject);
//        assertEquals("ERROR", responseObject.getStatus());
//        assertEquals("Account not active!", responseObject.getMessage());
//    }

    @Test
    void editPosition_shouldReturnSuccessWhenPositionEditedSuccessfully() throws JSONException, UserException, PositionException {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setTitle("Software Engineer");
        positionRequest.setJobDescription("Build web applications");
        positionRequest.setJobRequirements("3 years of experience");
        positionRequest.setSalary(BigDecimal.valueOf(5000.00));
        positionRequest.setExpireDate(Date.valueOf("2023-11-30"));
        positionRequest.setLocation("FPT");

        Position existingPosition = new Position();
        existingPosition.setTitle("Software Engineer");
        existingPosition.setJobDescription("Build web applications ");
        existingPosition.setJobRequirements("1 years of experience");
        existingPosition.setSalary(BigDecimal.valueOf(2000.00));
        existingPosition.setExpireDate(Date.valueOf("2023-10-30"));
        existingPosition.setLocation("FPT");

        // Mocking behaviors
        when(positionService.getSelectedPosition(1)).thenReturn(existingPosition);
        // Act
        ResponseEntity<ResponseObject> responseEntity = adminController.editPosition(1, positionRequest, mockRequest);
        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.UPDATED_SUCCESS, responseObject.getMessage());

        // Verify that the editPosition method is called once with the updated position
        verify(positionService, times(1)).editPosition(eq(1), any(Position.class));
    }

    @Test
    void listInterviewsession_shouldReturnInterviewSessionSuccessList() throws UserException, JSONException, InterviewSessionException {

        // Mock user
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(5);
        lenient().when(userService.isEnabled(5)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);


        List<InterviewSession> mockInterviewSessions = new ArrayList<>();
        InterviewSession interviewSession = new InterviewSession();
        interviewSession.setId(1);
        interviewSession.setInterviewerID(null);
        interviewSession.setApplicationID(1);
//        interviewSession.setPositionID(null);
//        interviewSession.setCandidateID(null);
        interviewSession.setDate(Date.valueOf("2023-10-29"));
        interviewSession.setLocation("fpt");
        interviewSession.setStatus(SessionStatus.valueOf("NotOnSchedule"));
        interviewSession.setLocation("NotYet");
        interviewSession.setNotes("abc");
        mockInterviewSessions.add(interviewSession);


        interviewSession.setId(2);
        interviewSession.setInterviewerID(3);
        interviewSession.setApplicationID(1);
//        interviewSession.setPositionID(null);
//        interviewSession.setCandidateID(null);
        interviewSession.setDate(Date.valueOf("2023-10-25"));
        interviewSession.setLocation("fpt");
        interviewSession.setStatus(SessionStatus.valueOf("Yet"));
        interviewSession.setLocation("Good");
        interviewSession.setNotes("bcd");
        mockInterviewSessions.add(interviewSession);


        when(interviewSessionService.getAllInterviewSession()).thenReturn(mockInterviewSessions);
        ResponseEntity<ResponseObject> responseEntity = adminController.listInterviewsession(mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, responseObject.getMessage());
        assertEquals(mockInterviewSessions, responseObject.getData());

    }

    @Test
    void listInterviewsession_shouldReturnErrorWhenInterviewSessionNotFound() throws UserException, JSONException, InterviewSessionException {

        lenient().when(interviewSessionService.getAllInterviewSession()).thenReturn(null);
        // Act
        ResponseEntity<ResponseObject> responseEntity = adminController.listInterviewsession(mockRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals("ERROR", responseObject.getStatus());

    }

    @Test
    void detailInterviewSessionId_success() throws UserException, InterviewSessionException {}
        //Start testing scheduleInterview() function
        @Test
        void scheduleInterview_shouldReturnSuccess () throws InterviewSessionException, InterviewSessionException {

            Integer sessionId = 1;

            InterviewSession interviewSession = new InterviewSession();
            interviewSession.setId(sessionId);
            interviewSession.setInterviewerID(1);
            interviewSession.setApplicationID(1);
            interviewSession.setDate(Date.valueOf("2023-11-11"));
            interviewSession.setLocation("FPT");
            interviewSession.setStatus(SessionStatus.Yet);
            interviewSession.setResult(SessionResult.NotYet);
            interviewSession.setNotes("Nothing");

            InterviewSession newInterviewSession = new InterviewSession();
            newInterviewSession.setId(sessionId);
            newInterviewSession.setInterviewerID(1);
            newInterviewSession.setApplicationID(1);
            newInterviewSession.setDate(Date.valueOf("2023-11-20"));
            newInterviewSession.setLocation("FPT");
            newInterviewSession.setStatus(SessionStatus.Yet);
            newInterviewSession.setResult(SessionResult.Good);
            newInterviewSession.setNotes("Nothing");


            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            InterviewSessionRequest interviewSessionRequest = new InterviewSessionRequest();
            interviewSessionRequest.setInterviewerId(1);
            interviewSessionRequest.setDate(Date.valueOf("2023-11-20"));
            interviewSessionRequest.setLocation("FPT");
            interviewSessionRequest.setNotes("Nothing");
            lenient().when(interviewSessionService.scheduleInterview(sessionId, interviewSessionRequest)).thenReturn(newInterviewSession);

            ResponseEntity<ResponseObject> response = adminController.scheduleInterview(sessionId, mockRequest, interviewSessionRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(ResponseObject.SUCCESS_STATUS, response.getBody().getStatus());
            assertEquals(ResponseObject.SCHEDULED, response.getBody().getMessage());
        }
    @Test
    void scheduleInterview_shouldReturnInternalServerError () throws
            InterviewSessionException, InterviewSessionException {
        Integer sessionId = 1;

        InterviewSession interviewSession = new InterviewSession();
        interviewSession.setId(sessionId);
        interviewSession.setInterviewerID(1);
        interviewSession.setApplicationID(1);
        interviewSession.setDate(Date.valueOf("2023-11-11"));
        interviewSession.setLocation("FPT");
        interviewSession.setStatus(SessionStatus.Yet);
        interviewSession.setResult(SessionResult.NotYet);
        interviewSession.setNotes("Nothing");

        InterviewSession newInterviewSession = new InterviewSession();
        newInterviewSession.setId(sessionId);
        newInterviewSession.setInterviewerID(1);
        newInterviewSession.setApplicationID(1);
        newInterviewSession.setDate(Date.valueOf("2023-11-20"));
        newInterviewSession.setLocation("FPT");
        newInterviewSession.setStatus(SessionStatus.Yet);
        newInterviewSession.setResult(SessionResult.Good);
        newInterviewSession.setNotes("Nothing");


        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        InterviewSessionRequest interviewSessionRequest = new InterviewSessionRequest();
        interviewSessionRequest.setInterviewerId(1);
        interviewSessionRequest.setDate(Date.valueOf("2023-11-20"));
        interviewSessionRequest.setLocation("FPT");
        interviewSessionRequest.setNotes("Nothing");
        lenient().when(interviewSessionService.scheduleInterview(sessionId, interviewSessionRequest)).thenThrow(InterviewSessionException.class);
        ResponseEntity<ResponseObject> response = adminController.scheduleInterview(sessionId, mockRequest, interviewSessionRequest);

        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
    }
    //Finish testing scheduleInterview() function

    //Start testing getReport() function
    @Test
    void getReport_shouldReturnSuccess () throws Exception {
        Integer sessionId = 1;
        Report report = new Report();
        report.setId(1);
        report.setSessionID(1);
        report.setReportName("Haha");
        report.setReportData("Ha");
        report.setCreateDate(Date.valueOf("2023-11-1"));
        report.setUpdateDate(Date.valueOf("2023-11-1"));
        lenient().when(reportService.getReportBySessionId(1)).thenReturn(report);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", report.getId());
        data.put("report_name", report.getReportName());
        data.put("report_data", report.getReportData());
        data.put("createDate", report.getCreateDate() != null ? report.getCreateDate().toString() : null);
        data.put("updateDate", report.getUpdateDate() != null ? report.getUpdateDate().toString() : null);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> response = adminController.getReport(sessionId, mockRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, response.getBody().getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, response.getBody().getMessage());
        assertEquals(data, response.getBody().getData());
    }
    @Test
    void getReport_shouldReturnNotFound () throws Exception {
        Integer sessionId = 1;
        Report report = new Report();
        report.setId(1);
        report.setSessionID(1);
        report.setReportName("Haha");
        report.setReportData("Ha");
        report.setCreateDate(Date.valueOf("2023-11-1"));
        report.setUpdateDate(Date.valueOf("2023-11-1"));
        lenient().when(reportService.getReportBySessionId(1)).thenReturn(null);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> response = adminController.getReport(sessionId, mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
        assertEquals(ResponseObject.REPORT_NOT_FOUND, response.getBody().getMessage());
    }
    @Test
    void getReport_shouldReturnInternalServerError () throws Exception {
        Integer sessionId = 1;
        Report report = new Report();
        report.setId(1);
        report.setSessionID(1);
        report.setReportName("Haha");
        report.setReportData("Ha");
        report.setCreateDate(Date.valueOf("2023-11-1"));
        report.setUpdateDate(Date.valueOf("2023-11-1"));
        lenient().when(reportService.getReportBySessionId(1)).thenThrow(ApplicationException.class);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> response = adminController.getReport(sessionId, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, response.getBody().getMessage());
    }
    //Finish testing getReport() function

    //Start testing getDetailUser() function
    @Test
    void getDetailUser_shouldReturnSuccess () throws JSONException, UserException {
        User mockUser = User.builder().id(1)
                .username("khang").password("khang123")
                .email("khang123@gmail.com").phoneNumber(null)
                .roleName(Role.Candidate).status(UserStatus.Active)
                .createDate(Date.valueOf("2023-11-15"))
                .updateDate(Date.valueOf("2023-11-15")).build();
        when(userService.getUserById(1)).thenReturn(mockUser);
        Map<String, String> data = new LinkedHashMap<>();
        data.put("id", mockUser.getId().toString());
        data.put("username", mockUser.getUsername());
        data.put("email", mockUser.getEmail());
        data.put("phonenumber", mockUser.getPhoneNumber());
        data.put("rolename", mockUser.getRoleName().name());
        data.put("status", mockUser.getStatus().toString());
        data.put("create_date", mockUser.getCreateDate().toString());
        data.put("update_date", mockUser.getUpdateDate().toString());
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailUser(1, mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, responseEntity.getBody().getStatus());
        assertEquals(data, responseEntity.getBody().getData());
    }
    @Test
    void getDetailUser_shouldReturnInternalServerError () throws JSONException, UserException {
        User mockUser = User.builder().id(1)
                .username("khang").password("khang123")
                .email("khang123@gmail.com").phoneNumber(null)
                .roleName(Role.Candidate).status(UserStatus.Active)
                .createDate(Date.valueOf("2023-11-15"))
                .updateDate(Date.valueOf("2023-11-15")).build();
        lenient().when(userService.getUserById(1)).thenThrow(UserException.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailUser(1, mockRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, responseEntity.getBody().getStatus());
    }
    //Finish testing getDetailUser() function

    //Start testing editUser()
    @Test
    void editUser_shouldReturnSuccess () throws UserException {
        User mockUser = User.builder().id(1)
                .username("khang").password("khang123")
                .email("khang123@gmail.com").phoneNumber(null)
                .roleName(Role.Candidate).status(UserStatus.Active)
                .createDate(Date.valueOf("2023-11-15"))
                .updateDate(Date.valueOf("2023-11-15")).build();
        EditUserRequest editUserRequest = new EditUserRequest();
        editUserRequest.setUsername("khang");
        editUserRequest.setPassword("khang123");
        editUserRequest.setEmail("khang123@gmail.com");
        editUserRequest.setPhonenumber("0991123333");
        editUserRequest.setRolename(String.valueOf(Role.Candidate));

        mockUser.setPhoneNumber(editUserRequest.getPhonenumber());
        Map<String, String> data = new LinkedHashMap<>();
        data.put("id", mockUser.getId().toString());
        data.put("username", mockUser.getUsername());
        data.put("email", mockUser.getEmail());
        data.put("phonenumber", mockUser.getPhoneNumber());
        data.put("rolename", mockUser.getRoleName().name());
        data.put("status", mockUser.getStatus().toString());
        data.put("create_date", mockUser.getCreateDate().toString());
        data.put("update_date", mockUser.getUpdateDate().toString());

        lenient().when(userService.editUser(1, editUserRequest)).thenReturn(mockUser);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> responseEntity = adminController.editUser(1, mockRequest, editUserRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, responseEntity.getBody().getStatus());
        assertEquals(ResponseObject.UPDATED_SUCCESS, responseEntity.getBody().getMessage());
        assertEquals(data, responseEntity.getBody().getData());
    }
    @Test
    void editUser_shouldReturnInternalServerError () throws UserException {
        User mockUser = User.builder().id(1)
                .username("khang").password("khang123")
                .email("khang123@gmail.com").phoneNumber(null)
                .roleName(Role.Candidate).status(UserStatus.Active)
                .createDate(Date.valueOf("2023-11-15"))
                .updateDate(Date.valueOf("2023-11-15")).build();
        EditUserRequest editUserRequest = new EditUserRequest();
        editUserRequest.setUsername("khang");
        editUserRequest.setPassword("khang123");
        editUserRequest.setEmail("khang123@gmail.com");
        editUserRequest.setPhonenumber("0991123333");
        editUserRequest.setRolename(String.valueOf(Role.Candidate));

        lenient().when(userService.editUser(1, editUserRequest)).thenThrow(UserException.class);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> responseEntity = adminController.editUser(1, mockRequest, editUserRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, responseEntity.getBody().getStatus());
    }
    //Finish testing editUser()

    // Start testing getCandidateInformation()
    @Test
    void getCandidateInformation_shouldReturnSuccess () throws ApplicationException, UserException, JSONException {
        User mockUser = User.builder().id(2)
                .username("khang").password("khang123")
                .email("khang123@gmail.com").phoneNumber(null)
                .roleName(Role.Candidate).status(UserStatus.Active)
                .createDate(Date.valueOf("2023-11-15"))
                .updateDate(Date.valueOf("2023-11-15")).build();
        lenient().when(userService.getUserById(2)).thenReturn(mockUser);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", mockUser.getUsername());
        data.put("email", mockUser.getEmail());
        data.put("phonenumber", mockUser.getPhoneNumber());
        data.put("roleName", mockUser.getRoleName().name());

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> response = adminController.getCandidateInformation(2, mockRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, response.getBody().getStatus());
        assertEquals(data, response.getBody().getData());
    }

    @Test
    void getCandidateInformation_shouldReturnInternalServerError () throws
            ApplicationException, UserException, JSONException {
        User mockUser = new User();
        mockUser.setId(2);
        mockUser.setUsername("bcd");
        mockUser.setPassword("$2a$10$SgZX47bsE057V9z4n1NeG.y0hJkv1scG07pmPjPmBovIAnw4RhB7y");
        mockUser.setEmail("b123@gmail.com");
        mockUser.setPhoneNumber("0988888888");
        mockUser.setRoleName(Role.Admin);
        mockUser.setStatus(UserStatus.Active);
        mockUser.setCreateDate(Date.valueOf("2023-10-23"));
        mockUser.setUpdateDate(Date.valueOf("2023-10-23"));
        lenient().when(userService.getUserById(2)).thenReturn(mockUser);
        //lenient().when(mockUser.getRoleName().equals(Role.Candidate)).thenReturn(false);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", mockUser.getUsername());
        data.put("email", mockUser.getEmail());
        data.put("phonenumber", mockUser.getPhoneNumber());
        data.put("roleName", mockUser.getRoleName().name());

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        ResponseEntity<ResponseObject> response = adminController.getCandidateInformation(2, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
        assertEquals(ResponseObject.NOT_CANDIDATE, response.getBody().getMessage());
    }
    //Finish testing getCandidateInformation()

    //Start testing updateApplicationStatus()
    @Test
    void updateApplicationStatus_shouldReturnSuccess () throws
            UserException, PositionException, ApplicationException {
        User mockUser = new User();

        ApplicationResultRequest resultRequest = new ApplicationResultRequest();
        resultRequest.setStatus(ApplicationStatus.Pending.name());

        Integer applicationId = 1;
        Application application = new Application();
        application.setId(applicationId);
        application.setCandidateID(1);
        application.setPositionID(1);
        application.setCv("cv");
        application.setCreateDate(Date.valueOf("2023-11-11"));
        application.setUpdateDate(Date.valueOf("2023-11-11"));

        lenient().when(applicationService.findById(1)).thenReturn(application);
        Position mockPosition = new Position();
        mockPosition.setTitle("Bao ve");
        lenient().when(positionService.getSelectedPosition(application.getPositionID())).thenReturn(mockPosition);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("applicationID", application.getId());
        data.put("positionTitle", positionService.getSelectedPosition(application.getPositionID()).getTitle());
        data.put("status", application.getStatus());

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        ResponseEntity<ResponseObject> response = adminController.updateApplicationStatus(1, mockRequest, resultRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, response.getBody().getStatus());
        assertEquals(data, response.getBody().getData());
    }

    @Test
    void updateApplicationStatus_shouldReturnErrorInvalidStatus () throws
            UserException, PositionException, ApplicationException {
        User mockUser = new User();

        ApplicationResultRequest resultRequest = new ApplicationResultRequest();
        resultRequest.setStatus("Hello");

        Integer applicationId = 1;
        Application application = new Application();
        application.setId(applicationId);
        application.setCandidateID(1);
        application.setPositionID(1);
        application.setCv("cv");
        application.setCreateDate(Date.valueOf("2023-11-11"));
        application.setUpdateDate(Date.valueOf("2023-11-11"));

        lenient().when(applicationService.findById(1)).thenReturn(application);
        Position mockPosition = new Position();
        mockPosition.setTitle("Bao ve");
        lenient().when(positionService.getSelectedPosition(application.getPositionID())).thenReturn(mockPosition);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("applicationID", application.getId());
        data.put("positionTitle", positionService.getSelectedPosition(application.getPositionID()).getTitle());
        data.put("status", application.getStatus());

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ResponseEntity<ResponseObject> response = adminController.updateApplicationStatus(1, mockRequest, resultRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
        assertEquals(ResponseObject.INVALID_STATUS, response.getBody().getMessage());
    }
    //Finish testing updateApplicationStatus()

    //Start testing getDetailInterviewSession()
    @Test
    void getDetailInterviewSession_success () throws UserException, InterviewSessionException {
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        InterviewSession mockInterviewSession = new InterviewSession();
        mockInterviewSession.setId(1);
        mockInterviewSession.setInterviewerID(1);
        mockInterviewSession.setApplicationID(1);
        mockInterviewSession.setDate(Date.valueOf("2023-10-29"));
        mockInterviewSession.setLocation("fpt");
        mockInterviewSession.setStatus(SessionStatus.valueOf("NotOnSchedule"));
        mockInterviewSession.setResult(SessionResult.valueOf("NotYet"));
        mockInterviewSession.setNotes("abc");

        lenient().when(interviewSessionService.findByID(1)).thenReturn(mockInterviewSession);
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailInterviewSession(1, mockRequest);


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
        assertEquals(ResponseObject.LOAD_SUCCESS, responseObject.getMessage());
    }

//        @Test
//        void detailInterviewSessionId_notFound () throws Exception {
//            assertEquals(ResponseObject.SUCCESS_STATUS, responseObject.getStatus());
//            assertEquals(ResponseObject.LOAD_SUCCESS, responseObject.getMessage());
//        }

    @Test
    void getDetailInterviewSession_notFound () throws Exception {
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        when(interviewSessionService.findByID(anyInt())).thenReturn(null);
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailInterviewSession(1, mockRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ResponseObject responseObject = responseEntity.getBody();
        assertNotNull(responseObject);
        assertEquals(ResponseObject.ERROR_STATUS, responseObject.getStatus());


    }

    @Test
    void getDetailInterviewSession_internalServerError () throws Exception {

        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(2);
        lenient().when(userService.isEnabled(2)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(interviewSessionService.findByID(anyInt())).thenThrow(new RuntimeException(ResponseObject.INTERNAL_SERVER_ERROR));
        ResponseEntity<ResponseObject> responseEntity = adminController.getDetailInterviewSession(1, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(ResponseObject.INTERNAL_SERVER_ERROR, responseEntity.getBody().getMessage());

    }
    //Finish testing getDetailInterviewSession()

    @Test
    void getEvaluate_shouldReturnSuccess() throws UserException, InterviewSessionException, JSONException {
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(4);
        lenient().when(userService.isEnabled(4)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);


        Integer interviewSessionId = 1;
        EvaluateRequest evaluateRequest = new EvaluateRequest();
        evaluateRequest.setResult("Good");

        InterviewSession mockInterviewSession = new InterviewSession();
        mockInterviewSession.setId(interviewSessionId);
        mockInterviewSession.setInterviewerID(null);
        mockInterviewSession.setApplicationID(1);
        mockInterviewSession.setDate(Date.valueOf("2023-10-29"));
        mockInterviewSession.setLocation("fpt");
        mockInterviewSession.setStatus(SessionStatus.valueOf("NotOnSchedule"));
        mockInterviewSession.setResult(SessionResult.Good);
        mockInterviewSession.setNotes("abc");
        when(interviewSessionService.findByID(interviewSessionId)).thenReturn(mockInterviewSession);

        ResponseEntity<ResponseObject> response = adminController.getEvaluate(interviewSessionId, evaluateRequest, mockRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseObject.SUCCESS_STATUS, response.getBody().getStatus());

        Map<String, Object> expectedData = new LinkedHashMap<>();
        expectedData.put("id", mockInterviewSession.getId());
        expectedData.put("interviewerID", mockInterviewSession.getInterviewerID());
        expectedData.put("applicationID", mockInterviewSession.getApplicationID());
        expectedData.put("date", mockInterviewSession.getDate());
        expectedData.put("location", mockInterviewSession.getLocation());
        expectedData.put("status", mockInterviewSession.getStatus());
        expectedData.put("result", mockInterviewSession.getResult());
        expectedData.put("notes", mockInterviewSession.getNotes());
        assertEquals(expectedData, response.getBody().getData());
    }
    @Test
    void getEvaluate_shouldReturnErrorInvalidStatus() throws UserException, InterviewSessionException, JSONException {
        ExtractUser mockUserInfo = mock(ExtractUser.class);
        lenient().when(mockUserInfo.isEnabled()).thenReturn(true);
        lenient().when(mockUserInfo.getUserId()).thenReturn(4);
        lenient().when(userService.isEnabled(4)).thenReturn(true);
        lenient().when(userService.getUserRole(2)).thenReturn("Admin");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        lenient().when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        Integer interviewSessionId = 1;
        EvaluateRequest evaluateRequest = new EvaluateRequest();
        evaluateRequest.setResult("InvalidStatus");

        ResponseEntity<ResponseObject> response = adminController.getEvaluate(interviewSessionId, evaluateRequest, mockRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ResponseObject.ERROR_STATUS, response.getBody().getStatus());
        //assertEquals("Invalid status", response.getBody().getMessage());
    }
}

