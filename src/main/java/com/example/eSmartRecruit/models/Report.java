package com.example.eSmartRecruit.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;


@Builder
@Data
@Entity
@Table(name = "Reports")
public class Report {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "SessionID")
    private Integer sessionID;

    @Column(name = "ReportName", length = 255)
    private String reportName;

    @Lob
    @Column(name = "ReportData", columnDefinition = "text")
    private String reportData;

    @Temporal(TemporalType.DATE)
    @Column(name = "CreateDate")
    private Date createDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "UpdateDate")
    private Date updateDate;

    public Report() {}

	public Report(Integer sessionID, String reportName, String reportData, Date createDate, Date updateDate) {
		super();
		this.sessionID = sessionID;
		this.reportName = reportName;
		this.reportData = reportData;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public Report(Integer id, Integer sessionID, String reportName, String reportData, Date createDate, Date updateDate) {
		super();
		this.id = id;
		this.sessionID = sessionID;
		this.reportName = reportName;
		this.reportData = reportData;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSessionID() {
		return sessionID;
	}

	public void setSessionID(Integer sessionID) {
		this.sessionID = sessionID;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportData() {
		return reportData;
	}

	public void setReportData(String reportData) {
		this.reportData = reportData;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}