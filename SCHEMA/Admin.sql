-- Active: 1787082271926@@127.0.0.1@3306@quickshow
use quickshow; 

CREATE TABLE Admin (
    AdminID INT PRIMARY KEY,
    FirstName VARCHAR(50),
    MiddleName VARCHAR(50),
    LastName VARCHAR(50),
    Role VARCHAR(30),
    Password VARCHAR(255),
    Email VARCHAR(100)
);