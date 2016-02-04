-- MySQL dump 10.13  Distrib 5.7.10, for osx10.9 (x86_64)
--
-- Host: localhost    Database: birzha
-- ------------------------------------------------------
-- Server version	5.7.10

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
-- Table structure for table `COMMISSION`
--

DROP TABLE IF EXISTS `COMMISSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COMMISSION` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `operation_type` int(40) NOT NULL,
  `value` double(40,9) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `COMMISSION` (`operation_type`),
  CONSTRAINT `commission_ibfk_1` FOREIGN KEY (`operation_type`) REFERENCES `OPERATION_TYPE` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COMMISSION`
--

LOCK TABLES `COMMISSION` WRITE;
/*!40000 ALTER TABLE `COMMISSION` DISABLE KEYS */;
INSERT INTO `COMMISSION` VALUES (1,3,11.000000000,'2016-01-19 12:48:11'),(2,3,12.000000000,'2016-01-19 12:48:21'),(3,4,5.000000000,'2016-01-19 12:49:39'),(4,4,9.000000000,'2016-01-19 12:49:49'),(5,1,0.110000000,'2016-01-31 17:44:03');
/*!40000 ALTER TABLE `COMMISSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CURRENCY`
--

DROP TABLE IF EXISTS `CURRENCY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CURRENCY` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ID_cur_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CURRENCY`
--

