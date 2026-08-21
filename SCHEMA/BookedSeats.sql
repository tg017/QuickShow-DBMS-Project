-- Active: 1787082271926@@127.0.0.1@3306@quickshow
CREATE TABLE BookedSeats (
    BookingID INT,
    ShowID INT,
    ScreenID INT,
    SeatID INT,

    PRIMARY KEY (BookingID, ShowID, ScreenID, SeatID),

    FOREIGN KEY (BookingID)
        REFERENCES Booking(BookingID),

    FOREIGN KEY (ShowID, ScreenID, SeatID)
        REFERENCES ShowSeatAllocates(ShowID, ScreenID, SeatID)
);
