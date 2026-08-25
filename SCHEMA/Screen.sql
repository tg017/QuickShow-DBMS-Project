CREATE TABLE Screen(
    ScreenID INT PRIMARY KEY AUTO_INCREMENT,

    ScreenName VARCHAR(50) NOT NULL,

    ScreenType ENUM(
        'TWO_D',
        'THREE_D',
        'IMAX',
        'IMAX_3D',
        'FOUR_DX'
    ) DEFAULT 'TWO_D',

    SeatingCapacity INT NOT NULL
        CHECK (SeatingCapacity >= 0),

    TheatreID INT NOT NULL,

    FOREIGN KEY (TheatreID)
        REFERENCES Theatre(TheatreID)
        ON DELETE CASCADE
);