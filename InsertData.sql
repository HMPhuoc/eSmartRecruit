USE eSmartRecruit;
-- Mật khẩu là khang123
INSERT INTO `eSmartRecruit`.`Users` (`ID`, `Username`, `Password`, `Email`, `PhoneNumber`, `RoleName`, `Status`, `CreateDate`, `UpdateDate`) VALUES ('1', 'abc', '$2a$10$S5x1eUGgsbXA4RJfrnc07ueCheYAVNMXsqw23/HfivFQJsaowrTXW', 'a123@gmail.com', '0999999999', 0, 'Active', '2023-10-26', '2023-10-26');
INSERT INTO `eSmartRecruit`.`Users` (`ID`, `Username`, `Password`, `Email`, `PhoneNumber`, `RoleName`, `Status`, `CreateDate`, `UpdateDate`) VALUES ('2', 'bcd', '$2a$10$S5x1eUGgsbXA4RJfrnc07ueCheYAVNMXsqw23/HfivFQJsaowrTXW', 'b123@gmail.com', '0988888888', 1, 'Active', '2023-10-23', '2023-10-23');
INSERT INTO `eSmartRecruit`.`Users` (`ID`, `Username`, `Password`, `Email`, `PhoneNumber`, `RoleName`, `Status`, `CreateDate`, `UpdateDate`) VALUES ('3', 'cde', '$2a$10$S5x1eUGgsbXA4RJfrnc07ueCheYAVNMXsqw23/HfivFQJsaowrTXW', 'c123@gmail.com', '0977777777', 2, 'Active', '2023-10-24', '2023-10-24');

INSERT INTO `eSmartRecruit`.`Positions` (`ID`, `Title`, `JobDescription`, `JobRequirements`, `Salary`, `PostDate`, `ExpireDate`, `Location`) VALUES ('1', 'Front-end Dev', 'abc', 'abc', '1000', '2023-10-26', '2024-10-26', 'fpt');
INSERT INTO `eSmartRecruit`.`Positions` (`ID`, `Title`, `JobDescription`, `JobRequirements`, `Salary`, `PostDate`, `ExpireDate`, `Location`) VALUES ('2', 'Back-end Dev', 'bcd', 'bcd', '2000', '2023-10-25', '2024-10-25', 'fpt');

INSERT INTO `eSmartRecruit`.`Applications` (`ID`, `CandidateID`, `PositionID`, `Status`, `CV`, `CreateDate`, `UpdateDate`) VALUES ('1', '1', '1', 'Pending', 'abc', '2023-10-25', '2023-10-25');
INSERT INTO `eSmartRecruit`.`Applications` (`ID`, `CandidateID`, `PositionID`, `Status`, `CV`, `CreateDate`, `UpdateDate`) VALUES ('2', '1', '2', 'Pending', 'bcd', '2023-10-26', '2023-10-26');

INSERT INTO `eSmartRecruit`.`Skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('1', '1', 'Python');
INSERT INTO `eSmartRecruit`.`Skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('2', '1', 'Java');
INSERT INTO `eSmartRecruit`.`Skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('3', '1', 'RESTful API');
INSERT INTO `eSmartRecruit`.`Skills` (`ID`, `CandidateID`, `SkillName`) VALUES ('4', '1', 'Docker');

INSERT INTO `eSmartRecruit`.`InterviewSessions` (`ID`, `InterviewerID`, `ApplicationID`, `Date`, `Location`, `Status`, `Result`, `Notes`) VALUES ('1', NULL, '1', '2023-10-29', 'fpt', 'NotOnSchedule', 'NotYet', 'abc');
INSERT INTO `eSmartRecruit`.`InterviewSessions` (`ID`, `InterviewerID`, `ApplicationID`, `Date`, `Location`, `Status`, `Result`, `Notes`) VALUES ('2', '3', '1', '2023-10-25', 'fpt', 'Yet', 'Good', 'bcd');

INSERT INTO `eSmartRecruit`.`Reports` (`ID`, `SessionID`, `ReportName`, `ReportData`, `CreateDate`, `UpdateDate`) VALUES ('1', '1', 'English skills', '9', '2023-10-25', '2023-10-25');
INSERT INTO `eSmartRecruit`.`Reports` (`ID`, `SessionID`, `ReportName`, `ReportData`, `CreateDate`, `UpdateDate`) VALUES ('2', '2', 'Programming skills', '10', '2023-10-24', '2023-10-24');