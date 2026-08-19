CREATE TABLE ShowSeatAllocates (
    ShowID INT,
    SeatID INT,
    ScreenID INT,
    Status ENUM(
        'AVAILABLE',
        'BOOKED'
    ) NOT NULL DEFAULT 'AVAILABLE',

    PRIMARY KEY (ShowID, ScreenID, SeatID),

    FOREIGN KEY (ShowID) REFERENCES `Show`(ShowID),
    FOREIGN KEY (ScreenID, SeatID) REFERENCES Seat(ScreenID, SeatID)
);
