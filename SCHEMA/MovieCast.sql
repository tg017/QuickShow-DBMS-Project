CREATE TABLE MovieCast(
    MovieID INT,
    Actor VARCHAR(100),

    PRIMARY KEY (MovieID, Actor),

    FOREIGN KEY (MovieID)
        REFERENCES Movie(MovieID)
        ON DELETE CASCADE
);