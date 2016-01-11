CREATE TABLE `proxymity_proxies` (
`id` int(11) NOT NULL,
  `host` varchar(100) NOT NULL,
  `port` int(11) NOT NULL,
  `type` enum('socks4','socks5','http','https') NOT NULL,
  `inserted` datetime NOT NULL,
  `lastchecked` datetime DEFAULT NULL,
  `status` enum('active','inactive','pending') NOT NULL,
  `fullanonymous` enum('yes','no') NOT NULL,
  `remoteIp` varchar(50) DEFAULT NULL
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

--
ALTER TABLE `proxymity_proxies` ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `host` (`host`,`port`,`type`);

ALTER TABLE `proxymity_proxies` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=0;

ALTER TABLE `proxymity_proxies` CHANGE `status` `status` ENUM('active','inactive','pending','dead') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

ALTER TABLE `proxymity_proxies` ADD `lastactive` DATETIME NOT NULL ;