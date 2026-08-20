CREATE TABLE Booking(
    BookingID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL,
    BookingDateTime TIMESTAMP,
    BookingStatus ENUM(
        'PENDING',
        'CONFIRMED',
        'FAILED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING',
    TotalAmount BIGINT NOT NULL,
    TotalSeatsCount INT NOT NULL CHECK (TotalSeatsCount > 0),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);