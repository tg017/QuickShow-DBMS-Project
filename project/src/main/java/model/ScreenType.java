package model;

import lombok.Getter;

@Getter
public enum ScreenType {
    TWO_D("2D"),
    THREE_D("3D"),
    IMAX("IMAX"),
    IMAX3D("IMAX3D"),
    FOUR_DX("4DX");

    private final String databaseValue;


    ScreenType(String databaseValue) {
        this.databaseValue = databaseValue;
    }
}