package com.example.eSmartRecruit.repositories;

import com.example.eSmartRecruit.models.InterviewSession;
import com.example.eSmartRecruit.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepos extends JpaRepository<InterviewSession, Integer>{
    List<InterviewSession> findByInterviewerID(Integer InterviewerID);
}
