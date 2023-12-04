package com.example.eSmartRecruit.repositories;

import com.example.eSmartRecruit.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepos extends JpaRepository<Report, Integer>{

    Report findBySessionID(Integer id);
}
