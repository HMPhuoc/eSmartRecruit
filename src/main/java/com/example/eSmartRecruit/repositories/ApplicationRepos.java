package com.example.eSmartRecruit.repositories;

import com.example.eSmartRecruit.models.Application;
import com.example.eSmartRecruit.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepos extends JpaRepository<Application, Integer>{
    List<Application> findByCandidateID(Integer candidateID);
    Optional<Application> findByCandidateIDAndPositionID(Integer candidateId, Integer positionId);
}
