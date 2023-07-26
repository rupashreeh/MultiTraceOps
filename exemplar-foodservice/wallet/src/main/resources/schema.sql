CREATE DATABASE IF NOT EXISTS test;
DROP TABLE IF EXISTS wallet cascade;

CREATE TABLE `wallet` (
  `custid` int NOT NULL  PRIMARY KEY,
  `balance` float
);
