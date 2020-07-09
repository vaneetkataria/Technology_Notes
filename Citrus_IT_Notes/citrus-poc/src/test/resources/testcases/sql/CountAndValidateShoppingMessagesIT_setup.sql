DROP table IF EXISTS  appconfig.configservice;

CREATE TABLE appconfig.configservice (
  `config_key` varchar(255) NOT NULL,
  `config_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO appconfig.configservice (config_key,config_value) VALUES 
('cache.expire.hours','6')
,('cache.max.entries','1000')
,('debezium.database.server.name','configdb20')
,('kafka.bootstrap.servers','127.0.0.1:9092')
,('kafka.p2d.topic.name','p2d_updates_final')
,('kafka.producer.acks','all')
,('kafka.producer.linger.ms','0')
,('kafka.producer.retries','0')
,('spring.appconfig.datasource.driver-class-name','org.mariadb.jdbc.Driver')
,('spring.appconfig.datasource.password','root')
,('spring.appconfig.datasource.url','jdbc:mariadb://localhost:3307/appconfig')
,('spring.appconfig.datasource.username','root')
,('spring.content.datasource.driver-class-name','org.mariadb.jdbc.Driver')
,('spring.content.datasource.password','root')
,('spring.content.datasource.url','jdbc:mariadb://localhost:3307/content_pull')
,('spring.content.datasource.username','root')
,('spring.otapulladapter.datasource.driver-class-name','org.mariadb.jdbc.Driver')
,('spring.otapulladapter.datasource.password','root')
,('spring.otapulladapter.datasource.url','jdbc:mariadb://localhost:3307/otapulladapter')
,('spring.otapulladapter.datasource.username','root')
,('spring.productautomation.datasource.driver-class-name','org.mariadb.jdbc.Driver')
,('spring.productautomation.datasource.password','root')
,('spring.productautomation.datasource.url','jdbc:mariadb://localhost:3307/product_automation')
,('spring.productautomation.datasource.username','root')
,('spring.pushcore.datasource.driver-class-name','org.mariadb.jdbc.Driver')
,('spring.pushcore.datasource.password','root')
,('spring.pushcore.datasource.url','jdbc:mariadb://localhost:3307/pushcore')
,('spring.pushcore.datasource.username','root');


DROP table IF EXISTS appconfig.product_discovery;

CREATE TABLE appconfig.product_discovery (
  `config_key` varchar(255) NOT NULL,
  `config_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO appconfig.product_discovery (config_key,config_value) VALUES 
('channel.booking.name','BKG')
,('channel.booking.shop-interval','350')
,('mars.shopping.delay.seconds','10')
,('p2d.scheduler.booking.client','R2ARI')
,('p2d.scheduler.booking.sga.code','MT')
,('property.creation.enabled','true')
,('sp.datadump.api.url','http://localhost:8971')
,('spring.data.cassandra.contact-points','valdbsdevcas01a.asp.dhisco.com')
,('spring.data.cassandra.keyspace-name','productdiscovery')
,('spring.data.cassandra.port','9042')
,('spring.data.cassandra.stp.keyspace-name','stp')
,('spring.kafka.consumer.auto-commit-interval','6000')
,('spring.kafka.consumer.auto-offset-reset','latest')
,('spring.kafka.consumer.bootstrap-servers','127.0.0.1:9092')
,('spring.kafka.consumer.group-id','product-discovery')
,('spring.kafka.producer.bootstrap-servers','127.0.0.1:9092')
,('topic.p2d.scheduler.initial.shop','stp-admin-scheduler')
,('topic.property.create.event','product.discovery.property.create')
,('topic.property.state.event','product.discovery.property.state');



DROP database IF EXISTS `pushcore`;
create database `pushcore`;
DROP table IF EXISTS pushcore.sga;

CREATE TABLE pushcore.sga (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `matching_qualifier` varchar(255) DEFAULT NULL,
  `shop_sga_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_sga_nm` (`name`),
  KEY `FK_sga_shop_sga_id` (`shop_sga_id`),
  CONSTRAINT `FK_sga` FOREIGN KEY (`shop_sga_id`) REFERENCES `sga` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO pushcore.sga (name,created_at,updated_at,matching_qualifier,shop_sga_id) VALUES 
('MT','2019-07-01 18:50:52.000','2019-07-01 18:50:52.000','REQUESTED_ONLY',NULL);


DROP table IF EXISTS pushcore.brand;

CREATE TABLE pushcore.brand (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `is_shadowed` bit(1) DEFAULT b'0',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `consumer_count` int(11) NOT NULL DEFAULT 2,
  `code` varchar(255) NOT NULL,
  `topic` varchar(255) NOT NULL,
  `isShadowed` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_brand_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP table IF EXISTS pushcore.hotel_property;

CREATE TABLE pushcore.hotel_property (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `is_shadowed` bit(1) DEFAULT b'0',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `hotel_code` varchar(255) NOT NULL,
  `brand_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hp_cd_bid` (`hotel_code`,`brand_id`),
  UNIQUE KEY `UKrdkfsv2l9pak9njoe8goj328l` (`hotel_code`,`brand_id`),
  UNIQUE KEY `UKpmyi0v6cyrdw2id3sfease6no` (`hotel_code`,`brand_id`),
  KEY `FK_hp_bid` (`brand_id`),
  CONSTRAINT `FK8kl956y0l0wrm4ceekif1ob2` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP table IF EXISTS pushcore.inventory_code;
CREATE TABLE pushcore.inventory_code (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `is_shadowed` bit(1) DEFAULT b'0',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `name` varchar(255) NOT NULL,
  `hotel_property_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ic_hpid` (`name`,`hotel_property_id`),
  UNIQUE KEY `UK1fxg001146nwm58xanj8neqb5` (`name`,`hotel_property_id`),
  UNIQUE KEY `UK_ic_nm_hpid` (`name`,`hotel_property_id`),
  KEY `FK_ic_hpid` (`hotel_property_id`),
  CONSTRAINT `FKq5h3pcviuyh4y9nfe6u4curn` FOREIGN KEY (`hotel_property_id`) REFERENCES `hotel_property` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP table IF EXISTS pushcore.rate_plan;
CREATE TABLE pushcore.rate_plan (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `is_shadowed` bit(1) DEFAULT b'0',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `name` varchar(255) NOT NULL,
  `hotel_property_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_rp_nm_hpid` (`name`,`hotel_property_id`),
  UNIQUE KEY `UKhffmj81bpv8nte8rveprr4kxb` (`name`,`hotel_property_id`),
  KEY `FK_rp_hpid` (`hotel_property_id`),
  CONSTRAINT `FKiddw1pq54j6942s53naqg31u` FOREIGN KEY (`hotel_property_id`) REFERENCES `hotel_property` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP table IF EXISTS pushcore.`shop_profile`;
CREATE TABLE pushcore.`shop_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `is_shadowed` bit(1) DEFAULT b'0',
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `matching_qualifier` varchar(255) DEFAULT NULL,
  `maxlos` int(11) DEFAULT NULL,
  `maxnrcs` int(11) DEFAULT NULL,
  `maxocc` int(11) DEFAULT NULL,
  `maxrooms` int(11) DEFAULT NULL,
  `rolling_update_interval` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

DROP table IF EXISTS pushcore.shop_profile_brand;
CREATE TABLE pushcore.shop_profile_brand (
  `sp_id` bigint(20) NOT NULL,
  `brand_id` bigint(20) NOT NULL,
  `sga_id` bigint(20) NOT NULL,
  PRIMARY KEY (`sp_id`),
  UNIQUE KEY `UKfoc5g9rrbh4ku00un41sv3yk7` (`sga_id`,`brand_id`),
  KEY `FK8w3gpmxpi2vfphlo4i6efrfrk` (`brand_id`),
  CONSTRAINT `FK8w3gpmxpi2vfphlo4i6efrfr` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
  CONSTRAINT `FKf01w58q0pw9bj143lcj5dbms` FOREIGN KEY (`sp_id`) REFERENCES `shop_profile` (`id`),
  CONSTRAINT `FKf8wq8fq0jcuijl3ck76ay55a` FOREIGN KEY (`sga_id`) REFERENCES `sga` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP table IF EXISTS pushcore.manual_refresh_tables;

CREATE TABLE pushcore.`manual_refresh_tables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_name` (`table_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

INSERT INTO pushcore.manual_refresh_tables (table_name) VALUES 
('content_pull_service')
,('content_pull_trigger')
,('flightcenter_mapping')
,('hilton_sga_authentication')
,('supplier_platform');
