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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COMMISSION`
--

LOCK TABLES `COMMISSION` WRITE;
/*!40000 ALTER TABLE `COMMISSION` DISABLE KEYS */;
INSERT INTO `COMMISSION` VALUES (6,1,11.000000000,'2016-02-10 10:58:11'),(7,2,11.000000000,'2016-02-10 10:58:17'),(8,3,8.000000000,'2016-02-10 10:58:23'),(9,4,8.000000000,'2016-02-10 10:58:34');
/*!40000 ALTER TABLE `COMMISSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COMPANY_TRANSACTION`
--

DROP TABLE IF EXISTS `COMPANY_TRANSACTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COMPANY_TRANSACTION` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `wallet_id` int(11) DEFAULT NULL,
  `sum` double(40,9) DEFAULT NULL,
  `currency_id` int(11) DEFAULT NULL,
  `operation_type_id` int(11) DEFAULT NULL,
  `merchant_id` int(11) DEFAULT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `COMPANY_TRANSACTION` (`wallet_id`),
  KEY `currency_id` (`currency_id`),
  KEY `fk_company_transaction_3` (`merchant_id`),
  KEY `fk_company_transaction_2` (`operation_type_id`),
  CONSTRAINT `company_transaction_ibfk_1` FOREIGN KEY (`wallet_id`) REFERENCES `COMPANY_WALLET` (`id`),
  CONSTRAINT `company_transaction_ibfk_2` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `company_transaction_ibfk_3` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`),
  CONSTRAINT `company_transaction_ibfk_4` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COMPANY_TRANSACTION`
--

LOCK TABLES `COMPANY_TRANSACTION` WRITE;
/*!40000 ALTER TABLE `COMPANY_TRANSACTION` DISABLE KEYS */;
INSERT INTO `COMPANY_TRANSACTION` VALUES (2,2,1.000000000,1,1,1,'2016-02-10 06:47:44'),(3,2,5.000000000,1,1,1,'2016-02-10 12:43:12'),(4,2,1.000000000,1,1,1,'2016-02-10 15:06:00'),(5,2,1.000000000,1,1,1,'2016-02-10 22:19:02'),(6,2,2.000000000,1,1,1,'2016-02-11 01:39:06'),(7,2,1.000000000,1,1,1,'2016-02-11 02:08:00');
/*!40000 ALTER TABLE `COMPANY_TRANSACTION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COMPANY_WALLET`
--

DROP TABLE IF EXISTS `COMPANY_WALLET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COMPANY_WALLET` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency_id` (`currency_id`),
  CONSTRAINT `company_wallet_ibfk_1` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COMPANY_WALLET`
--

LOCK TABLES `COMPANY_WALLET` WRITE;
/*!40000 ALTER TABLE `COMPANY_WALLET` DISABLE KEYS */;
INSERT INTO `COMPANY_WALLET` VALUES (2,1);
/*!40000 ALTER TABLE `COMPANY_WALLET` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CURRENCY`
--

LOCK TABLES `CURRENCY` WRITE;
/*!40000 ALTER TABLE `CURRENCY` DISABLE KEYS */;
INSERT INTO `CURRENCY` VALUES (1,'RUB','RUB'),(2,'USD','USD');
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
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `IP_Log`
--

