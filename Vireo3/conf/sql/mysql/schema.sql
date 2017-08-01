-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 04, 2015 at 12:11 PM
-- Server version: 5.5.41-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `vireo`
--

-- --------------------------------------------------------

--
-- Table structure for table `actionlog`
--

CREATE TABLE IF NOT EXISTS `actionlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `actionDate` datetime DEFAULT NULL,
  `entry` longtext NOT NULL,
  `privateFlag` tinyint(1) NOT NULL,
  `submissionState` varchar(255) NOT NULL,
  `attachment_id` bigint(20) DEFAULT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_9ud0hhk9umt2gcffwi4ipxhrb` (`attachment_id`),
  KEY `FK_4norsqa704bntket913ub5bpx` (`person_id`),
  KEY `FK_ahsjgamx6y6m5g7cgr8xnkjq6` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `administrative_groups`
--

CREATE TABLE IF NOT EXISTS `administrative_groups` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `emails` tinyblob,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g0arithcbc2y7mqw06l9f53yv` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `attachment`
--

CREATE TABLE IF NOT EXISTS `attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` varchar(255) DEFAULT NULL,
  `date` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ruw2i1tqm58ytj13n6o8n49r6` (`submission_id`,`name`),
  KEY `FK_8se0vn1c9y2lviiun4a877q7h` (`person_id`),
  KEY `FK_xrwsik7ddccorroc9m86mtmy` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `college`
--

CREATE TABLE IF NOT EXISTS `college` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `emails` tinyblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nyc2rxbj71rdhcw055436agb5` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `committee_member`
--

CREATE TABLE IF NOT EXISTS `committee_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `middleName` varchar(255) DEFAULT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_4eqjy8xt9agdgnkygu1jhib6b` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `committee_member_roles`
--

CREATE TABLE IF NOT EXISTS `committee_member_roles` (
  `JpaCommitteeMemberImpl_id` bigint(20) NOT NULL,
  `roles` varchar(255) DEFAULT NULL,
  `roles_ORDER` int(11) NOT NULL,
  PRIMARY KEY (`JpaCommitteeMemberImpl_id`,`roles_ORDER`),
  KEY `FK_lhstcjubx3ok7jsd4sur6dpmu` (`JpaCommitteeMemberImpl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `committee_member_role_type`
--

