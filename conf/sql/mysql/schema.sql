-- MySQL dump 10.13  Distrib 5.5.11, for osx10.5 (i386)
--
-- Host: localhost    Database: vireo
-- ------------------------------------------------------
-- Server version	5.5.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_colleges`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_colleges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_colleges` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `colleges` varchar(255) DEFAULT NULL,
  KEY `FK613A5EFD2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK613A5EFD2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_degrees`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_degrees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_degrees` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `degrees` varchar(255) DEFAULT NULL,
  KEY `FK5ACB1C62E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK5ACB1C62E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_departments`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_departments` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `departments` varchar(255) DEFAULT NULL,
  KEY `FK326B06802E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK326B06802E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_documentTypes`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_documentTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_documentTypes` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `documentTypes` varchar(255) DEFAULT NULL,
  KEY `FK685EA37D2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK685EA37D2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_majors`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_majors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_majors` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `majors` varchar(255) DEFAULT NULL,
  KEY `FKCD42EB5B2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FKCD42EB5B2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_searchText`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_searchText`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_searchText` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `searchText` varchar(255) DEFAULT NULL,
  KEY `FK395E4BF62E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK395E4BF62E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_semesters`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_semesters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_semesters` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `semesters` varchar(255) DEFAULT NULL,
  KEY `FK254C311A2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK254C311A2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JpaNamedSearchFilterImpl_states`
--

DROP TABLE IF EXISTS `JpaNamedSearchFilterImpl_states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `JpaNamedSearchFilterImpl_states` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `states` varchar(255) DEFAULT NULL,
  KEY `FKD887B7832E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FKD887B7832E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `actionlog`
--

DROP TABLE IF EXISTS `actionlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actionlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `actionDate` datetime DEFAULT NULL,
  `entry` longtext NOT NULL,
  `privateFlag` bit(1) NOT NULL,
  `submissionState` varchar(255) NOT NULL,
  `attachment_id` bigint(20) DEFAULT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKBD1F14E936E2B7C` (`attachment_id`),
  KEY `FKBD1F14EF967E3C` (`person_id`),
  KEY `FKBD1F14E2D7E525C` (`submission_id`),
  CONSTRAINT `FKBD1F14E2D7E525C` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`),
  CONSTRAINT `FKBD1F14E936E2B7C` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`),
  CONSTRAINT `FKBD1F14EF967E3C` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=953 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` varchar(255) DEFAULT NULL,
  `date` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `submission_id` (`submission_id`,`name`),
  KEY `FK8AF75923F967E3C` (`person_id`),
  KEY `FK8AF759232D7E525C` (`submission_id`),
  CONSTRAINT `FK8AF759232D7E525C` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`),
  CONSTRAINT `FK8AF75923F967E3C` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `college`
--

DROP TABLE IF EXISTS `college`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `college` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `committee_member`
--

DROP TABLE IF EXISTS `committee_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `committee_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chair` bit(1) NOT NULL,
  `displayOrder` int(11) NOT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `middleName` varchar(255) DEFAULT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1792999C2D7E525C` (`submission_id`),
  CONSTRAINT `FK1792999C2D7E525C` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `configuration`
--

DROP TABLE IF EXISTS `configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_action_definition`
--

DROP TABLE IF EXISTS `custom_action_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_action_definition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `label` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `label` (`label`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_action_value`
--

DROP TABLE IF EXISTS `custom_action_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_action_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` bit(1) NOT NULL,
  `definition_id` bigint(20) NOT NULL,
  `submission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `submission_id` (`submission_id`,`definition_id`),
  KEY `FKE49B30366B22F363` (`definition_id`),
  KEY `FKE49B30362D7E525C` (`submission_id`),
  CONSTRAINT `FKE49B30362D7E525C` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`),
  CONSTRAINT `FKE49B30366B22F363` FOREIGN KEY (`definition_id`) REFERENCES `custom_action_definition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `degree`
--

DROP TABLE IF EXISTS `degree`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `degree` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`level`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deposit_location`
--

DROP TABLE IF EXISTS `deposit_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deposit_location` (
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document_type`
--

DROP TABLE IF EXISTS `document_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`level`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_template`
--

DROP TABLE IF EXISTS `email_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `email_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `message` longtext NOT NULL,
  `name` varchar(255) NOT NULL,
  `subject` longtext NOT NULL,
  `systemRequired` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `embargo_type`
--

DROP TABLE IF EXISTS `embargo_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `embargo_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `description` longtext NOT NULL,
  `displayOrder` int(11) NOT NULL,
  `duration` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `graduation_month`
--

DROP TABLE IF EXISTS `graduation_month`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graduation_month` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `month` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `month` (`month`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `major`
--

DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `major` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `displayOrder` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
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
  `displayName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `institutionalIdentifier` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `middleName` varchar(255) DEFAULT NULL,
  `netid` varchar(255) DEFAULT NULL,
  `passwordHash` varchar(255) DEFAULT NULL,
  `permanentEmailAddress` varchar(255) DEFAULT NULL,
  `permanentPhoneNumber` varchar(255) DEFAULT NULL,
  `permanentPostalAddress` varchar(255) DEFAULT NULL,
  `role` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `netid` (`netid`)
) ENGINE=InnoDB AUTO_INCREMENT=215 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person_affiliations`
--

DROP TABLE IF EXISTS `person_affiliations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_affiliations` (
  `JpaPersonImpl_id` bigint(20) NOT NULL,
  `affiliations` varchar(255) DEFAULT NULL,
  KEY `FKE29E7C2DF4A0EAA1` (`JpaPersonImpl_id`),
  CONSTRAINT `FKE29E7C2DF4A0EAA1` FOREIGN KEY (`JpaPersonImpl_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `preference`
