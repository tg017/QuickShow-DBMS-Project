CREATE TABLE Payment(
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    PaymentMethod ENUM(
    'CREDIT_CARD',
    'DEBIT_CARD',
    'UPI'
    ) NOT NULL,
    TransactionID VARCHAR(100) UNIQUE NOT NULL,
    PaymentAmount BIGINT NOT NULL,
    PaymentDateTime TIMESTAMP NOT NULL,
    PaymentStatus ENUM(
        'PENDING',
        'SUCCESS',
        'FAILED',
        'PROCESSING'
    ) NOT NULL  DEFAULT 'PENDING',
    BookingID INT NOT NULL,

    FOREIGN KEY (BookingID)
        REFERENCES Booking(BookingID)
)
