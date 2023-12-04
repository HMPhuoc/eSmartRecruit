CREATE DATABASE IF NOT EXISTS eSmartRecruit;
USE eSmartRecruit;

-- Tạo bảng Roles
Create table Roles(
	ID INT primary key,
    RoleName VARCHAR(20) UNIQUE NOT NULL);
    
INSERT INTO `eSmartRecruit`.`Roles`
(`ID`,`RoleName`)
VALUES
(0,"Candidate");
INSERT INTO `eSmartRecruit`.`Roles`
(`ID`,`RoleName`)
VALUES
(1, "Admin");
INSERT INTO `eSmartRecruit`.`Roles`
(`ID`,`RoleName`)
VALUES
(2, "Interviewer");

-- Tạo bảng Users
CREATE TABLE Users (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(255) UNIQUE,
    Password VARCHAR(255),
    Email VARCHAR(255) UNIQUE,
    PhoneNumber VARCHAR(20) UNIQUE,
    RoleName INT NOT NULL,
    Status ENUM('Active', 'Inactive') DEFAULT('Active'),
    CreateDate Date,
    UpdateDate Date,
    FOREIGN KEY (RoleName) REFERENCES Roles(ID)
);

-- Tạo bảng Vị trí tuyển dụng
CREATE TABLE Positions (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Title NVARCHAR(255),
    JobDescription TEXT,
    JobRequirements TEXT,
    Salary DECIMAL,
    PostDate DATE,
    ExpireDate DATE,
    UpdateDate DATE,
    Location VARCHAR(255)
);

-- Tạo bảng Hồ sơ ứng tuyển
CREATE TABLE Applications (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CandidateID INT,
    PositionID INT,
    Status ENUM('Pending', 'Approved', 'Declined') DEFAULT('Pending'),
    CV VARCHAR(255),
    CreateDate Date,
    UpdateDate Date,
    FOREIGN KEY (CandidateID) REFERENCES Users(ID),
    FOREIGN KEY (PositionID) REFERENCES Positions(ID)
);

-- Tạo bảng Kỹ năng của Ứng viên
CREATE TABLE Skills (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CandidateID INT,
    SkillName NVARCHAR(100),
    FOREIGN KEY (CandidateID) REFERENCES Users(ID)
);

-- Tạo bảng Phiên phỏng vấn
CREATE TABLE InterviewSessions (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    InterviewerID INT DEFAULT NULL,
    ApplicationID INT,
    Date DATE,
    Location NVARCHAR(255),
	Status ENUM('NotOnSchedule','Yet', 'Already') DEFAULT('NotOnSchedule'),
    Result ENUM('NotYet', 'Good', 'NotGood') DEFAULT('NotYet'),
    Notes TEXT,
    FOREIGN KEY (InterviewerID) REFERENCES Users(ID),
    FOREIGN KEY (ApplicationID) REFERENCES Applications(ID)
);

-- Tạo bảng Reports
CREATE TABLE Reports (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    SessionID INT,
    ReportName NVARCHAR(255),
    ReportData TEXT,
    CreateDate Date,
    UpdateDate Date,
    FOREIGN KEY (SessionID) REFERENCES InterviewSessions(ID)
);