LOCK TABLES `IP_Log` WRITE;
/*!40000 ALTER TABLE `IP_Log` DISABLE KEYS */;
INSERT INTO `IP_Log` VALUES (1,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:21:54'),(2,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:26:49'),(3,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:33:07'),(4,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:33:15'),(5,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:35:32'),(6,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:38:54'),(7,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:44:56'),(8,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:52:32'),(9,'0:0:0:0:0:0:0:1',1,'2016-02-04 16:57:41'),(10,'0:0:0:0:0:0:0:1',1,'2016-02-04 18:56:04'),(11,'0:0:0:0:0:0:0:1',1,'2016-02-04 19:02:55'),(12,'0:0:0:0:0:0:0:1',1,'2016-02-05 17:44:27'),(13,'0:0:0:0:0:0:0:1',1,'2016-02-05 17:49:44'),(14,'0:0:0:0:0:0:0:1',1,'2016-02-05 18:05:48'),(15,'0:0:0:0:0:0:0:1',1,'2016-02-05 18:09:35'),(16,'0:0:0:0:0:0:0:1',1,'2016-02-05 18:33:47'),(17,'0:0:0:0:0:0:0:1',1,'2016-02-05 19:35:23'),(18,'0:0:0:0:0:0:0:1',1,'2016-02-05 19:49:09'),(19,'0:0:0:0:0:0:0:1',1,'2016-02-05 19:55:15'),(20,'0:0:0:0:0:0:0:1',1,'2016-02-05 19:56:19'),(21,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:02:29'),(22,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:06:53'),(23,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:11:31'),(24,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:19:37'),(25,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:22:37'),(26,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:25:35'),(27,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:27:50'),(28,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:40:47'),(29,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:45:26'),(30,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:46:51'),(31,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:48:19'),(32,'0:0:0:0:0:0:0:1',1,'2016-02-05 20:51:36'),(33,'0:0:0:0:0:0:0:1',1,'2016-02-05 21:00:00'),(34,'0:0:0:0:0:0:0:1',1,'2016-02-05 21:07:10'),(35,'0:0:0:0:0:0:0:1',1,'2016-02-05 21:09:40'),(36,'0:0:0:0:0:0:0:1',1,'2016-02-05 21:11:16'),(37,'0:0:0:0:0:0:0:1',1,'2016-02-05 21:21:13'),(38,'0:0:0:0:0:0:0:1',1,'2016-02-06 22:09:56'),(39,'0:0:0:0:0:0:0:1',1,'2016-02-07 17:26:32'),(40,'0:0:0:0:0:0:0:1',1,'2016-02-07 17:27:27'),(41,'0:0:0:0:0:0:0:1',1,'2016-02-07 17:56:20'),(42,'0:0:0:0:0:0:0:1',1,'2016-02-07 17:56:51'),(43,'0:0:0:0:0:0:0:1',1,'2016-02-07 17:57:19'),(44,'0:0:0:0:0:0:0:1',1,'2016-02-07 20:04:43'),(45,'0:0:0:0:0:0:0:1',1,'2016-02-07 20:06:45'),(46,'0:0:0:0:0:0:0:1',1,'2016-02-07 20:12:29'),(47,'0:0:0:0:0:0:0:1',1,'2016-02-07 20:16:50'),(48,'0:0:0:0:0:0:0:1',1,'2016-02-07 20:20:01'),(49,'0:0:0:0:0:0:0:1',1,'2016-02-07 20:22:05'),(50,'0:0:0:0:0:0:0:1',1,'2016-02-08 08:39:30'),(51,'0:0:0:0:0:0:0:1',1,'2016-02-08 08:40:41'),(52,'0:0:0:0:0:0:0:1',1,'2016-02-08 08:41:51'),(53,'0:0:0:0:0:0:0:1',1,'2016-02-08 08:42:41'),(54,'0:0:0:0:0:0:0:1',1,'2016-02-08 08:58:59'),(55,'0:0:0:0:0:0:0:1',1,'2016-02-08 09:59:45'),(56,'0:0:0:0:0:0:0:1',1,'2016-02-08 10:44:38'),(57,'0:0:0:0:0:0:0:1',1,'2016-02-08 10:59:16'),(58,'0:0:0:0:0:0:0:1',1,'2016-02-08 11:09:43'),(59,'0:0:0:0:0:0:0:1',1,'2016-02-08 11:16:16'),(60,'0:0:0:0:0:0:0:1',1,'2016-02-08 11:49:26'),(61,'0:0:0:0:0:0:0:1',1,'2016-02-08 11:52:15'),(62,'0:0:0:0:0:0:0:1',1,'2016-02-08 11:57:49'),(63,'0:0:0:0:0:0:0:1',1,'2016-02-08 11:58:27'),(64,'0:0:0:0:0:0:0:1',1,'2016-02-08 12:26:16'),(65,'0:0:0:0:0:0:0:1',1,'2016-02-08 12:35:15'),(66,'0:0:0:0:0:0:0:1',1,'2016-02-08 12:51:26'),(67,'0:0:0:0:0:0:0:1',1,'2016-02-08 12:52:39'),(68,'0:0:0:0:0:0:0:1',1,'2016-02-08 12:55:22'),(69,'0:0:0:0:0:0:0:1',1,'2016-02-08 13:21:55'),(70,'0:0:0:0:0:0:0:1',1,'2016-02-08 13:37:04'),(71,'0:0:0:0:0:0:0:1',1,'2016-02-08 13:38:58'),(72,'0:0:0:0:0:0:0:1',1,'2016-02-08 13:44:52'),(73,'0:0:0:0:0:0:0:1',1,'2016-02-08 13:55:51'),(74,'0:0:0:0:0:0:0:1',1,'2016-02-08 18:06:55'),(75,'0:0:0:0:0:0:0:1',1,'2016-02-08 18:08:10'),(76,'0:0:0:0:0:0:0:1',1,'2016-02-09 07:59:52'),(77,'0:0:0:0:0:0:0:1',1,'2016-02-09 08:19:49'),(78,'0:0:0:0:0:0:0:1',1,'2016-02-09 08:22:19'),(79,'0:0:0:0:0:0:0:1',1,'2016-02-09 09:17:25'),(80,'0:0:0:0:0:0:0:1',1,'2016-02-09 09:20:43'),(81,'0:0:0:0:0:0:0:1',1,'2016-02-09 09:26:00'),(82,'0:0:0:0:0:0:0:1',1,'2016-02-09 09:28:54'),(83,'0:0:0:0:0:0:0:1',1,'2016-02-09 09:32:39'),(84,'0:0:0:0:0:0:0:1',1,'2016-02-09 09:34:09'),(85,'0:0:0:0:0:0:0:1',1,'2016-02-09 10:37:55'),(86,'0:0:0:0:0:0:0:1',1,'2016-02-09 10:53:46'),(87,'0:0:0:0:0:0:0:1',1,'2016-02-09 11:13:46'),(88,'0:0:0:0:0:0:0:1',1,'2016-02-09 12:37:37'),(89,'0:0:0:0:0:0:0:1',1,'2016-02-09 13:17:37'),(90,'0:0:0:0:0:0:0:1',1,'2016-02-09 13:38:00'),(91,'0:0:0:0:0:0:0:1',1,'2016-02-09 13:39:42'),(92,'0:0:0:0:0:0:0:1',1,'2016-02-09 14:16:55'),(93,'0:0:0:0:0:0:0:1',1,'2016-02-09 14:18:14'),(94,'0:0:0:0:0:0:0:1',1,'2016-02-09 15:36:12'),(95,'0:0:0:0:0:0:0:1',1,'2016-02-09 15:43:21'),(96,'0:0:0:0:0:0:0:1',1,'2016-02-09 19:50:35'),(97,'0:0:0:0:0:0:0:1',1,'2016-02-09 19:54:31'),(98,'0:0:0:0:0:0:0:1',1,'2016-02-09 20:22:40'),(99,'0:0:0:0:0:0:0:1',1,'2016-02-09 20:24:40'),(100,'0:0:0:0:0:0:0:1',1,'2016-02-09 20:36:45'),(101,'0:0:0:0:0:0:0:1',1,'2016-02-09 22:23:49'),(102,'0:0:0:0:0:0:0:1',1,'2016-02-09 22:37:51'),(103,'0:0:0:0:0:0:0:1',1,'2016-02-09 22:43:49'),(104,'0:0:0:0:0:0:0:1',1,'2016-02-09 23:02:04'),(105,'0:0:0:0:0:0:0:1',1,'2016-02-09 23:07:07'),(106,'0:0:0:0:0:0:0:1',1,'2016-02-09 23:22:44'),(107,'0:0:0:0:0:0:0:1',1,'2016-02-09 23:30:11'),(108,'0:0:0:0:0:0:0:1',1,'2016-02-09 23:53:45'),(109,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:06:10'),(110,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:07:30'),(111,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:10:57'),(112,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:12:16'),(113,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:19:02'),(114,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:25:16'),(115,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:28:57'),(116,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:30:21'),(117,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:33:02'),(118,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:33:07'),(119,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:36:00'),(120,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:38:58'),(121,'0:0:0:0:0:0:0:1',1,'2016-02-10 00:41:09'),(122,'0:0:0:0:0:0:0:1',1,'2016-02-10 06:46:15'),(123,'0:0:0:0:0:0:0:1',1,'2016-02-10 06:46:18'),(124,'0:0:0:0:0:0:0:1',1,'2016-02-10 06:50:14'),(125,'0:0:0:0:0:0:0:1',1,'2016-02-10 07:10:02'),(126,'0:0:0:0:0:0:0:1',1,'2016-02-10 08:38:15'),(127,'0:0:0:0:0:0:0:1',1,'2016-02-10 08:38:28'),(128,'0:0:0:0:0:0:0:1',1,'2016-02-10 08:52:47'),(129,'0:0:0:0:0:0:0:1',1,'2016-02-10 08:53:28'),(130,'0:0:0:0:0:0:0:1',1,'2016-02-10 08:53:30'),(131,'0:0:0:0:0:0:0:1',1,'2016-02-10 09:06:40'),(132,'0:0:0:0:0:0:0:1',1,'2016-02-10 09:27:31'),(133,'0:0:0:0:0:0:0:1',1,'2016-02-10 09:27:42'),(134,'0:0:0:0:0:0:0:1',1,'2016-02-10 09:41:22'),(135,'0:0:0:0:0:0:0:1',1,'2016-02-10 10:01:46'),(136,'0:0:0:0:0:0:0:1',1,'2016-02-10 10:36:48'),(137,'0:0:0:0:0:0:0:1',1,'2016-02-10 10:39:01'),(138,'0:0:0:0:0:0:0:1',1,'2016-02-10 10:45:55'),(139,'0:0:0:0:0:0:0:1',1,'2016-02-10 10:54:23'),(140,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:03:19'),(141,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:05:40'),(142,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:07:03'),(143,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:16:16'),(144,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:18:55'),(145,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:22:55'),(146,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:22:57'),(147,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:25:34'),(148,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:27:59'),(149,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:29:53'),(150,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:38:16'),(151,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:48:02'),(152,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:49:52'),(153,'0:0:0:0:0:0:0:1',1,'2016-02-10 11:49:55'),(154,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:01:04'),(155,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:03:41'),(156,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:06:02'),(157,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:19:02'),(158,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:20:58'),(159,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:24:45'),(160,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:31:20'),(161,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:32:22'),(162,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:34:04'),(163,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:40:56'),(164,'0:0:0:0:0:0:0:1',1,'2016-02-10 12:45:48'),(165,'0:0:0:0:0:0:0:1',1,'2016-02-10 13:08:37'),(166,'0:0:0:0:0:0:0:1',1,'2016-02-10 14:08:29'),(167,'0:0:0:0:0:0:0:1',1,'2016-02-10 14:09:22');
/*!40000 ALTER TABLE `IP_Log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MERCHANT`
--

DROP TABLE IF EXISTS `MERCHANT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MERCHANT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(50) DEFAULT NULL,
  `name` varchar(42) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MERCHANT`
--

LOCK TABLES `MERCHANT` WRITE;
/*!40000 ALTER TABLE `MERCHANT` DISABLE KEYS */;
INSERT INTO `MERCHANT` VALUES (1,'Yandex.Money','yandexmoney'),(2,'Perfect Money','perfectmoney');
/*!40000 ALTER TABLE `MERCHANT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MERCHANT_CURRENCY`
--

DROP TABLE IF EXISTS `MERCHANT_CURRENCY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MERCHANT_CURRENCY` (
  `merchant_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  PRIMARY KEY (`merchant_id`,`currency_id`),
  KEY `MERCHANTS` (`currency_id`),
  CONSTRAINT `merchant_currency_ibfk_1` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `merchant_currency_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `MERCHANT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MERCHANT_CURRENCY`
--

LOCK TABLES `MERCHANT_CURRENCY` WRITE;
/*!40000 ALTER TABLE `MERCHANT_CURRENCY` DISABLE KEYS */;
INSERT INTO `MERCHANT_CURRENCY` VALUES (1,1),(2,1),(2,2);
/*!40000 ALTER TABLE `MERCHANT_CURRENCY` ENABLE KEYS */;
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
  `amount_buy` double(40,9) NOT NULL,
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
  CONSTRAINT `fk_ORDERS_WALLET` FOREIGN KEY (`wallet_id_sell`) REFERENCES `WALLET` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_CURRENCY` FOREIGN KEY (`currency_buy`) REFERENCES `CURRENCY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_OPERATION_TYPE` FOREIGN KEY (`operation_type`) REFERENCES `OPERATION_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_ORDER_STATUS` FOREIGN KEY (`status`) REFERENCES `ORDER_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
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
-- Table structure for table `TRANSACTION`
--