--

DROP TABLE IF EXISTS `preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preference` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` longtext,
  `person_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `person_id` (`person_id`,`name`),
  KEY `FKA8FCBCDBF967E3C` (`person_id`),
  CONSTRAINT `FKA8FCBCDBF967E3C` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter`
--

DROP TABLE IF EXISTS `search_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `publicFlag` bit(1) NOT NULL,
  `rangeEnd` date DEFAULT NULL,
  `rangeStart` date DEFAULT NULL,
  `umiRelease` bit(1) DEFAULT NULL,
  `unassigned` bit(1) DEFAULT NULL,
  `creator_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `creator_id` (`creator_id`,`name`),
  KEY `FK40B835EF2EF2D605` (`creator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_colleges`
--

DROP TABLE IF EXISTS `search_filter_colleges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_colleges` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `colleges` varchar(255) DEFAULT NULL,
  KEY `FK32FE0A0C2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK32FE0A0C2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_degrees`
--

DROP TABLE IF EXISTS `search_filter_degrees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_degrees` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `degrees` varchar(255) DEFAULT NULL,
  KEY `FK884FE8D72E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK884FE8D72E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_departments`
--

DROP TABLE IF EXISTS `search_filter_departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_departments` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `departments` varchar(255) DEFAULT NULL,
  KEY `FKB38A4D112E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FKB38A4D112E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_documenttypes`
--

DROP TABLE IF EXISTS `search_filter_documenttypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_documenttypes` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `documentTypes` varchar(255) DEFAULT NULL,
  KEY `FK208979EE2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK208979EE2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_embargo_type`
--

DROP TABLE IF EXISTS `search_filter_embargo_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_embargo_type` (
  `search_filter_id` bigint(20) NOT NULL,
  `embargos_id` bigint(20) NOT NULL,
  UNIQUE KEY `embargos_id` (`embargos_id`),
  KEY `FK63F94FB68E0B1A22` (`search_filter_id`),
  KEY `FK63F94FB69C9E0205` (`embargos_id`),
  CONSTRAINT `FK63F94FB69C9E0205` FOREIGN KEY (`embargos_id`) REFERENCES `embargo_type` (`id`),
  CONSTRAINT `FK63F94FB68E0B1A22` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_majors`
--

DROP TABLE IF EXISTS `search_filter_majors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_majors` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `majors` varchar(255) DEFAULT NULL,
  KEY `FKFAC40E2A2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FKFAC40E2A2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_person`
--

