package com.example.eSmartRecruit.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Skills")
public class Skill {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
    private Integer id;

    @Column(name = "CandidateID")
    private Integer candidateId;

    @Column(name = "SkillName")
    private String skillName;
    
    public Skill() {}

    public Skill(Integer candidateId, String skillName) {
		super();
		this.candidateId = candidateId;
		this.skillName = skillName;
	}
    
	public Skill(Integer id, Integer candidateId, String skillName) {
		super();
		this.id = id;
		this.candidateId = candidateId;
		this.skillName = skillName;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidate(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}