package com.example.eSmartRecruit.services.impl;

import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.InterviewSessionRequest;
import com.example.eSmartRecruit.exception.ApplicationException;
import com.example.eSmartRecruit.exception.InterviewSessionException;
import com.example.eSmartRecruit.exception.PositionException;
import com.example.eSmartRecruit.models.Application;
import com.example.eSmartRecruit.models.InterviewSession;
import com.example.eSmartRecruit.models.Position;

import com.example.eSmartRecruit.models.enumModel.ApplicationStatus;
import com.example.eSmartRecruit.models.enumModel.SessionResult;
import com.example.eSmartRecruit.models.enumModel.SessionStatus;
import com.example.eSmartRecruit.repositories.InterviewSessionRepos;
import com.example.eSmartRecruit.services.IInterviewSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InterviewSessionService implements IInterviewSessionService {
    private final InterviewSessionRepos interviewSessionRepos;
    public List<InterviewSession> findByInterviewerID(Integer userId) throws InterviewSessionException {
        return  interviewSessionRepos.findByInterviewerID(userId);
    }
    public  InterviewSession findByID(Integer ID) throws InterviewSessionException {
//        try {
//            Optional<InterviewSession> interviewSession = interviewSessionRepos.findById(ID);
//            if (interviewSession.isPresent()) {
//                return  interviewSession.orElseThrow(()->new InterviewSessionException("The required interview Session not found"))
//            } else return null;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return  interviewSessionRepos.findById(ID).orElseThrow(()->new InterviewSessionException(ResponseObject.NO_INTERVIEWSESSION));
    }

    public boolean isAlready(Integer interviewersessionID) throws InterviewSessionException {
        InterviewSession interviewSession = interviewSessionRepos.findById(interviewersessionID).orElseThrow(()->new InterviewSessionException(ResponseObject.POSITION_NOT_FOUND));
        if(interviewSession.getStatus() != SessionStatus.Already ){
            return false;
        }
        return true;
    }

    public Long getCountInterview() {
        return interviewSessionRepos.count();
    }
    public void save(InterviewSession interviewSession){
        interviewSessionRepos.save(interviewSession);
    }
    public InterviewSession scheduleInterview(int id, InterviewSessionRequest interviewSessionRequest) throws InterviewSessionException {
        InterviewSession interviewSession = findByID(id);
        interviewSession.setInterviewerID(interviewSessionRequest.getInterviewerId());
        interviewSession.setDate(interviewSessionRequest.getDate());
        interviewSession.setLocation(interviewSessionRequest.getLocation());
        interviewSession.setStatus(SessionStatus.Yet);
        interviewSession.setNotes(interviewSessionRequest.getNotes());
        save(interviewSession);
        return interviewSession;
    }

    public InterviewSession getSelectedInterviewSession(int id) throws InterviewSessionException {
        return interviewSessionRepos.findById(id).orElseThrow(()->new InterviewSessionException("The required interviewsession not found"));
    }
//    public String evaluate(Integer interviewsessionId, InterviewSession interviewSession) {
//        try {
//            InterviewSession evaluateInterview = interviewSessionRepos.findById(interviewsessionId)
//                    .orElseThrow(() -> new PositionException("The required position not found"));
//
//            evaluateInterview.setStatus(interviewSession.getStatus());
//            evaluateInterview.setResult(interviewSession.getResult());
//
//            return "Update infomation successfully";
//        } catch (PositionException e) {
//            return "Error updating information: " + e.getMessage();
//        }
//    }

    public List<InterviewSession> getAllInterviewSession() throws InterviewSessionException {
        return  interviewSessionRepos.findAll();
    }
    public String interviewUpdate(Integer id, SessionResult result){
        try{
            InterviewSession exInterviewSession = interviewSessionRepos.findById(id).orElseThrow(()->new InterviewSessionException("InterviewSession not found!"));
            // exInterviewSession.setStatus(status);
            exInterviewSession.setResult(result);
            interviewSessionRepos.save(exInterviewSession);

            return "Successfully evaluated!";
        }catch (Exception e){
            return e.toString();
        }
       // return null;
    }
}
