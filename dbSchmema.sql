-- phpMyAdmin SQL Dump
-- version 4.2.9
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jan 09, 2016 at 04:00 PM
-- Server version: 5.5.39
-- PHP Version: 5.3.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `proxies`
--

-- --------------------------------------------------------

--
-- Table structure for table `proxymity_proxies`
--

CREATE TABLE `proxymity_proxies` (
`id` int(11) NOT NULL,
  `host` varchar(100) NOT NULL,
  `port` int(11) NOT NULL,
  `type` enum('socks4','socks5','http','https') NOT NULL,
  `inserted` date NOT NULL,
  `lastchecked` datetime DEFAULT NULL,
  `status` enum('active','inactive','pending') NOT NULL,
  `fullanonymous` enum('yes','no') NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `proxymity_proxies`
--
ALTER TABLE `proxymity_proxies`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `host` (`host`,`port`,`type`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `proxymity_proxies`
--
ALTER TABLE `proxymity_proxies`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
