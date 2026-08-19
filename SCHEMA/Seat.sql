CREATE TABLE Seat(
    ScreenID INT,
    SeatID INT,

    RowNO VARCHAR(5) NOT NULL,
    SeatNo INT NOT NULL,

    PRIMARY KEY (ScreenID, SeatID),

    FOREIGN KEY (ScreenID)
        REFERENCES Screen(ScreenID)
        ON DELETE CASCADE,

    CONSTRAINT uq_seat
        UNIQUE(ScreenID, RowNo, SeatNo)
);