LOCK TABLES `CURRENCY` WRITE;
/*!40000 ALTER TABLE `CURRENCY` DISABLE KEYS */;
INSERT INTO `CURRENCY` VALUES (1,'XX','XX');
/*!40000 ALTER TABLE `CURRENCY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `IP_Log`
--

DROP TABLE IF EXISTS `IP_Log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IP_Log` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) NOT NULL,
  `user_id` int(40) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_IP_Logs_USERS1_idx` (`user_id`),
  CONSTRAINT `fk_IP_Logs_USERS1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `IP_Log`
--

LOCK TABLES `IP_Log` WRITE;
/*!40000 ALTER TABLE `IP_Log` DISABLE KEYS */;
INSERT INTO `IP_Log` VALUES (1,'0:0:0:0:0:0:0:1',1,'2016-01-21 20:48:49'),(2,'0:0:0:0:0:0:0:1',1,'2016-01-21 21:11:23'),(3,'0:0:0:0:0:0:0:1',1,'2016-01-21 21:12:46'),(4,'0:0:0:0:0:0:0:1',1,'2016-01-21 21:15:57'),(5,'0:0:0:0:0:0:0:1',1,'2016-01-21 21:17:14'),(6,'0:0:0:0:0:0:0:1',1,'2016-01-21 21:19:34'),(7,'0:0:0:0:0:0:0:1',1,'2016-01-21 21:24:47'),(8,'0:0:0:0:0:0:0:1',1,'2016-01-21 21:27:12'),(9,'0:0:0:0:0:0:0:1',1,'2016-01-22 10:55:47'),(10,'0:0:0:0:0:0:0:1',1,'2016-01-22 11:08:47'),(11,'0:0:0:0:0:0:0:1',1,'2016-01-22 11:09:52'),(12,'0:0:0:0:0:0:0:1',1,'2016-01-22 11:11:26'),(13,'0:0:0:0:0:0:0:1',1,'2016-01-22 11:12:39'),(14,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:05:19'),(15,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:06:33'),(16,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:10:40'),(17,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:33:24'),(18,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:41:09'),(19,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:46:38'),(20,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:48:46'),(21,'0:0:0:0:0:0:0:1',1,'2016-01-22 12:52:51'),(22,'0:0:0:0:0:0:0:1',1,'2016-01-22 13:02:32'),(23,'0:0:0:0:0:0:0:1',1,'2016-01-22 13:39:18'),(24,'0:0:0:0:0:0:0:1',1,'2016-01-22 13:40:59'),(25,'0:0:0:0:0:0:0:1',1,'2016-01-22 13:53:34'),(26,'0:0:0:0:0:0:0:1',1,'2016-01-22 14:27:08'),(27,'0:0:0:0:0:0:0:1',1,'2016-01-22 14:29:01'),(28,'0:0:0:0:0:0:0:1',1,'2016-01-22 14:34:46'),(29,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:24:34'),(30,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:26:55'),(31,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:29:12'),(32,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:31:09'),(33,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:32:11'),(34,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:33:48'),(35,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:34:55'),(36,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:36:54'),(37,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:38:00'),(38,'0:0:0:0:0:0:0:1',1,'2016-01-22 15:39:57'),(39,'0:0:0:0:0:0:0:1',1,'2016-01-22 16:07:45'),(40,'0:0:0:0:0:0:0:1',1,'2016-01-22 16:16:01'),(41,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:25:58'),(42,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:31:35'),(43,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:31:44'),(44,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:39:12'),(45,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:46:11'),(46,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:49:07'),(47,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:53:05'),(48,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:56:28'),(49,'0:0:0:0:0:0:0:1',1,'2016-01-25 16:57:29'),(50,'0:0:0:0:0:0:0:1',1,'2016-01-25 19:34:47'),(51,'0:0:0:0:0:0:0:1',1,'2016-01-25 19:50:47'),(52,'0:0:0:0:0:0:0:1',1,'2016-01-25 19:58:17'),(53,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:00:13'),(54,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:01:29'),(55,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:04:43'),(56,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:08:27'),(57,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:10:54'),(58,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:16:50'),(59,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:25:05'),(60,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:29:00'),(61,'0:0:0:0:0:0:0:1',1,'2016-01-25 20:32:07'),(62,'0:0:0:0:0:0:0:1',1,'2016-01-26 00:15:21'),(63,'0:0:0:0:0:0:0:1',1,'2016-01-26 00:16:19'),(64,'0:0:0:0:0:0:0:1',1,'2016-01-26 01:12:44'),(65,'0:0:0:0:0:0:0:1',1,'2016-01-26 01:16:21'),(66,'0:0:0:0:0:0:0:1',1,'2016-01-26 01:18:24'),(67,'0:0:0:0:0:0:0:1',1,'2016-01-26 01:21:28'),(68,'0:0:0:0:0:0:0:1',1,'2016-01-26 02:42:36'),(69,'0:0:0:0:0:0:0:1',1,'2016-01-26 02:44:26'),(70,'0:0:0:0:0:0:0:1',1,'2016-01-26 03:03:28'),(71,'0:0:0:0:0:0:0:1',1,'2016-01-26 08:16:22'),(72,'0:0:0:0:0:0:0:1',1,'2016-01-26 08:18:58'),(73,'0:0:0:0:0:0:0:1',1,'2016-01-26 09:19:10'),(74,'0:0:0:0:0:0:0:1',1,'2016-01-26 09:20:47'),(75,'0:0:0:0:0:0:0:1',1,'2016-01-26 09:21:36'),(76,'0:0:0:0:0:0:0:1',1,'2016-01-26 09:23:36'),(77,'0:0:0:0:0:0:0:1',1,'2016-01-26 09:28:29'),(78,'0:0:0:0:0:0:0:1',1,'2016-01-26 10:24:21'),(79,'0:0:0:0:0:0:0:1',1,'2016-01-26 10:46:07'),(80,'0:0:0:0:0:0:0:1',1,'2016-01-26 11:09:21'),(81,'0:0:0:0:0:0:0:1',1,'2016-01-26 11:10:29'),(82,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:02:17'),(83,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:36:06'),(84,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:39:37'),(85,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:49:28'),(86,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:50:47'),(87,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:52:03'),(88,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:52:56'),(89,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:57:49'),(90,'0:0:0:0:0:0:0:1',1,'2016-01-26 17:59:19'),(91,'0:0:0:0:0:0:0:1',1,'2016-01-26 18:20:02'),(92,'0:0:0:0:0:0:0:1',1,'2016-01-26 18:22:07'),(93,'0:0:0:0:0:0:0:1',1,'2016-01-26 18:22:37'),(94,'0:0:0:0:0:0:0:1',1,'2016-01-26 18:26:21'),(95,'0:0:0:0:0:0:0:1',1,'2016-01-26 18:27:57'),(96,'0:0:0:0:0:0:0:1',1,'2016-01-26 18:28:50'),(97,'0:0:0:0:0:0:0:1',1,'2016-01-27 14:27:59'),(98,'0:0:0:0:0:0:0:1',1,'2016-01-27 14:28:02'),(99,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:30:59'),(100,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:32:43'),(101,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:35:59'),(102,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:38:13'),(103,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:39:55'),(104,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:42:07'),(105,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:45:41'),(106,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:48:23'),(107,'0:0:0:0:0:0:0:1',1,'2016-01-27 17:51:34'),(108,'0:0:0:0:0:0:0:1',3,'2016-01-29 18:35:28'),(109,'0:0:0:0:0:0:0:1',3,'2016-01-29 19:28:24'),(110,'0:0:0:0:0:0:0:1',3,'2016-01-29 19:30:27'),(111,'0:0:0:0:0:0:0:1',3,'2016-01-29 20:05:51'),(112,'0:0:0:0:0:0:0:1',3,'2016-01-29 20:08:58'),(113,'0:0:0:0:0:0:0:1',3,'2016-01-29 20:11:12'),(114,'0:0:0:0:0:0:0:1',3,'2016-01-29 20:57:20'),(115,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:06:11'),(116,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:08:55'),(117,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:09:53'),(118,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:26:35'),(119,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:42:00'),(120,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:46:01'),(121,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:47:16'),(122,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:48:46'),(123,'0:0:0:0:0:0:0:1',3,'2016-01-29 21:50:19'),(124,'0:0:0:0:0:0:0:1',3,'2016-01-29 22:54:23'),(125,'0:0:0:0:0:0:0:1',3,'2016-01-29 22:56:46'),(126,'0:0:0:0:0:0:0:1',3,'2016-01-29 23:10:06'),(127,'0:0:0:0:0:0:0:1',3,'2016-01-29 23:42:58'),(128,'0:0:0:0:0:0:0:1',3,'2016-01-29 23:59:58'),(129,'0:0:0:0:0:0:0:1',3,'2016-01-30 00:37:44'),(130,'0:0:0:0:0:0:0:1',3,'2016-01-30 00:52:45'),(131,'0:0:0:0:0:0:0:1',3,'2016-01-30 00:53:57'),(132,'0:0:0:0:0:0:0:1',3,'2016-01-30 00:56:53'),(133,'0:0:0:0:0:0:0:1',3,'2016-01-30 00:59:23'),(134,'0:0:0:0:0:0:0:1',3,'2016-01-30 01:01:09'),(135,'0:0:0:0:0:0:0:1',3,'2016-01-30 01:03:26'),(136,'0:0:0:0:0:0:0:1',3,'2016-01-30 01:19:08'),(137,'0:0:0:0:0:0:0:1',3,'2016-01-30 15:32:23'),(138,'0:0:0:0:0:0:0:1',3,'2016-01-30 15:35:11'),(139,'0:0:0:0:0:0:0:1',3,'2016-01-30 16:24:23'),(140,'0:0:0:0:0:0:0:1',3,'2016-01-30 16:27:57'),(141,'0:0:0:0:0:0:0:1',3,'2016-01-30 16:30:42'),(142,'0:0:0:0:0:0:0:1',3,'2016-01-30 16:33:00'),(143,'0:0:0:0:0:0:0:1',3,'2016-01-30 16:48:35'),(144,'0:0:0:0:0:0:0:1',3,'2016-01-30 17:58:58'),(145,'0:0:0:0:0:0:0:1',3,'2016-01-30 18:04:18'),(146,'0:0:0:0:0:0:0:1',3,'2016-01-30 18:05:08'),(147,'0:0:0:0:0:0:0:1',3,'2016-01-30 18:06:17'),(148,'0:0:0:0:0:0:0:1',3,'2016-01-30 18:32:23'),(149,'0:0:0:0:0:0:0:1',3,'2016-01-30 18:48:30'),(150,'0:0:0:0:0:0:0:1',3,'2016-01-30 21:04:04'),(151,'0:0:0:0:0:0:0:1',3,'2016-01-30 21:14:19'),(152,'0:0:0:0:0:0:0:1',3,'2016-01-30 21:22:34'),(153,'0:0:0:0:0:0:0:1',3,'2016-01-30 21:24:29'),(154,'0:0:0:0:0:0:0:1',3,'2016-01-30 21:33:01'),(155,'0:0:0:0:0:0:0:1',3,'2016-01-30 21:42:12'),(156,'0:0:0:0:0:0:0:1',3,'2016-01-30 21:43:32'),(157,'0:0:0:0:0:0:0:1',4,'2016-01-30 21:49:24'),(158,'0:0:0:0:0:0:0:1',3,'2016-01-30 22:07:22'),(159,'0:0:0:0:0:0:0:1',3,'2016-01-30 22:09:29'),(160,'0:0:0:0:0:0:0:1',5,'2016-01-30 22:12:54'),(161,'0:0:0:0:0:0:0:1',3,'2016-01-30 22:44:15'),(162,'0:0:0:0:0:0:0:1',3,'2016-01-31 11:48:40'),(163,'0:0:0:0:0:0:0:1',3,'2016-01-31 23:31:00'),(164,'0:0:0:0:0:0:0:1',3,'2016-01-31 23:50:41'),(165,'0:0:0:0:0:0:0:1',3,'2016-02-01 00:10:24'),(166,'0:0:0:0:0:0:0:1',3,'2016-02-01 00:25:23'),(167,'0:0:0:0:0:0:0:1',3,'2016-02-01 00:30:07'),(168,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:31:22'),(169,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:35:18'),(170,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:36:04'),(171,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:37:17'),(172,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:38:33'),(173,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:41:37'),(174,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:50:10'),(175,'0:0:0:0:0:0:0:1',3,'2016-02-01 13:59:17'),(176,'0:0:0:0:0:0:0:1',3,'2016-02-01 14:02:43'),(177,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:22:21'),(178,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:23:37'),(179,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:23:41'),(180,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:23:56'),(181,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:24:00'),(182,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:24:03'),(183,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:24:10'),(184,'0:0:0:0:0:0:0:1',3,'2016-02-01 15:26:06');
/*!40000 ALTER TABLE `IP_Log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MERCHANTS`
--

DROP TABLE IF EXISTS `MERCHANTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MERCHANTS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(50) DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MERCHANTS`
--

LOCK TABLES `MERCHANTS` WRITE;
/*!40000 ALTER TABLE `MERCHANTS` DISABLE KEYS */;
INSERT INTO `MERCHANTS` VALUES (1,'Yandex money payment system','Yandex Money');
/*!40000 ALTER TABLE `MERCHANTS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MERCHANTS_CURRENCY`
--

DROP TABLE IF EXISTS `MERCHANTS_CURRENCY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MERCHANTS_CURRENCY` (
  `merchant_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  PRIMARY KEY (`merchant_id`,`currency_id`),
  KEY `MERCHANTS` (`currency_id`),
  CONSTRAINT `merchants_currency_ibfk_1` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `merchants_currency_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANTS` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MERCHANTS_CURRENCY`
--

LOCK TABLES `MERCHANTS_CURRENCY` WRITE;
/*!40000 ALTER TABLE `MERCHANTS_CURRENCY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MERCHANTS_CURRENCY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `OPERATION_TYPE`
--

DROP TABLE IF EXISTS `OPERATION_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OPERATION_TYPE` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ID_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `OPERATION_TYPE`
--

LOCK TABLES `OPERATION_TYPE` WRITE;
/*!40000 ALTER TABLE `OPERATION_TYPE` DISABLE KEYS */;
INSERT INTO `OPERATION_TYPE` VALUES (1,'input',NULL),(2,'output',NULL),(3,'sell',NULL),(4,'buy',NULL);
/*!40000 ALTER TABLE `OPERATION_TYPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ORDERS`
--

DROP TABLE IF EXISTS `ORDERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ORDERS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `wallet_id_sell` int(40) NOT NULL,
  `amount_sell` double(40,9) NOT NULL,
  `wallet_id_buy` int(40) DEFAULT NULL,
  `currency_buy` int(40) NOT NULL,
  `exchange_rate` double(40,9) NOT NULL,
  `operation_type` int(40) NOT NULL,
  `status` int(40) NOT NULL DEFAULT '1',
  `date_creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_final` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_order_UNIQUE` (`id`),
  KEY `fk_ORDERS_WALLET1_idx` (`wallet_id_sell`),
  KEY `fk_ORDER_CURRENCY1_idx` (`currency_buy`),
  KEY `fk_ORDER_OPERATION_TYPE1_idx` (`operation_type`),
  KEY `fk_ORDER_ORDER_STATUS1_idx` (`status`),
  CONSTRAINT `fk_ORDERS_WALLET1` FOREIGN KEY (`wallet_id_sell`) REFERENCES `WALLET` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_CURRENCY1` FOREIGN KEY (`currency_buy`) REFERENCES `CURRENCY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_OPERATION_TYPE1` FOREIGN KEY (`operation_type`) REFERENCES `OPERATION_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_ORDER_STATUS1` FOREIGN KEY (`status`) REFERENCES `ORDER_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ORDERS`
--

LOCK TABLES `ORDERS` WRITE;
/*!40000 ALTER TABLE `ORDERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `ORDERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ORDER_STATUS`
--

DROP TABLE IF EXISTS `ORDER_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ORDER_STATUS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ORDER_STATUS`
--

LOCK TABLES `ORDER_STATUS` WRITE;
/*!40000 ALTER TABLE `ORDER_STATUS` DISABLE KEYS */;
/*!40000 ALTER TABLE `ORDER_STATUS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRANSACTIONS`
--

DROP TABLE IF EXISTS `TRANSACTIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRANSACTIONS` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `wallet_id` int(40) NOT NULL,
  `amount` double(40,9) DEFAULT NULL,
  `transaction_type` int(1) DEFAULT '1',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `commission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_company_UNIQUE` (`id`),
  KEY `fk_COMPANY_ACCOUNT_WALLET1_idx` (`wallet_id`),
  KEY `COMPANY_ACCOUNT` (`commission_id`),
  CONSTRAINT `fk_COMPANY_ACCOUNT_WALLET1` FOREIGN KEY (`wallet_id`) REFERENCES `WALLET` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRANSACTIONS`
--

LOCK TABLES `TRANSACTIONS` WRITE;
/*!40000 ALTER TABLE `TRANSACTIONS` DISABLE KEYS */;
INSERT INTO `TRANSACTIONS` VALUES (3,2,0.890000000,1,'2016-02-01 13:59:28',1),(4,2,0.890000000,1,'2016-02-01 15:26:21',1);
/*!40000 ALTER TABLE `TRANSACTIONS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(100) NOT NULL,
  `regdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `phone` int(40) DEFAULT NULL,
  `finpassword` varchar(100) DEFAULT NULL,
  `status` varchar(45) NOT NULL DEFAULT 'active',
  `ipaddress` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idusers_UNIQUE` (`id`),
  UNIQUE KEY `nickname_UNIQUE` (`nickname`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USER`
--

LOCK TABLES `USER` WRITE;
/*!40000 ALTER TABLE `USER` DISABLE KEYS */;
INSERT INTO `USER` VALUES (1,'denis','denis@yandex.com','$2a$10$Ibqgzp4qaq22ouTVXpdwQ.FsO8u5f2zcII3ATGps3pIxD3AnRWnpe','2016-01-21 20:48:38',NULL,NULL,'active','0:0:0:0:0:0:0:1'),(2,'avc','xxx@yandex.com','$2a$10$yowJpezqB1VXuDG1H.P8V.y2sVwZxbaQj06iuo64nqhwOKyhZPlay','2016-01-21 21:24:40',NULL,NULL,'active',NULL),(3,'user','user@user.com','$2a$10$mYXhyBKEOESk4EMhlBfJbuTWV7SzoQY9fPblu.5J5oyVIz0LQhEEW','2016-01-29 18:35:16',NULL,NULL,'active','0:0:0:0:0:0:0:1'),(4,'de','denis@denis.com','$2a$10$0DX0D/A5VEg42PXNwAwt9el0d25PYi1bBBepAjVLLHy4vTGwsPEvW','2016-01-30 21:49:14',NULL,NULL,'active','0:0:0:0:0:0:0:1'),(5,'x','x@x.com','$2a$10$I8Ww9wci.xlEFa4bzxFNAuwT3scdAQ1h2JSusHjm1z9RUGRpt2sMm','2016-01-30 22:12:46',NULL,NULL,'active','0:0:0:0:0:0:0:1');
/*!40000 ALTER TABLE `USER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USER_ROLE`
--

DROP TABLE IF EXISTS `USER_ROLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER_ROLE` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_USER_ROLE_USER1_idx` (`user_id`),
  CONSTRAINT `fk_USER_ROLE_USER1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USER_ROLE`
--

LOCK TABLES `USER_ROLE` WRITE;
/*!40000 ALTER TABLE `USER_ROLE` DISABLE KEYS */;
/*!40000 ALTER TABLE `USER_ROLE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WALLET`
--

DROP TABLE IF EXISTS `WALLET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WALLET` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `user_id` int(40) NOT NULL,
  `active_balance` double(40,9) DEFAULT NULL,
  `reserved_balance` double(40,9) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_purse_UNIQUE` (`id`),
  KEY `fk_WALLET_CURRENCIES1_idx` (`currency_id`),
  KEY `fk_WALLET_USERS1_idx` (`user_id`),
  CONSTRAINT `fk_WALLET_CURRENCIES1` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_WALLET_USERS1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WALLET`
--

LOCK TABLES `WALLET` WRITE;
/*!40000 ALTER TABLE `WALLET` DISABLE KEYS */;
INSERT INTO `WALLET` VALUES (1,1,1,5.820000000,0.000000000),(2,1,3,3.560000000,0.000000000);
/*!40000 ALTER TABLE `WALLET` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `YANDEX_MONEY_MERCHANT`
--

DROP TABLE IF EXISTS `YANDEX_MONEY_MERCHANT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `YANDEX_MONEY_MERCHANT` (
  `user_id` int(11) NOT NULL,
  `access_token` varchar(400) NOT NULL,
  `expiration_date` date DEFAULT NULL,
  PRIMARY KEY (`access_token`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `yandex_money_merchant_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `YANDEX_MONEY_MERCHANT`
--

LOCK TABLES `YANDEX_MONEY_MERCHANT` WRITE;
/*!40000 ALTER TABLE `YANDEX_MONEY_MERCHANT` DISABLE KEYS */;
INSERT INTO `YANDEX_MONEY_MERCHANT` VALUES (3,'410013603227463.860A817BB4B6D19865E4134103B0FE93BF591A068E7C698B9051320093FDF6D06DD45A9B89EC5CD591D528CBB7455BB45B94BA52E4D3289D5F89ADA5A95CEEC513CE31DABE5A58212A38F653C817A3B3EFBF99176502DF341BDFB819FAA84847CE7451CB8226EB0C0CE692D3F9101CDFD6792F2974CB9205DFB4D752CD6D44EA','2019-02-01');
/*!40000 ALTER TABLE `YANDEX_MONEY_MERCHANT` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-02-01 17:49:38
