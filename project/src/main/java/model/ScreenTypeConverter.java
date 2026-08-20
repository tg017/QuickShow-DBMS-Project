package model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ScreenTypeConverter implements AttributeConverter<ScreenType, String> {

    @Override
    public String convertToDatabaseColumn(ScreenType screenType) {
        if (screenType == null) {
            return null;
        }

        return screenType.getDatabaseValue();
    }

    @Override
    public ScreenType convertToEntityAttribute(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }

        for (ScreenType type : ScreenType.values()) {
            if (type.getDatabaseValue().equals(databaseValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "Unknown ScreenType: " + databaseValue
        );
    }
}
