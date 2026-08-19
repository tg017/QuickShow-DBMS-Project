CREATE TABLE CustomerEmails(
    UserID INT,
    Email VARCHAR(30) UNIQUE,

    PRIMARY KEY (UserID, Email),

    FOREIGN KEY (UserID)
        REFERENCES Customer(UserID)
        ON DELETE CASCADE
);