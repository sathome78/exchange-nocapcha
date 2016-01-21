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
  `user_id` int(40) NOT NULL,
  `operation_type` int(40) NOT NULL,
  `value` double(40,9) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `COMPANY_ACCOUNT`
--

DROP TABLE IF EXISTS `COMPANY_ACCOUNT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COMPANY_ACCOUNT` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `wallet_id` int(40) NOT NULL,
  `amount` double(40,9) DEFAULT NULL,
  `transaction_type` int(1) DEFAULT '1',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_company_UNIQUE` (`id`),
  KEY `fk_COMPANY_ACCOUNT_WALLET1_idx` (`wallet_id`),
  CONSTRAINT `fk_COMPANY_ACCOUNT_WALLET1` FOREIGN KEY (`wallet_id`) REFERENCES `WALLET` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ORDER`
--

DROP TABLE IF EXISTS `ORDER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ORDER` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `YANDEX_MONEY_MERCHANT`
--

DROP TABLE IF EXISTS `YANDEX_MONEY_MERCHANT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `YANDEX_MONEY_MERCHANT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `access_token` varchar(100) DEFAULT NULL,
  `expiration_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-01-21 18:54:20
