-- Active: 1786808158409@@127.0.0.1@3306@quickshow
CREATE TABLE Admin (
    AdminID INT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(50),
    MiddleName VARCHAR(50),
    LastName VARCHAR(50),
    Role VARCHAR(30),
    Password VARCHAR(255),
    Email VARCHAR(100)
);