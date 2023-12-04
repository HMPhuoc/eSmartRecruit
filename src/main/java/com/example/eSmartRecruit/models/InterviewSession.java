package com.example.eSmartRecruit.models;

import com.example.eSmartRecruit.models.enumModel.SessionResult;
import com.example.eSmartRecruit.models.enumModel.SessionStatus;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Date;

@Entity
@Table(name = "InterviewSessions")
@AllArgsConstructor
@Data
@Builder
public class InterviewSession {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;


//    @Column(name = "PositionID")
//    private Integer positionID;

    @Column(name = "InterviewerID")
    private Integer interviewerID;

//    @Column(name = "CandidateID")
//    private Integer candidateID;
	@Column(name = "ApplicationID")
	private Integer ApplicationID;


    @Column(name = "Date")
    private Date date;

    @Column(name = "Location", length = 255)
    private String location;

    @Column(name = "Status")
	@Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Column(name = "Result")
	@Enumerated(EnumType.STRING)
    private SessionResult result;

    @Lob
    @Column(name = "Notes", columnDefinition = "text")
    private String notes;


    public InterviewSession() {}



//	public Integer getId() {
//		return id;
//	}
//
//
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//
//	public Integer getPositionID() {
//		return positionID;
//	}
//
//
//	public void setPositionID(Integer positionID) {
//		this.positionID = positionID;
//	}
//
//
//	public Integer getInterviewerID() {
//		return interviewerID;
//	}
//
//
//	public void setInterviewerID(Integer interviewerID) {
//		this.interviewerID = interviewerID;
//	}
//
//
//	public Integer getCandidateID() {
//		return candidateID;
//	}
//
//
//	public void setCandidateID(int candidateID) {
//		this.candidateID = candidateID;
//	}
//
//
//	public Date getDate() {
//		return date;
//	}
//
//
//	public void setDate(Date date) {
//		this.date = date;
//	}
//
//
//	public String getLocation() {
//		return location;
//	}
//
//
//	public void setLocation(String location) {
//		this.location = location;
//	}
//
//
//	public String getStatus() {
//		return status;
//	}
//
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//
//	public String getResult() {
//		return result;
//	}
//
//
//	public void setResult(String result) {
//		this.result = result;
//	}
//
//
//	public String getNotes() {
//		return notes;
//	}
//
//
//	public void setNotes(String notes) {
//		this.notes = notes;
//	}

}
