package com.example.eSmartRecruit.controllers.interviewer;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import com.example.eSmartRecruit.config.ExtractUser;
import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.ReportRequest;
import com.example.eSmartRecruit.controllers.request_reponse.request.UserRequest;
import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.models.Application;
import com.example.eSmartRecruit.models.InterviewSession;
import com.example.eSmartRecruit.models.Report;
import com.example.eSmartRecruit.models.User;
import com.example.eSmartRecruit.models.enumModel.Role;
import com.example.eSmartRecruit.services.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.*;

@AllArgsConstructor
@RestController
@RequestMapping("eSmartRecruit/interviewer")
public class InterviewerController {
    private UserService userService;
    private InterviewSessionService interviewSessionService;
    private ReportService reportService;
    private ApplicationService applicationService;
    private PositionService positionService;

    @GetMapping("/home")
    ResponseEntity<ResponseObject> getInterviewerSession(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);

            Integer interviewerId = userInfo.getUserId();
            List<InterviewSession> interviewSessionList = interviewSessionService.findByInterviewerID(interviewerId);

            // trường hợp danh sách phiên phỏng vấn trống
            if (interviewSessionList.isEmpty()) {
                return new ResponseEntity<>(ResponseObject.builder()
                        .message(ResponseObject.NO_INTERVIEWSESSION).status(ResponseObject.SUCCESS_STATUS)
                        .data(Collections.emptyList()).build(), HttpStatus.OK);
            }
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(ResponseObject.LOAD_SUCCESS).status(ResponseObject.SUCCESS_STATUS)
                    .data(interviewSessionList).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(ResponseObject.ERROR_STATUS).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/interview-session/{interview-sessionID}")
    ResponseEntity<ResponseObject> findByInterviewSessionID(@PathVariable("interview-sessionID") Integer interviewersessionID, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);

            InterviewSession interviewSession = interviewSessionService.findByID(interviewersessionID);
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(ResponseObject.LOAD_SUCCESS).status(ResponseObject.SUCCESS_STATUS)
                    .data(interviewSession).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(ResponseObject.ERROR_STATUS).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/report/create/{interview-sessionID}")
    ResponseEntity<ResponseObject> reportInterviewSession(@PathVariable("interview-sessionID") Integer interviewsessionID,
                                                          HttpServletRequest request, @RequestBody @Valid ReportRequest reportRequest) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);


            if (!interviewSessionService.isAlready(interviewsessionID)) {
                return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                        .message(ResponseObject.INTERVIEW_SESSION)
                        .status(ResponseObject.ERROR_STATUS).build(), HttpStatus.BAD_REQUEST);
            }

            Report report = Report.builder()
                    .reportName(reportRequest.getReportName())
                    .reportData(reportRequest.getReportData())
                    .sessionID(interviewsessionID)
                    .createDate(Date.valueOf(LocalDate.now()))
                    .updateDate(Date.valueOf(LocalDate.now())).build();
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .status(ResponseObject.SUCCESS_STATUS)
                    .message(reportService.reportInterviewSession(report)).build(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(ResponseObject.ERROR_STATUS).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ResponseObject> getApplicationDetails(@PathVariable("applicationId") Integer id, HttpServletRequest request) {
        try {
            Application application = applicationService.getApplicationById(id);
            User user = userService.getUserById(application.getCandidateID());

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("applicationID", application.getId());
            data.put("candidateName", user.getUsername());
            data.put("positionTitle", positionService.getSelectedPosition(application.getPositionID()).getTitle());
            data.put("status", application.getStatus());
            data.put("cv", application.getCv());
            data.put("applicationDate", application.getCreateDate().toString());

            return ResponseEntity.ok(ResponseObject.builder()
                    .message(ResponseObject.LOAD_SUCCESS)
                    .status(ResponseObject.SUCCESS_STATUS).data(data).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message(e.getMessage()).status(ResponseObject.ERROR_STATUS).build());
        }
    }

    @GetMapping("/candidate/{candidateId}")
    ResponseEntity<ResponseObject> getCandidateInformation(@PathVariable("candidateId") Integer candidateId, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);

            User candidate = userService.getUserById(candidateId);
            if (!candidate.getRoleName().equals(Role.Candidate)) {
                return new ResponseEntity<>(ResponseObject.builder()
                        .message(ResponseObject.NOT_CANDIDATE)
                        .status(ResponseObject.ERROR_STATUS).build(), HttpStatus.BAD_REQUEST);
            }

            Map<String, String> data = new HashMap<>();
            data.put("username", candidate.getUsername());
            data.put("email", candidate.getEmail());
            data.put("phonenumber", candidate.getPhoneNumber());

            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(ResponseObject.LOAD_SUCCESS)
                    .status(ResponseObject.SUCCESS_STATUS).data(data).build(), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS)
                    .message(exception.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //get userInterviewer info
    @GetMapping("/profile")
    ResponseEntity<ResponseObject> getDetailUserInterviewer(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);

            Integer userId = userInfo.getUserId();
            User user = userService.getUserById(userId);

            Map<String, String> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phonenumber", user.getPhoneNumber());


            return new ResponseEntity<>(ResponseObject.builder()
                    .message(ResponseObject.LOAD_SUCCESS)
                    .status(ResponseObject.SUCCESS_STATUS).data(data).build(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS)
                    .message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //update userInterviewer
    @PutMapping("/profile")
    ResponseEntity<ResponseObject> updateUserInterviewer(HttpServletRequest request,
                                                         @RequestBody @Valid UserRequest user0) {
        try {
            String authHeader = request.getHeader("Authorization");
            ExtractUser userInfo = new ExtractUser(authHeader, userService);

            Integer userId = userInfo.getUserId();
            User user = userService.updateUser(user0, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phoneNumber", user.getPhoneNumber());
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .message(ResponseObject.UPDATED_SUCCESS)
                    .status(ResponseObject.SUCCESS_STATUS).data(data).build(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder()
                    .status(ResponseObject.ERROR_STATUS).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}