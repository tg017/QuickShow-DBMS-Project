CREATE DATABASE quickshow ;

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