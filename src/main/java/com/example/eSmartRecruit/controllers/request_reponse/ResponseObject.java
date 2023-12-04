package com.example.eSmartRecruit.controllers.request_reponse;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({ "status", "message", "data" })
public class ResponseObject {
    //status
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String ERROR_STATUS = "ERROR";

    //msg
    public static final String REGISTER_SUCCESS = "Register successfully!";
    public static final String SIGN_IN_SUCCESS = "Sign in successfully!";
    public static final String LOGOUT_SUCCESS = "Logout successfully!";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String APPLICATION_NOT_FOUND = "Application not found!";
    public static final String CV_NOT_FOUND = "CV is required!Not found!";
    public static final String POSITION_NOT_FOUND = "Position not found";
    public static final String REPORT_NOT_FOUND = "Report not found!";
    public static final String UPDATED_SUCCESS = "Updated Successfully";
    public static final String UPDATED_FAIL = "Updated Fail";
    public static final String NOT_ACTIVE = "Not Active";
    public static final String LOAD_SUCCESS = "Loading Successfully";
    public static final String LOAD_FAIL = "Load fail!";
    public static final String ACCESS_SUCCESS = "Access Success";
    public static final String SERVER_ERROR = "Server Error";
    public static final String DELETED_SUCCESS = "Deleted Success";
    public static final String DELETED_FAIL = "Deleted Fail";
    public static final String LIST_SUCCESS = "List Position Successfully";
    public static final String NOT_OPEN = "Not Open";
    public static final String SEARCH_SUCCESS = "Search Successfully";
    public static final String INTERVIEW_SESSION = "Interview Session not already done";
    public static final String NOT_CANDIDATE = "Not a candidate";
    public static final String INTERNAL_SERVER_ERROR = "Internal service error";
    public static final String APPLICATION_EXISTED = "You have already applied for this position!";
    public static final String APPLY_SUCCESS = "Successfully applied!";
    public static final String NOT_YOUR_APPLICATION = "This is not your application!";
    public static final String APPROVE = "Approve application successfully!";
    public static final String DECLINE = "Decline application successfully!";
    public static final String SCHEDULED = "Schedule successfully!";
    public static final String USER_CREATED = "Create user successfully!";
    public static final String NO_INTERVIEWSESSION = "No interview sessions found!";
    public static final String INVALID_STATUS = "Invalid Status";
    public static final String CREATED = "Successfully created!";
    public static final String EVALUATED = "Successfully evaluated!";

    private String status;
    private String message;
    private Object data;
}