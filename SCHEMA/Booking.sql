CREATE TABLE Booking(
    BookingID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL,
    BookingDateTime TIMESTAMP,
    BookingStatus ENUM(
        'PENDING',
        'CONFIRMED',
        'FAILED'
    ) NOT NULL DEFAULT 'PENDING',
    TotalAmount BIGINT NOT NULL,
    TotalSeatsCount INT ,
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);
