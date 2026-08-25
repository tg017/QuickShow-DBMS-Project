-- Active: 1787082271926@@127.0.0.1@3306@quickshow
CREATE TABLE Customer(
    UserId INT PRIMARY KEY AUTO_INCREMENT,

    FirstName VARCHAR(100) NOT NULL,
    MiddleName VARCHAR(100),
    LastName VARCHAR(100),

    DOB DATE,
    Gender ENUM('M', 'F', 'O'),
    PhoneNo VARCHAR(10),
    Password VARCHAR(255),

    HouseNo VARCHAR(10),
    Street VARCHAR(20),
    Area VARCHAR(20),
    City VARCHAR(20),
    State VARCHAR(20),
    PinCode VARCHAR(10)
);