CREATE TABLE Screen(
    ScreenID INT PRIMARY KEY AUTO_INCREMENT,

    ScreenName VARCHAR(50) NOT NULL,

    ScreenType ENUM(
        '2D',
        '3D',
        'IMAX',
        'IMAX3D',
        '4DX'
    ) DEFAULT '2D',

    SeatingCapacity INT NOT NULL
        CHECK (SeatingCapacity >= 0),

    TheatreID INT NOT NULL,

    FOREIGN KEY (TheatreID)
        REFERENCES Theatre(TheatreID)
        ON DELETE CASCADE
);