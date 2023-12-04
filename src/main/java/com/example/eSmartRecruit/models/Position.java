package com.example.eSmartRecruit.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "Positions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Position {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Title", length = 255)
    private String title;

    @Lob
    @Column(name = "JobDescription", columnDefinition = "text")
    private String jobDescription;

    @Lob
    @Column(name = "JobRequirements", columnDefinition = "text")
    private String jobRequirements;

    @Column(name = "Salary")
    private BigDecimal salary;

    @Column(name = "PostDate")
    private Date postDate;

    @Column(name = "ExpireDate")
    private Date expireDate;

	@Column(name = "UpdateDate")
	private Date updateDate;

    @Column(name = "Location", length = 255)
    private String location;


}
