package com.product.hms.entity.converter.base;

import com.product.hms.entity.converter.base.core.DbValueEnum;
import jakarta.persistence.AttributeConverter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("ConverterNotAnnotatedInspection")
public abstract class BaseEnumStringConverter<EntityEnum extends Enum<EntityEnum> & DbValueEnum> implements AttributeConverter<EntityEnum, String> {
    private final Map<String, EntityEnum> dbValueToEnum;

    protected BaseEnumStringConverter(EntityEnum[] allEnumValues) {
        this.dbValueToEnum = new HashMap<>();

        for (EntityEnum enumValue : allEnumValues) {
            String normalizedDbValue = normalize(enumValue.getDbValue());
            if (dbValueToEnum.containsKey(normalizedDbValue)) {
                throw new IllegalStateException("Duplicate db value: " + enumValue.getDbValue());
            }
            dbValueToEnum.put(normalizedDbValue, enumValue);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public String convertToDatabaseColumn(EntityEnum attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public EntityEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        String normalizedDbData = normalize(dbData);
        EntityEnum enumValue = dbValueToEnum.get(normalizedDbData);
        if (enumValue == null) {
            throw new IllegalArgumentException("Unknown db value: " + dbData);
        }
        return enumValue;
    }
}
