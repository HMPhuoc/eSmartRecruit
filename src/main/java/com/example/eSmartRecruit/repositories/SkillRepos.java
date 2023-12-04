package com.example.eSmartRecruit.repositories;

import com.example.eSmartRecruit.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepos extends JpaRepository<Skill, Integer>{

}
