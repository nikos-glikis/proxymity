SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE `proxymity_proxies` (
`id` int(11) NOT NULL,
  `host` varchar(100) NOT NULL,
  `port` int(11) NOT NULL,
  `type` enum('socks4','socks5','http','https') NOT NULL,
  `inserted` date NOT NULL,
  `lastchecked` datetime DEFAULT NULL,
  `status` enum('active','inactive','pending') NOT NULL,
  `fullanonymous` enum('yes','no') NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

ALTER TABLE `proxymity_proxies` ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `host` (`host`,`port`,`type`);

ALTER TABLE `proxymity_proxies` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `proxymity_proxies` ADD `remoteIp` VARCHAR(50) NULL ;