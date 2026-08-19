CREATE TABLE Movie(
    MovieID INT PRIMARY KEY AUTO_INCREMENT,

    Title VARCHAR(50) NOT NULL,
    Poster VARCHAR(255),
    Language VARCHAR(20) NOT NULL,
    Genre VARCHAR(50),
    Duration INT NOT NULL,
    ReleaseDate DATE NOT NULL,

    IMDbRating DECIMAL(3,1)
        CHECK (IMDbRating >= 0.0 AND IMDbRating <= 10.0),

    Certificate ENUM(
        'U',
        'UA_7_PLUS',
        'UA_13_PLUS',
        'UA_16_PLUS',
        'A'
    ),

    Director VARCHAR(50),
    Description TEXT
);