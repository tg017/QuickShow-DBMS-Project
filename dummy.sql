-- Active: 1786808158409@@127.0.0.1@3306@mvv

CREATE DATABASE mvv;

CREATE TABLE Customer(
    UserId INT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(20) NOT NULL,
    MiddleName VARCHAR(20),
    LastName VARCHAR(20),
    DOB DATE,
    Gender ENUM('M', 'F', 'O'),
    PhoneNo VARCHAR(10),
    Password VARCHAR(255), -- IGNORE Password for now implement the hash later
    HouseNo VARCHAR(10),
    Street VARCHAR(20),
    Area VARCHAR(20),
    City VARCHAR(20),
    State VARCHAR(20),
    PinCode VARCHAR(10)
);


CREATE TABLE CustomerEmails(
    UserID INT,
    Email VARCHAR(30) UNIQUE,
    PRIMARY KEY (UserID, Email),
    FOREIGN KEY (UserID) REFERENCES customer(UserID) ON DELETE CASCADE
);

CREATE TABLE Movie(
    MovieID INT PRIMARY KEY AUTO_INCREMENT,
    Title VARCHAR(50) NOT NULL,
    Poster VARCHAR(255),
    -- Poster URL
    Language VARCHAR(20) NOT NULL,
    Genre VARCHAR(50),
    Duration INT NOT NULL, -- Stored as num of minutes, example if the movie has runtime of 02:20:34, it will be rounded off to next num of minutes as 141
    ReleaseDate DATE NOT NULL,
    IMDbRating DECIMAL(3,1) CHECK (IMDbRating >= 0.0 and IMDbRating <= 10.0),
    Certificate ENUM(
        'U', 'UA_7_PLUS', 'UA_13_PLUS', 'UA_16_PLUS', 'A'
    ),
    Director VARCHAR(50),
    Description TEXT
);

CREATE TABLE MovieCast(
    MovieID INT,
    Actor VARCHAR(100),
    PRIMARY KEY (MovieID, Actor),
    FOREIGN KEY (MovieID) REFERENCES Movie(MovieID) ON DELETE CASCADE
);

CREATE TABLE Theatre(
    TheatreID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(50) NOT NULL,
    ContactNo VARCHAR(10),
    BuildingName VARCHAR(30),
    Street VARCHAR(20),
    Area VARCHAR(20),
    City VARCHAR(20),
    State VARCHAR(20),
    PinCode VARCHAR(10)
);

CREATE TABLE Screen(
    ScreenID INT PRIMARY KEY AUTO_INCREMENT,
    ScreenName VARCHAR(50) NOT NULL,
    ScreenType ENUM('2D', '3D', 'IMAX', 'IMAX3D', '4DX') DEFAULT '2D',
    SeatingCapacity INT NOT NULL CHECK (SeatingCapacity >= 0),
    TheatreID INT NOT NULL,
    FOREIGN KEY (TheatreID) REFERENCES Theatre(TheatreID) ON DELETE CASCADE
);

CREATE TABLE Seat(
    ScreenID INT,
    SeatID INT,
    RowNO VARCHAR(5) NOT NULL,
    SeatNo INT NOT NULL,
    PRIMARY KEY (ScreenID, SeatID),
    FOREIGN KEY (ScreenID) REFERENCES Screen(ScreenID) ON DELETE CASCADE,
    CONSTRAINT uq_seat UNIQUE(ScreenID, RowNo, SeatNo)
);


