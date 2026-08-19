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