CREATE TABLE IF NOT EXISTS `committee_member_role_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_f8fg81yj2rftvrw9drj6t32c5` (`name`,`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `configuration`
--

CREATE TABLE IF NOT EXISTS `configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c3pprgpekt3nw1vmv98sqjqce` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `custom_action_definition`
--

CREATE TABLE IF NOT EXISTS `custom_action_definition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `label` varchar(255) NOT NULL,
  `isStudentVisible` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_vws65okmwb4j72n41daph650` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `custom_action_value`
--

CREATE TABLE IF NOT EXISTS `custom_action_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` tinyint(1) NOT NULL,
  `definition_id` bigint(20) NOT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_sfhufnuq27bp4rvau3p418gej` (`submission_id`,`definition_id`),
  KEY `FK_3brcld4o5axxxtpqvtx5jlhty` (`definition_id`),
  KEY `FK_784g0grh0a7si8baguih2eu93` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `degree`
--

CREATE TABLE IF NOT EXISTS `degree` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gbfqltywj1fafp78jeloxu0ef` (`name`,`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

CREATE TABLE IF NOT EXISTS `department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `emails` tinyblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1t68827l97cwyxo9r1u6t4p7d` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `deposit_location`
--

CREATE TABLE IF NOT EXISTS `deposit_location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `collection` longtext,
  `depositor` varchar(255) DEFAULT NULL,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `onBehalfOf` varchar(255) DEFAULT NULL,
  `packager` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `repository` longtext,
  `username` varchar(255) DEFAULT NULL,
  `timeout` int(11) DEFAULT '60',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dovfybuq9gk43yqlfept9xc42` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `document_type`
--

CREATE TABLE IF NOT EXISTS `document_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gu9uufujcobcnd1g0auqi6sxo` (`name`,`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `email_template`
--

CREATE TABLE IF NOT EXISTS `email_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `message` longtext NOT NULL,
  `name` varchar(255) NOT NULL,
  `subject` longtext NOT NULL,
  `systemRequired` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2qlliow884c9ci671eliwiydu` (`name`,`systemRequired`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `email_workflow_rules`
--

CREATE TABLE IF NOT EXISTS `email_workflow_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `associatedState` varchar(255) DEFAULT NULL,
  `displayOrder` int(11) NOT NULL,
  `isDisabled` tinyint(1) DEFAULT NULL,
  `isSystem` tinyint(1) DEFAULT NULL,
  `recipientType` int(11) DEFAULT NULL,
  `adminGroupRecipientId` bigint(20) DEFAULT NULL,
  `conditionID` bigint(20) DEFAULT NULL,
  `emailTemplateId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_5it9i77coc0sut7nt4ivl5taj` (`adminGroupRecipientId`),
  KEY `FK_bs7ru7e7sy0824bei7ldbyhcv` (`conditionID`),
  KEY `FK_282jvq0q662qfl3c8g09pprim` (`emailTemplateId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `email_workflow_rule_conditions`
--

CREATE TABLE IF NOT EXISTS `email_workflow_rule_conditions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `conditionId` bigint(20) DEFAULT NULL,
  `conditionType` int(11) DEFAULT NULL,
  `displayOrder` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `embargo_type`
--

CREATE TABLE IF NOT EXISTS `embargo_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` tinyint(1) NOT NULL,
  `description` longtext NOT NULL,
  `displayOrder` int(11) NOT NULL,
  `duration` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `guarantor` int(11) NOT NULL DEFAULT '0',
  `systemRequired` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_rrrhycsns6ohf9uvxrs5i61jk` (`name`,`guarantor`,`systemRequired`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `graduation_month`
--

CREATE TABLE IF NOT EXISTS `graduation_month` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `month` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dv0ctus1ai651v35jvpdyhuyx` (`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `language`
--

CREATE TABLE IF NOT EXISTS `language` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g8hr207ijpxlwu10pewyo65gv` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `major`
--

CREATE TABLE IF NOT EXISTS `major` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_oi0ctjbjvktdcfxws9w2exiwb` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `person`
--

CREATE TABLE IF NOT EXISTS `person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `birthYear` int(11) DEFAULT NULL,
  `currentCollege` varchar(255) DEFAULT NULL,
  `currentDegree` varchar(255) DEFAULT NULL,
  `currentDepartment` varchar(255) DEFAULT NULL,
  `currentEmailAddress` varchar(255) DEFAULT NULL,
  `currentGraduationMonth` int(11) DEFAULT NULL,
  `currentGraduationYear` int(11) DEFAULT NULL,
  `currentMajor` varchar(255) DEFAULT NULL,
  `currentPhoneNumber` varchar(255) DEFAULT NULL,
  `currentPostalAddress` varchar(255) DEFAULT NULL,
  `currentProgram` varchar(255) DEFAULT NULL,
  `displayName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `institutionalIdentifier` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `middleName` varchar(255) DEFAULT NULL,
  `netid` varchar(255) DEFAULT NULL,
  `orcid` varchar(255) DEFAULT NULL,
  `passwordHash` varchar(255) DEFAULT NULL,
  `permanentEmailAddress` varchar(255) DEFAULT NULL,
  `permanentPhoneNumber` varchar(255) DEFAULT NULL,
  `permanentPostalAddress` varchar(255) DEFAULT NULL,
  `role` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fwmwi44u55bo4rvwsv0cln012` (`email`),
  UNIQUE KEY `UK_7lave14pgltnfmvs342s9qco8` (`netid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `person_affiliations`
--

CREATE TABLE IF NOT EXISTS `person_affiliations` (
  `JpaPersonImpl_id` bigint(20) NOT NULL,
  `affiliations` varchar(255) DEFAULT NULL,
  KEY `FK_2u4d124bh89lkfo4o5gysf0do` (`JpaPersonImpl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `preference`
--

CREATE TABLE IF NOT EXISTS `preference` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` longtext,
  `person_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kpjn6eigbd3yxtc9dbr4ytpyp` (`person_id`,`name`),
  KEY `FK_h918mhr9wiqqvqbjcgxga8sj2` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `program`
--

CREATE TABLE IF NOT EXISTS `program` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `emails` tinyblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ha1ojetw3fv9tfdrrvfy99yuf` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter`
--

CREATE TABLE IF NOT EXISTS `search_filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `publicFlag` tinyint(1) NOT NULL,
  `rangeEnd` date DEFAULT NULL,
  `rangeStart` date DEFAULT NULL,
  `umiRelease` tinyint(1) DEFAULT NULL,
  `creator_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hcac87nf5end9s93gfb1lygyp` (`creator_id`,`name`),
  KEY `FK_3j48541vurjef5am8q0rnipmk` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_assignees`
--

CREATE TABLE IF NOT EXISTS `search_filter_assignees` (
  `search_filter_id` bigint(20) NOT NULL,
  `assigneeIds` bigint(20) DEFAULT NULL,
  KEY `FK_b1r4dxhoyq31751qlo822cav5` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_colleges`
--

CREATE TABLE IF NOT EXISTS `search_filter_colleges` (
  `search_filter_id` bigint(20) NOT NULL,
  `colleges` varchar(255) DEFAULT NULL,
  KEY `FK_cys8fjpqxkvcyde4beis0n3lx` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_columns`
--

CREATE TABLE IF NOT EXISTS `search_filter_columns` (
  `search_filter_id` bigint(20) NOT NULL,
  `columns` int(11) DEFAULT NULL,
  KEY `FK_k6e868fvh8225894xuyplaxri` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_customactions`
--

CREATE TABLE IF NOT EXISTS `search_filter_customactions` (
  `search_filter_id` bigint(20) NOT NULL,
  `customActionIds` bigint(20) DEFAULT NULL,
  KEY `FK_82hut8geqdkp89x0uyl3n6iub` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_degrees`
--

CREATE TABLE IF NOT EXISTS `search_filter_degrees` (
  `search_filter_id` bigint(20) NOT NULL,
  `degrees` varchar(255) DEFAULT NULL,
  KEY `FK_3rkguuxiipf4dcn38yt6nryj4` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_departments`
--

CREATE TABLE IF NOT EXISTS `search_filter_departments` (
  `search_filter_id` bigint(20) NOT NULL,
  `departments` varchar(255) DEFAULT NULL,
  KEY `FK_7vinqqsd2j0kidubm1ofpyc87` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_documenttypes`
--

CREATE TABLE IF NOT EXISTS `search_filter_documenttypes` (
  `search_filter_id` bigint(20) NOT NULL,
  `documentTypes` varchar(255) DEFAULT NULL,
  KEY `FK_ji793qo35mmcehjs1y8y5luy6` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_embargos`
--

CREATE TABLE IF NOT EXISTS `search_filter_embargos` (
  `search_filter_id` bigint(20) NOT NULL,
  `embargoIds` bigint(20) DEFAULT NULL,
  KEY `FK_mf5xk637mx8l8scb9m1iu2hod` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_excluded_actionlogs`
--

CREATE TABLE IF NOT EXISTS `search_filter_excluded_actionlogs` (
  `search_filter_id` bigint(20) NOT NULL,
  `excludedActionLogIds` bigint(20) DEFAULT NULL,
  KEY `FK_5o8iokop2rxq2ciq162j0xca0` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_excluded_submissions`
--

CREATE TABLE IF NOT EXISTS `search_filter_excluded_submissions` (
  `search_filter_id` bigint(20) NOT NULL,
  `excludedSubmissionIds` bigint(20) DEFAULT NULL,
  KEY `FK_7s000trks1u9ir2nn1vsida74` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_included_actionlogs`
--

CREATE TABLE IF NOT EXISTS `search_filter_included_actionlogs` (
  `search_filter_id` bigint(20) NOT NULL,
  `includedActionLogIds` bigint(20) DEFAULT NULL,
  KEY `FK_mg0crds89wdjejct5ly9nub5o` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_included_submissions`
--

CREATE TABLE IF NOT EXISTS `search_filter_included_submissions` (
  `search_filter_id` bigint(20) NOT NULL,
  `includedSubmissionIds` bigint(20) DEFAULT NULL,
  KEY `FK_15qdawr540viu5in5mem02w62` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_majors`
--

CREATE TABLE IF NOT EXISTS `search_filter_majors` (
  `search_filter_id` bigint(20) NOT NULL,
  `majors` varchar(255) DEFAULT NULL,
  KEY `FK_3wwtaloot4o2yv8ue7jg8cyo0` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_programs`
--

CREATE TABLE IF NOT EXISTS `search_filter_programs` (
  `search_filter_id` bigint(20) NOT NULL,
  `programs` varchar(255) DEFAULT NULL,
  KEY `FK_x3e9uwkkebwkloegbyfayceh` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_semesters`
--

CREATE TABLE IF NOT EXISTS `search_filter_semesters` (
  `search_filter_id` bigint(20) NOT NULL,
  `semesters` varchar(255) DEFAULT NULL,
  KEY `FK_miqf877rp21ethhtf3mep607r` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_states`
--

CREATE TABLE IF NOT EXISTS `search_filter_states` (
  `search_filter_id` bigint(20) NOT NULL,
  `states` varchar(255) DEFAULT NULL,
  KEY `FK_85a9ru0277s4n4y19iqt2veo7` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `search_filter_text`
--

CREATE TABLE IF NOT EXISTS `search_filter_text` (
  `search_filter_id` bigint(20) NOT NULL,
  `searchText` varchar(255) DEFAULT NULL,
  KEY `FK_5csq0a20x6lkhhddxl1mxflum` (`search_filter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `submission`
--

CREATE TABLE IF NOT EXISTS `submission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `UMIRelease` tinyint(1) DEFAULT NULL,
  `approvalDate` datetime DEFAULT NULL,
  `college` varchar(255) DEFAULT NULL,
  `committeeApprovalDate` datetime DEFAULT NULL,
  `committeeContactEmail` varchar(255) DEFAULT NULL,
  `committeeEmailHash` varchar(255) DEFAULT NULL,
  `committeeEmbargoApprovalDate` datetime DEFAULT NULL,
  `defenseDate` datetime DEFAULT NULL,
  `degree` varchar(255) DEFAULT NULL,
  `degreeLevel` int(11) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `depositDate` datetime DEFAULT NULL,
  `depositId` longtext,
  `documentAbstract` longtext,
  `documentKeywords` longtext,
  `documentLanguage` varchar(255) DEFAULT NULL,
  `documentTitle` longtext,
  `documentType` varchar(255) DEFAULT NULL,
  `graduationMonth` int(11) DEFAULT NULL,
  `graduationYear` int(11) DEFAULT NULL,
  `lastActionLogDate` datetime DEFAULT NULL,
  `lastActionLogEntry` longtext,
  `licenseAgreementDate` datetime DEFAULT NULL,
  `major` varchar(255) DEFAULT NULL,
  `orcid` varchar(255) DEFAULT NULL,
  `program` varchar(255) DEFAULT NULL,
  `publishedMaterial` longtext,
  `reviewerNotes` longtext,
  `stateName` varchar(255) DEFAULT NULL,
  `studentBirthYear` int(11) DEFAULT NULL,
  `studentFirstName` varchar(255) DEFAULT NULL,
  `studentLastName` varchar(255) DEFAULT NULL,
  `studentMiddleName` varchar(255) DEFAULT NULL,
  `submissionDate` datetime DEFAULT NULL,
  `assignee_id` bigint(20) DEFAULT NULL,
  `embargoType_id` bigint(20) DEFAULT NULL,
  `submitter_id` bigint(20) NOT NULL,
  `collegeId` bigint(20) DEFAULT NULL,
  `departmentId` bigint(20) DEFAULT NULL,
  `programId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_k7sos9qjyyos23e4hw2c5gqgh` (`committeeEmailHash`),
  KEY `FK_bsnfsllpukyx681rjx6t7kw0q` (`assignee_id`),
  KEY `FK_q1frkfciq469vm81v1n44y6ev` (`embargoType_id`),
  KEY `FK_5tcephlarb35bwrijkqdrg0uu` (`submitter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `submission_embargotypes`
--

CREATE TABLE IF NOT EXISTS `submission_embargotypes` (
  `submission_id` bigint(20) NOT NULL,
  `embargoTypeIds` bigint(20) DEFAULT NULL,
  KEY `FK_b3h9qu258ulj5cu8crm175d5i` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `submission_subjects`
--

CREATE TABLE IF NOT EXISTS `submission_subjects` (
  `JpaSubmissionImpl_id` bigint(20) NOT NULL,
  `documentSubjects` varchar(255) DEFAULT NULL,
  `documentSubjects_ORDER` int(11) NOT NULL,
  PRIMARY KEY (`JpaSubmissionImpl_id`,`documentSubjects_ORDER`),
  KEY `FK_rexg1ed7ej7dng9e9aq6whvn3` (`JpaSubmissionImpl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `actionlog`
--
ALTER TABLE `actionlog`
  ADD CONSTRAINT `FK_ahsjgamx6y6m5g7cgr8xnkjq6` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`),
  ADD CONSTRAINT `FK_4norsqa704bntket913ub5bpx` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`),
  ADD CONSTRAINT `FK_9ud0hhk9umt2gcffwi4ipxhrb` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`);

--
-- Constraints for table `attachment`
--
ALTER TABLE `attachment`
  ADD CONSTRAINT `FK_xrwsik7ddccorroc9m86mtmy` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`),
  ADD CONSTRAINT `FK_8se0vn1c9y2lviiun4a877q7h` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`);

--
-- Constraints for table `committee_member`
--
ALTER TABLE `committee_member`
  ADD CONSTRAINT `FK_4eqjy8xt9agdgnkygu1jhib6b` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`);

--
-- Constraints for table `committee_member_roles`
--
ALTER TABLE `committee_member_roles`
  ADD CONSTRAINT `FK_lhstcjubx3ok7jsd4sur6dpmu` FOREIGN KEY (`JpaCommitteeMemberImpl_id`) REFERENCES `committee_member` (`id`);

--
-- Constraints for table `custom_action_value`
--
ALTER TABLE `custom_action_value`
  ADD CONSTRAINT `FK_784g0grh0a7si8baguih2eu93` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`),
  ADD CONSTRAINT `FK_3brcld4o5axxxtpqvtx5jlhty` FOREIGN KEY (`definition_id`) REFERENCES `custom_action_definition` (`id`);

--
-- Constraints for table `email_workflow_rules`
--
ALTER TABLE `email_workflow_rules`
  ADD CONSTRAINT `FK_282jvq0q662qfl3c8g09pprim` FOREIGN KEY (`emailTemplateId`) REFERENCES `email_template` (`id`),
  ADD CONSTRAINT `FK_5it9i77coc0sut7nt4ivl5taj` FOREIGN KEY (`adminGroupRecipientId`) REFERENCES `administrative_groups` (`id`),
  ADD CONSTRAINT `FK_bs7ru7e7sy0824bei7ldbyhcv` FOREIGN KEY (`conditionID`) REFERENCES `email_workflow_rule_conditions` (`id`);

--
-- Constraints for table `person_affiliations`
--
ALTER TABLE `person_affiliations`
  ADD CONSTRAINT `FK_2u4d124bh89lkfo4o5gysf0do` FOREIGN KEY (`JpaPersonImpl_id`) REFERENCES `person` (`id`);

--
-- Constraints for table `preference`
--
ALTER TABLE `preference`
  ADD CONSTRAINT `FK_h918mhr9wiqqvqbjcgxga8sj2` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`);

--
-- Constraints for table `search_filter`
--
ALTER TABLE `search_filter`
  ADD CONSTRAINT `FK_3j48541vurjef5am8q0rnipmk` FOREIGN KEY (`creator_id`) REFERENCES `person` (`id`);

--
-- Constraints for table `search_filter_assignees`
--
ALTER TABLE `search_filter_assignees`
  ADD CONSTRAINT `FK_b1r4dxhoyq31751qlo822cav5` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_colleges`
--
ALTER TABLE `search_filter_colleges`
  ADD CONSTRAINT `FK_cys8fjpqxkvcyde4beis0n3lx` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_columns`
--
ALTER TABLE `search_filter_columns`
  ADD CONSTRAINT `FK_k6e868fvh8225894xuyplaxri` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_customactions`
--
ALTER TABLE `search_filter_customactions`
  ADD CONSTRAINT `FK_82hut8geqdkp89x0uyl3n6iub` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_degrees`
--
ALTER TABLE `search_filter_degrees`
  ADD CONSTRAINT `FK_3rkguuxiipf4dcn38yt6nryj4` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_departments`
--
ALTER TABLE `search_filter_departments`
  ADD CONSTRAINT `FK_7vinqqsd2j0kidubm1ofpyc87` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_documenttypes`
--
ALTER TABLE `search_filter_documenttypes`
  ADD CONSTRAINT `FK_ji793qo35mmcehjs1y8y5luy6` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_embargos`
--
ALTER TABLE `search_filter_embargos`
  ADD CONSTRAINT `FK_mf5xk637mx8l8scb9m1iu2hod` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_excluded_actionlogs`
--
ALTER TABLE `search_filter_excluded_actionlogs`
  ADD CONSTRAINT `FK_5o8iokop2rxq2ciq162j0xca0` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_excluded_submissions`
--
ALTER TABLE `search_filter_excluded_submissions`
  ADD CONSTRAINT `FK_7s000trks1u9ir2nn1vsida74` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_included_actionlogs`
--
ALTER TABLE `search_filter_included_actionlogs`
  ADD CONSTRAINT `FK_mg0crds89wdjejct5ly9nub5o` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_included_submissions`
--
ALTER TABLE `search_filter_included_submissions`
  ADD CONSTRAINT `FK_15qdawr540viu5in5mem02w62` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_majors`
--
ALTER TABLE `search_filter_majors`
  ADD CONSTRAINT `FK_3wwtaloot4o2yv8ue7jg8cyo0` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_programs`
--
ALTER TABLE `search_filter_programs`
  ADD CONSTRAINT `FK_x3e9uwkkebwkloegbyfayceh` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_semesters`
--
ALTER TABLE `search_filter_semesters`
  ADD CONSTRAINT `FK_miqf877rp21ethhtf3mep607r` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_states`
--
ALTER TABLE `search_filter_states`
  ADD CONSTRAINT `FK_85a9ru0277s4n4y19iqt2veo7` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `search_filter_text`
--
ALTER TABLE `search_filter_text`
  ADD CONSTRAINT `FK_5csq0a20x6lkhhddxl1mxflum` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`);

--
-- Constraints for table `submission`
--
ALTER TABLE `submission`
  ADD CONSTRAINT `FK_5tcephlarb35bwrijkqdrg0uu` FOREIGN KEY (`submitter_id`) REFERENCES `person` (`id`),
  ADD CONSTRAINT `FK_bsnfsllpukyx681rjx6t7kw0q` FOREIGN KEY (`assignee_id`) REFERENCES `person` (`id`),
  ADD CONSTRAINT `FK_q1frkfciq469vm81v1n44y6ev` FOREIGN KEY (`embargoType_id`) REFERENCES `embargo_type` (`id`);

--
-- Constraints for table `submission_embargotypes`
--
ALTER TABLE `submission_embargotypes`
  ADD CONSTRAINT `FK_b3h9qu258ulj5cu8crm175d5i` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`);

--
-- Constraints for table `submission_subjects`
--
ALTER TABLE `submission_subjects`
  ADD CONSTRAINT `FK_rexg1ed7ej7dng9e9aq6whvn3` FOREIGN KEY (`JpaSubmissionImpl_id`) REFERENCES `submission` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
