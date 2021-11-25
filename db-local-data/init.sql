CREATE DATABASE IF NOT EXISTS dev;

USE dev;

DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS users_address;

CREATE TABLE user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(250) DEFAULT NULL,
  password VARCHAR(250) DEFAULT NULL
);

CREATE TABLE address (
  id INT AUTO_INCREMENT PRIMARY KEY,
  address_1 VARCHAR(50) NOT NULL,
  address_2 VARCHAR(50) NOT NULL,
  city VARCHAR(250) DEFAULT NULL,
  state VARCHAR(250) DEFAULT NULL,
  zip VARCHAR(250) DEFAULT NULL,
  country VARCHAR(50) DEFAULT NULL
);


CREATE TABLE user_address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    address_id INT
);

INSERT INTO user (first_name,last_name,email,password) VALUES ('first1','last1','email1','password1');
INSERT INTO user (first_name,last_name,email,password) VALUES ('first2','last2','email2','password2');
INSERT INTO user (first_name,last_name,email,password) VALUES ('first3','last3','email3','password3');
INSERT INTO user (first_name,last_name,email,password) VALUES ('first4','last4','email4','password4');
INSERT INTO user (first_name,last_name,email,password) VALUES ('first5','last5','email5','password5');
INSERT INTO user (first_name,last_name,email,password) VALUES ('first6','last6','email6','password6');
INSERT INTO user (first_name,last_name,email,password) VALUES ('first7','last7','email7','password7');

INSERT INTO address (address_1,address_2,city,state,zip,country) VALUES ('address_12','address_22','city2','state2','zip2','country1');
INSERT INTO address (address_1,address_2,city,state,zip,country) VALUES ('address_13','address_23','city3','state3','zip3','country3');
INSERT INTO address (address_1,address_2,city,state,zip,country) VALUES ('address_11','address_21','city1','state1','zip1','country1');

INSERT INTO user_address (user_id,address_id) VALUES (1,1);
INSERT INTO user_address (user_id,address_id) VALUES (1,2);
INSERT INTO user_address (user_id,address_id) VALUES (2,1);
INSERT INTO user_address (user_id,address_id) VALUES (3,1);
INSERT INTO user_address (user_id,address_id) VALUES (4,2);
INSERT INTO user_address (user_id,address_id) VALUES (5,2);
INSERT INTO user_address (user_id,address_id) VALUES (6,2);
INSERT INTO user_address (user_id,address_id) VALUES (7,1);
INSERT INTO user_address (user_id,address_id) VALUES (7,3);