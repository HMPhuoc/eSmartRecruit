package com.example.eSmartRecruit.models;

import com.example.eSmartRecruit.models.enumModel.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "Applications")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Application {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CandidateID")
    private Integer candidateID;

    @Column(name = "PositionID")
	private Integer positionID;

    @Column(name = "Status")
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status;

    @Column(name = "CV", length = 255)
	private String cv;

    @Temporal(TemporalType.DATE)
    @Column(name = "CreateDate")
    private Date createDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "UpdateDate")
    private Date updateDate;

   public Application(String cv) {
	   this.cv = cv;
   }


	public Application(Integer candidateID, Integer positionID, String cv, Date updateDate) {
		super();
		this.candidateID = candidateID;
		this.positionID = positionID;
		this.cv = cv;
		this.createDate = Date.valueOf(LocalDate.now());
		this.updateDate = updateDate;
	}


	//To create new
	public Application(Integer candidateID, Integer positionID, String cv) {
		super();
		this.candidateID = candidateID;
		this.positionID = positionID;
		this.status = ApplicationStatus.Pending;
		this.cv = cv;
		this.createDate = Date.valueOf(LocalDate.now());
		this.updateDate = Date.valueOf(LocalDate.now());
	}


	public Application(Integer candidateID, Integer positionID, ApplicationStatus status, String cv, Date createDate, Date updateDate) {
		super();
		this.candidateID = candidateID;
		this.positionID = positionID;
		this.status = status;
		this.cv = cv;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

//	public Application(Integer id, Integer candidateID, Integer positionID, String status, String cv, Date createDate,
//			Date updateDate) {
//		super();
//		this.id = id;
//		this.candidateID = candidateID;
//		this.positionID = positionID;
//		this.status = status;
//		this.cv = cv;
//		this.createDate = createDate;
//		this.updateDate = updateDate;
//	}

//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//	public Integer getCandidateID() {
//		return candidateID;
//	}
//
//	public void setCandidateID(Integer candidateID) {
//		this.candidateID = candidateID;
//	}
//
//	public Integer getPositionID() {
//		return positionID;
//	}
//
//	public void setPositionID(Integer positionID) {
//		this.positionID = positionID;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public String getCv() {
//		return cv;
//	}
//
//	public void setCv(String cv) {
//		this.cv = cv;
//	}
//
//	public Date getCreateDate() {
//		return createDate;
//	}
//
//	public void setCreateDate(Date createDate) {
//		this.createDate = createDate;
//	}
//
//	public Date getUpdateDate() {
//		return updateDate;
//	}
//
//	public void setUpdateDate(Date updateDate) {
//		this.updateDate = updateDate;
//	}
}