DROP TABLE IF EXISTS `search_filter_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_person` (
  `search_filter_id` bigint(20) NOT NULL,
  `assignees_id` bigint(20) NOT NULL,
  UNIQUE KEY `assignees_id` (`assignees_id`),
  KEY `FK1EA7A56F7C2AAD` (`assignees_id`),
  KEY `FK1EA7A58E0B1A22` (`search_filter_id`),
  CONSTRAINT `FK1EA7A58E0B1A22` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`),
  CONSTRAINT `FK1EA7A56F7C2AAD` FOREIGN KEY (`assignees_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_semesters`
--

DROP TABLE IF EXISTS `search_filter_semesters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_semesters` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `semesters` varchar(255) DEFAULT NULL,
  KEY `FK8BFDE7EB2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK8BFDE7EB2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_states`
--

DROP TABLE IF EXISTS `search_filter_states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_states` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `states` varchar(255) DEFAULT NULL,
  KEY `FK608DA522E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK608DA522E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_submission`
--

DROP TABLE IF EXISTS `search_filter_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_submission` (
  `search_filter_id` bigint(20) NOT NULL,
  `submissions_id` bigint(20) NOT NULL,
  UNIQUE KEY `submissions_id` (`submissions_id`),
  KEY `FK57729F9C8E0B1A22` (`search_filter_id`),
  KEY `FK57729F9C4783DD21` (`submissions_id`),
  CONSTRAINT `FK57729F9C4783DD21` FOREIGN KEY (`submissions_id`) REFERENCES `submission` (`id`),
  CONSTRAINT `FK57729F9C8E0B1A22` FOREIGN KEY (`search_filter_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_filter_text`
--

DROP TABLE IF EXISTS `search_filter_text`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_filter_text` (
  `JpaNamedSearchFilterImpl_id` bigint(20) NOT NULL,
  `searchText` varchar(255) DEFAULT NULL,
  KEY `FK8068257D2E6A9C33` (`JpaNamedSearchFilterImpl_id`),
  CONSTRAINT `FK8068257D2E6A9C33` FOREIGN KEY (`JpaNamedSearchFilterImpl_id`) REFERENCES `search_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `submission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `UMIRelease` bit(1) DEFAULT NULL,
  `approvalDate` datetime DEFAULT NULL,
  `college` varchar(255) DEFAULT NULL,
  `committeeApprovalDate` datetime DEFAULT NULL,
  `committeeContactEmail` varchar(255) DEFAULT NULL,
  `committeeEmailHash` varchar(255) DEFAULT NULL,
  `committeeEmbargoApprovalDate` datetime DEFAULT NULL,
  `degree` varchar(255) DEFAULT NULL,
  `degreeLevel` int(11) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `depositId` varchar(255) DEFAULT NULL,
  `documentAbstract` longtext,
  `documentKeywords` longtext,
  `documentTitle` longtext,
  `documentType` varchar(255) DEFAULT NULL,
  `graduationMonth` int(11) DEFAULT NULL,
  `graduationYear` int(11) DEFAULT NULL,
  `lastActionLogDate` datetime DEFAULT NULL,
  `lastActionLogEntry` longtext,
  `licenseAgreementDate` datetime DEFAULT NULL,
  `major` varchar(255) DEFAULT NULL,
  `stateName` varchar(255) DEFAULT NULL,
  `studentBirthYear` int(11) DEFAULT NULL,
  `studentFirstName` varchar(255) DEFAULT NULL,
  `studentLastName` varchar(255) DEFAULT NULL,
  `studentMiddleName` varchar(255) DEFAULT NULL,
  `submissionDate` datetime DEFAULT NULL,
  `assignee_id` bigint(20) DEFAULT NULL,
  `embargoType_id` bigint(20) DEFAULT NULL,
  `submitter_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `committeeEmailHash` (`committeeEmailHash`),
  KEY `FK84363B4C62B46408` (`submitter_id`),
  KEY `FK84363B4C44904182` (`assignee_id`),
  KEY `FK84363B4CC6F816D8` (`embargoType_id`),
  CONSTRAINT `FK84363B4CC6F816D8` FOREIGN KEY (`embargoType_id`) REFERENCES `embargo_type` (`id`),
  CONSTRAINT `FK84363B4C44904182` FOREIGN KEY (`assignee_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK84363B4C62B46408` FOREIGN KEY (`submitter_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=433 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-09-26  9:44:28
