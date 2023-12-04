USE eSmartRecruit;
INSERT INTO `esmartrecruit`.`users` (`ID`, `Username`, `Password`, `Email`, `PhoneNumber`, `RoleName`, `Status`, `CreateDate`, `UpdateDate`) VALUES ('1', 'abc', 'a123', 'a123@gmail.com', '0999999999', 'Candidate', 'Active', '2023-10-26', '2023-10-26');
INSERT INTO `esmartrecruit`.`users` (`ID`, `Username`, `Password`, `Email`, `PhoneNumber`, `RoleName`, `Status`, `CreateDate`, `UpdateDate`) VALUES ('2', 'bcd', 'b123', 'b123@gmail.com', '0988888888', 'Admin', 'Active', '2023-10-23', '2023-10-23');
INSERT INTO `esmartrecruit`.`users` (`ID`, `Username`, `Password`, `Email`, `PhoneNumber`, `RoleName`, `Status`, `CreateDate`, `UpdateDate`) VALUES ('3', 'cde', 'c123', 'c123@gmail.com', '0977777777', 'Interviewer', 'Active', '2023-10-24', '2023-10-24');

INSERT INTO `esmartrecruit`.`positions` (`ID`, `Title`, `JobDescription`, `JobRequirements`, `Salary`, `PostDate`, `ExpireDate`, `Location`) VALUES ('1', 'Front-end Dev', 'abc', 'abc', '1000', '2023-10-26', '2023-10-26', 'fpt');
INSERT INTO `esmartrecruit`.`positions` (`ID`, `Title`, `JobDescription`, `JobRequirements`, `Salary`, `PostDate`, `ExpireDate`, `Location`) VALUES ('2', 'Back-end Dev', 'bcd', 'bcd', '2000', '2023-10-25', '2023-10-25', 'fpt');

INSERT INTO `esmartrecruit`.`applications` (`ID`, `CandidateID`, `PositionID`, `Status`, `CV`, `CreateDate`, `UpdateDate`) VALUES ('1', '1', '1', 'Pending', 'abc', '2023-10-25', '2023-10-25');
INSERT INTO `esmartrecruit`.`applications` (`ID`, `CandidateID`, `PositionID`, `Status`, `CV`, `CreateDate`, `UpdateDate`) VALUES ('2', '1', '2', 'Pending', 'bcd', '2023-10-26', '2023-10-26');

INSERT INTO `esmartrecruit`.`blacklists` (`ID`, `CandidateID`, `Reason`, `CreateDate`, `UpdateDate`) VALUES ('1', '1', 'Late', '2023-10-25', '2023-10-25');
INSERT INTO `esmartrecruit`.`blacklists` (`ID`, `CandidateID`, `Reason`, `CreateDate`, `UpdateDate`) VALUES ('2', '1', 'Late', '2023-10-24', '2023-10-24');

INSERT INTO `esmartrecruit`.`skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('1', '1', 'Python');
INSERT INTO `esmartrecruit`.`skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('2', '1', 'Java');
INSERT INTO `esmartrecruit`.`skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('3', '1', 'RESTful API');
INSERT INTO `esmartrecruit`.`skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('4', '1', 'Docker');

INSERT INTO `esmartrecruit`.`interviewsessions` (`ID`, `PositionID`, `InterviewerID`, `CandidateID`, `Date`, `Location`, `Status`, `Result`, `Notes`) VALUES ('1', '1', NULL, '1', '2023-10-29', 'fpt', 'NotOnSchedule', 'NotYet', 'abc');
INSERT INTO `esmartrecruit`.`interviewsessions` (`ID`, `PositionID`, `InterviewerID`, `CandidateID`, `Date`, `Location`, `Status`, `Result`, `Notes`) VALUES ('2', '2', '3', '1', '2023-10-25', 'fpt', 'Yet', 'Good', 'bcd');

INSERT INTO `esmartrecruit`.`communications` (`ID`, `CandidateID`, `Notes`, `DateContacted`, `CreateDate`, `UpdateDate`) VALUES ('1', '1', 'New Dev Front-end', '2023-10-29', '2023-10-25', '2023-10-25');
INSERT INTO `esmartrecruit`.`communications` (`ID`, `CandidateID`, `Notes`, `DateContacted`, `CreateDate`, `UpdateDate`) VALUES ('2', '1', 'New Dev Back-end', '2023-10-30', '2023-10-24', '2023-10-24');

INSERT INTO `esmartrecruit`.`reports` (`ID`, `SessionID`, `ReportName`, `ReportData`, `CreateDate`, `UpdateDate`) VALUES ('1', '1', 'English skills', '9', '2023-10-25', '2023-10-25');
INSERT INTO `esmartrecruit`.`reports` (`ID`, `SessionID`, `ReportName`, `ReportData`, `CreateDate`, `UpdateDate`) VALUES ('2', '2', 'Programming skills', '10', '2023-10-24', '2023-10-24');