DROP TABLE IF EXISTS `TRANSACTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRANSACTION` (
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
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRANSACTION`
--

LOCK TABLES `TRANSACTION` WRITE;
/*!40000 ALTER TABLE `TRANSACTION` DISABLE KEYS */;
INSERT INTO `TRANSACTION` VALUES (11,5,1.000000000,1,'2016-02-10 22:19:02',6),(12,5,1.200000000,0,'2016-02-11 00:00:14',7),(13,5,2.220000000,1,'2016-02-11 01:39:06',6),(14,8,1.110000000,1,'2016-02-11 02:08:00',6);
/*!40000 ALTER TABLE `TRANSACTION` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USER`
--

LOCK TABLES `USER` WRITE;
/*!40000 ALTER TABLE `USER` DISABLE KEYS */;
INSERT INTO `USER` VALUES (1,'user','user@user.com','$2a$10$BnftysQTrL3.5GXxk4SZ3uAf4a4TBZ/uZhDcFwdEBEYNezFRgH76y','2016-02-04 16:21:34',NULL,NULL,'active','0:0:0:0:0:0:0:1'),(2,'user1','user1@user.com','$2a$10$8sVufGF.thdgSoJLW70KD.ZzTvJOo.22EK4GMgJVXX6H0m/2ulxqi','2016-02-10 14:45:13',NULL,NULL,'active',NULL),(3,'user12','user12@user.com','$2a$10$HP8a4ddOxch4MHDRPZxBwOx6piI8PX1zGtAkTjoWYz6nnSHQFcxrO','2016-02-10 14:59:06',NULL,NULL,'active',NULL),(5,'denis','denis@denis.com','$2a$10$xF02A52SuuGdb7h1xpNQQuT0HzVsu9BNV2GRdxz1lfVjon0PIsSqm','2016-02-11 01:58:35',NULL,NULL,'active',NULL),(6,'abs','xx@xx.com','$2a$10$te6Xj6lGZXMC7ZQcuyPrkerROqyLnNM2xnsJoPRn2jCZiSF3lsN0.','2016-02-11 03:06:27',NULL,NULL,'active',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WALLET`
--

LOCK TABLES `WALLET` WRITE;
/*!40000 ALTER TABLE `WALLET` DISABLE KEYS */;
INSERT INTO `WALLET` VALUES (5,1,1,4.600000000,NULL),(6,1,3,1.000000000,NULL),(7,2,1,11.000000000,1.000000000),(8,1,5,1.000000000,NULL);
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

-- Dump completed on 2016-02-11 11:14:28
