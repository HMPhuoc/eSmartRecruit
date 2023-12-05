package com.example.eSmartRecruit.controllers.admin;

import com.example.eSmartRecruit.authentication.AuthenticationService;
import com.example.eSmartRecruit.authentication.request_reponse.RegisterRequest;
import com.example.eSmartRecruit.config.ExtractUser;
import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.*;
import com.example.eSmartRecruit.exception.InterviewSessionException;
import com.example.eSmartRecruit.exception.PositionException;
import com.example.eSmartRecruit.exception.UserException;

import com.example.eSmartRecruit.models.*;
import com.example.eSmartRecruit.models.enumModel.ApplicationStatus;
import com.example.eSmartRecruit.models.enumModel.Role;
import com.example.eSmartRecruit.models.enumModel.SessionResult;
import com.example.eSmartRecruit.models.enumModel.SessionStatus;
import com.example.eSmartRecruit.repositories.ApplicationRepos;
import com.example.eSmartRecruit.repositories.InterviewSessionRepos;
import com.example.eSmartRecruit.services.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jdk.jshell.spi.ExecutionControl;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("eSmartRecruit/admin")
public class AdminController {

    private UserService userService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationRepos applicationRepository;
    private InterviewSessionService interviewSessionService;
    private AuthenticationService authenticationService;
    private InterviewSessionRepos interviewSessionRepos;
    @Autowired
    private ReportService reportService;
    //private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/home")
    public ResponseEntity<ResponseObject> home(HttpServletRequest request) throws JSONException, UserException {

        String authHeader = request.getHeader("Authorization");
        //return new ResponseEntity<String>("hello",HttpStatus.OK);
        ExtractUser userInfo = new ExtractUser(authHeader, userService);

        Map<String, Object> homeList = new LinkedHashMap<>();
        homeList.put("no_user", userService.getcountUser());
        homeList.put("no_position", positionService.getcountPosition());
        homeList.put("no_application", applicationService.getcountApplication());
        homeList.put("no_interview_session", interviewSessionService.getCountInterview());

        return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(homeList).build(), HttpStatus.OK);


    }

    @PostMapping("/position/create")
    public ResponseEntity<ResponseObject> createPost(@RequestBody Position position, HttpServletRequest request) throws JSONException, UserException {

        String authHeader = request.getHeader("Authorization");
        //return new ResponseEntity<String>("hello",HttpStatus.OK);
        ExtractUser userInfo = new ExtractUser(authHeader, userService);

        System.out.println("đã chạy create post");
        Position createPosition = positionService.createPost(position);
        Map<String, Object> datapost = new LinkedHashMap<>();
        datapost.put("title", createPosition.getTitle());
        datapost.put("jobDescription", createPosition.getJobDescription());
        datapost.put("jobRequirements", createPosition.getJobRequirements());
        datapost.put("salary", createPosition.getSalary());
        datapost.put("expireDate", createPosition.getExpireDate());
        datapost.put("location", createPosition.getLocation());

        ResponseObject response = ResponseObject.builder()
                .status(ResponseObject.SUCCESS_STATUS)
                .message(ResponseObject.CREATED)
                .data(datapost)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/position")
    public ResponseEntity<ResponseObject> getPositionAdmin(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);
            Integer userId = userInfo.getUserId();
            User user = userService.getUserById(userId);
            if (!userInfo.isEnabled() || !userService.getUserRole(userId).toLowerCase().equalsIgnoreCase("admin")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder().status("ERROR").message("Account is not active!").build());
            }

            List<Position> data = positionService.getAllPosition();
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).data(data).message(ResponseObject.LOAD_SUCCESS).build(), HttpStatus.OK);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status("ERROR").message(exception.getMessage()).build());
        }
    }

    @GetMapping("/position/{positionID}")
    ResponseEntity<ResponseObject> getDetailPositionAdmin(@PathVariable("positionID") Integer id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);
            Integer userId = userInfo.getUserId();
            User user = userService.getUserById(userId);

            Position positions = positionService.getSelectedPosition(id);

            if (positions == null) {
                return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(ResponseObject.POSITION_NOT_FOUND).build(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(positions).build(), HttpStatus.OK);

        } catch (Exception exception) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(exception.getMessage()).build(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/application")
    public ResponseEntity<ResponseObject> getApplications(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);
            Integer userId = userInfo.getUserId();
            User user = userService.getUserById(userId);
            if (!userInfo.isEnabled() || !userService.getUserRole(userId).toLowerCase().equalsIgnoreCase("admin")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder().status("ERROR").message("Account not active!").build());
            }

            List<Application> applications = applicationRepository.findAll();
            List<Map<String, Object>> applicationList = new ArrayList<>();

            for (Application app : applications) {
                Map<String, Object> applicationMap = new LinkedHashMap<>();
                applicationMap.put("applicationID", app.getId());
                applicationMap.put("positionTitle", positionService.getSelectedPosition(app.getPositionID()).getTitle());
                applicationMap.put("status", app.getStatus());
                applicationMap.put("applicationDate", app.getCreateDate().toString());
                applicationList.add(applicationMap);
            }
            return ResponseEntity.ok(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(applicationList).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(ResponseObject.LOAD_FAIL).message(e.getMessage()).build());
        }
    }

    @GetMapping("/application/{applicationID}")
    public ResponseEntity<ResponseObject> getDetailApplication(@PathVariable("applicationID") Integer Id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);
            Integer userId = userInfo.getUserId();


            Optional<Application> application = applicationRepository.findById(Id);
            if (application.isPresent()) {
                Application app = application.get();
                Map<String, Object> data = new LinkedHashMap<>();

                User user = userService.findById(app.getCandidateID());

                data.put("applicationID", app.getId());
                data.put("candidateName", user.getUsername());
                data.put("positionTitle", positionService.getSelectedPosition(app.getPositionID()).getTitle());
                data.put("status", app.getStatus());
                data.put("cv", app.getCv());
                data.put("applicationDate", app.getCreateDate().toString());

                return ResponseEntity.ok(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(data).build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.APPLICATION_NOT_FOUND).build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(ResponseObject.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PutMapping("/position/{positionID}")
    public ResponseEntity<ResponseObject> editPosition(@PathVariable Integer positionID, @RequestBody PositionRequest positionRequest, HttpServletRequest request) throws JSONException, UserException, PositionException {
        String authHeader = request.getHeader("Authorization");
        ExtractUser userInfo = new ExtractUser(authHeader, userService);

        try {
            Position existingPosition = positionService.getSelectedPosition(positionID);
            Position updatedPosition = Position.builder()
                    .title(positionRequest.getTitle())
                    .jobDescription(positionRequest.getJobDescription())
                    .jobRequirements(positionRequest.getJobRequirements())
                    .salary(positionRequest.getSalary())
                    .expireDate(positionRequest.getExpireDate())
                    .location(positionRequest.getLocation())
                    .build();
            positionService.editPosition(positionID, updatedPosition);

            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.UPDATED_SUCCESS).build(), HttpStatus.OK);
        } catch (PositionException e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(ResponseObject.UPDATED_FAIL).status(ResponseObject.ERROR_STATUS).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/position/{positionID}")
    public ResponseEntity<ResponseObject> deletePosition(@PathVariable Integer positionID, HttpServletRequest request) throws JSONException, UserException, PositionException {
        String authHeader = request.getHeader("Authorization");
        ExtractUser userInfo = new ExtractUser(authHeader, userService);

        Position existingPosition = positionService.getSelectedPosition(positionID);

        try {
            positionService.deletePosition(positionID);
        } catch (PositionException e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(ResponseObject.DELETED_FAIL).status(ResponseObject.ERROR_STATUS).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                .status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.DELETED_SUCCESS).build(), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseObject> getUsers(HttpServletRequest request) throws JSONException {
        try {
            String authHeader = request.getHeader("Authorization");
            //logger.info("Received request to get users. Authorization header: {}", authHeader);

            ExtractUser userInfo = new ExtractUser(authHeader, userService);
            Integer userId = userInfo.getUserId();

            List<User> userList = userService.getAllUser();
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (User user : userList) {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("id", user.getId());
                data.put("username", user.getUsername());
                data.put("email", user.getEmail());
                data.put("phonenumber", user.getPhoneNumber());
                data.put("rolename", user.getRoleName());
                data.put("status", user.getStatus());
                data.put("create_date", user.getCreateDate());
                data.put("update_date", user.getUpdateDate());
                dataList.add(data);
            }

            //logger.info("Returning user list. Total users: {}", userList.size());
            return ResponseEntity.ok(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(dataList).build());
        } catch (UserException e) {
            //logger.error("Internal Server Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(e.getMessage()).build());
        }
    }

    @PostMapping("/user/create")
    ResponseEntity<ResponseObject> createUser(HttpServletRequest request,
                                              @RequestBody @Valid RegisterRequest registerRequest) {
        try {
            String authHeader = request.getHeader("Authorization");
            //logger.info("Received request to get users. Authorization header: {}", authHeader);

            ExtractUser userInfo = new ExtractUser(authHeader, userService);
            Integer userId = userInfo.getUserId();

            return ResponseEntity.ok(userService.saveUser(registerRequest));
        } catch (UserException | JSONException e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(ResponseObject.ERROR_STATUS).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/report/{interviewsessionid}")
    public ResponseEntity<ResponseObject> getReport(@PathVariable("interviewsessionid") Integer sessionId, HttpServletRequest request) {
        try {

            Report reportObject = reportService.getReportBySessionId(sessionId); // Sử dụng sessionId thay vì userId
            if (reportObject != null) {

                Map<String, Object> data = new LinkedHashMap<>();
                data.put("id", reportObject.getId());
                data.put("report_name", reportObject.getReportName());
                data.put("report_data", reportObject.getReportData());
                data.put("createDate", reportObject.getCreateDate() != null ? reportObject.getCreateDate().toString() : null);
                data.put("updateDate", reportObject.getUpdateDate() != null ? reportObject.getUpdateDate().toString() : null);

                return ResponseEntity.ok(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(data).build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(ResponseObject.REPORT_NOT_FOUND).build());
            }
        } catch (Exception e) {
            //logger.error("Error in getReport", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(ResponseObject.INTERNAL_SERVER_ERROR).build());
        }
    }

    @GetMapping("/candidate/{candidateId}")
    ResponseEntity<ResponseObject> getCandidateInformation(@PathVariable("candidateId") Integer candidateId, HttpServletRequest request) throws JSONException, UserException {
        try {
            User user = userService.getUserById(candidateId);
            if (!user.getRoleName().equals(Role.Candidate)) {
                throw new UserException("Not a candidate");
            }
            Map<String, String> data = new LinkedHashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());
            data.put("roleName", user.getRoleName().name());

            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(data).build(), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(exception.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/interviewsession/{interviewsessionid}")
    public ResponseEntity<ResponseObject> scheduleInterview(@PathVariable("interviewsessionid") Integer id, HttpServletRequest request,
                                                            @RequestBody @Valid InterviewSessionRequest interviewSessionRequest) {
        try {

            interviewSessionService.scheduleInterview(id, interviewSessionRequest);

            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(ResponseObject.SCHEDULED).status(ResponseObject.SUCCESS_STATUS).data(interviewSessionService.scheduleInterview(id, interviewSessionRequest)).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().message(e.getMessage()).status(ResponseObject.ERROR_STATUS).build(), HttpStatus.NOT_IMPLEMENTED);
        }
    }

    @GetMapping("/user/{userId}")
    ResponseEntity<ResponseObject> getDetailUser(@PathVariable("userId") Integer userId, HttpServletRequest request) throws JSONException, UserException {
        try {

            User user = userService.getUserById(userId);
            Map<String, String> data = new LinkedHashMap<>();
            data.put("id", user.getId().toString());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());
            data.put("rolename", user.getRoleName().name());
            data.put("status", user.getStatus().toString());
            data.put("create_date", user.getCreateDate().toString());
            data.put("update_date", user.getUpdateDate().toString());
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.UPDATED_SUCCESS).data(data).build(), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(exception.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user/{userId}")
    ResponseEntity<ResponseObject> editUser(@PathVariable("userId") Integer userId, HttpServletRequest request,
                                            @RequestBody @Valid EditUserRequest editUserRequest) {
        try {


            User user = userService.editUser(userId, editUserRequest);
            Map<String, String> data = new LinkedHashMap<>();
            data.put("id", user.getId().toString());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());
            data.put("rolename", user.getRoleName().name());
            data.put("status", user.getStatus().toString());
            data.put("create_date", user.getCreateDate().toString());
            data.put("update_date", user.getUpdateDate().toString());
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.UPDATED_SUCCESS).data(data).build(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PutMapping("/application/{applicationID}")
    public ResponseEntity<ResponseObject> updateApplicationStatus(@PathVariable("applicationID") Integer id, HttpServletRequest request, @RequestBody ApplicationResultRequest applicationResultRequest) {
        try {
            try {
                ApplicationStatus.valueOf(applicationResultRequest.getStatus());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                        .message(ResponseObject.INVALID_STATUS).status(ResponseObject.ERROR_STATUS).build(), HttpStatus.BAD_REQUEST);
            }
            ApplicationStatus status1 = ApplicationStatus.valueOf(applicationResultRequest.getStatus());
            String message = applicationService.adminUpdate(id, status1);
            Application application = applicationService.findById(id);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("applicationID", application.getId());
            data.put("positionTitle", positionService.getSelectedPosition(application.getPositionID()).getTitle());
            data.put("status", application.getStatus());
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(message).status(ResponseObject.SUCCESS_STATUS).data(data).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().message(e.getMessage()).status(ResponseObject.ERROR_STATUS).build(), HttpStatus.NOT_IMPLEMENTED);
        }
    }

    @GetMapping("/interviewsession")
    public ResponseEntity<ResponseObject> listInterviewsession (HttpServletRequest request) throws JSONException, UserException{
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);
            Integer userId = userInfo.getUserId();

            if (!userInfo.isEnabled() || !userService.getUserRole(userId).toLowerCase().equalsIgnoreCase("admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<InterviewSession> interviewSessions = interviewSessionService.getAllInterviewSession();
            List<Map<String, Object>> listIntervewsession = new ArrayList<>();

            for (InterviewSession interview : interviewSessions) {
                Map<String, Object> list = new LinkedHashMap<>();
                list.put("id", interview.getId());
                list.put("interviewid", interview.getInterviewerID());
                list.put("applicationid", interview.getApplicationID());
                list.put("date", interview.getDate());
                list.put("location", interview.getLocation());
                list.put("status", interview.getStatus());
                list.put("result", interview.getResult());
                list.put("notes", interview.getNotes());
                listIntervewsession.add(list);
            }
            return ResponseEntity.ok(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(interviewSessions).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(e.getMessage()).build());
        }
    }
    @GetMapping("/interviewsession/{interviewsessionID}")
    public ResponseEntity<ResponseObject> getDetailInterviewSession(@PathVariable("interviewsessionID") Integer Id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);


            InterviewSession interviewSession = interviewSessionService.findByID(Id);
            if (interviewSession == null) {
                return new ResponseEntity<ResponseObject>(ResponseObject.builder().status("ERROR").message("InterviewSession not found").build(), HttpStatus.NOT_FOUND);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", interviewSession.getId());
            data.put("interviewid", interviewSession.getInterviewerID());
            data.put("applicationid", interviewSession.getApplicationID());
            data.put("date", interviewSession.getDate());
            data.put("location", interviewSession.getLocation());
            data.put("status", interviewSession.getStatus());
            data.put("result", interviewSession.getResult());
            data.put("notes", interviewSession.getNotes());
            return ResponseEntity.ok(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.LOAD_SUCCESS).data(data).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(ResponseObject.INTERNAL_SERVER_ERROR).build());
        }
    }


    @PutMapping("/interviewsession/evaluate/{interviewsessionid}")
    public ResponseEntity<ResponseObject> getEvaluate(@PathVariable Integer interviewsessionid, @RequestBody @Valid EvaluateRequest evaluateRequest, HttpServletRequest request) throws JSONException, UserException, InterviewSessionException
    {
        String authHeader = request.getHeader("Authorization");
        ExtractUser userInfo = new ExtractUser(authHeader, userService);

        try {
            SessionResult.valueOf(evaluateRequest.getResult());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ResponseObject.builder()
                    .message(e.getMessage()).status(ResponseObject.ERROR_STATUS).build(), HttpStatus.BAD_REQUEST);
        }
        SessionResult result = SessionResult.valueOf(evaluateRequest.getResult());
        String message = interviewSessionService.interviewUpdate(interviewsessionid, result);
        InterviewSession interviewSession = interviewSessionService.findByID(interviewsessionid);
        interviewSession.setResult(result);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", interviewSession.getId());
        data.put("interviewerID", interviewSession.getInterviewerID());
        data.put("applicationID", interviewSession.getApplicationID()   );
        data.put("date", interviewSession.getDate());
        data.put("location", interviewSession.getLocation());
        data.put("status", interviewSession.getStatus());
        data.put("result", interviewSession.getResult());
        data.put("notes", interviewSession.getNotes());

        return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                .message(ResponseObject.EVALUATED).status(ResponseObject.SUCCESS_STATUS).data(data).build(), HttpStatus.OK);
    }


    @GetMapping("/profile")
    public ResponseEntity<ResponseObject> getProfile(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);

            Integer userId = userInfo.getUserId();
            User user = userService.getUserById(userId);

            Map<String, String> data = new LinkedHashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());

            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS)
                    .message(ResponseObject.LOAD_SUCCESS).data(data).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS)
                    .message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseObject> updateProfile(HttpServletRequest request,
                                              @RequestBody @Valid UserRequest user0) {
        try {
            String authHeader = request.getHeader("Authorization");
            //return new ResponseEntity<String>("hello",HttpStatus.OK);
            ExtractUser userInfo = new ExtractUser(authHeader, userService);

            Integer userId = userInfo.getUserId();
            User user = userService.updateUser(user0, userId);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("email", user.getEmail());
            data.put("phoneNumber", user.getPhoneNumber());

            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS)
                    .message(ResponseObject.UPDATED_SUCCESS).data(data).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS)
                    .message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

