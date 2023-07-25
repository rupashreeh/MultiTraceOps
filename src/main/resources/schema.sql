use test;


CREATE TABLE IF NOT EXISTS `logDB` (
  `ts` BIGINT NOT NULL  PRIMARY KEY,
  `logData` text
);

CREATE TABLE IF NOT EXISTS `appMetricsDB` (
  `ts` BIGINT NOT NULL  PRIMARY KEY,
  `appMetricsData` text
);


CREATE TABLE IF NOT EXISTS `dbMetricsDB` (
  `ts` BIGINT NOT NULL  PRIMARY KEY,
  `dbMetricsData` text
);
