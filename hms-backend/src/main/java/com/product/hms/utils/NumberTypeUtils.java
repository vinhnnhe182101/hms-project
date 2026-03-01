package com.product.hms.utils;

@SuppressWarnings({"unchecked"})
public class NumberTypeUtils {
    private NumberTypeUtils() {
    }

    public static <NumberType extends Number> NumberType convertNumberValue(Object value, Class<NumberType> targetType) {
        if (value == null) return null;

        Number numValue = (Number) value;
        if (targetType == Double.class) {
            return (NumberType) Double.valueOf(numValue.doubleValue());
        } else if (targetType == Long.class) {
            return (NumberType) Long.valueOf(numValue.longValue());
        } else if (targetType == Integer.class) {
            return (NumberType) Integer.valueOf(numValue.intValue());
        } else if (targetType == Float.class) {
            return (NumberType) Float.valueOf(numValue.floatValue());
        }

        return (NumberType) numValue;
    }

    public static <NumberType extends Number> NumberType convertNumberValueVer2(Object value, Class<NumberType> targetType) {
        if (value == null) return null;

        if (value instanceof Number number) {
            if (targetType == Double.class) {
                return (NumberType) Double.valueOf(number.doubleValue());
            } else if (targetType == Long.class) {
                return (NumberType) Long.valueOf(number.longValue());
            } else if (targetType == Integer.class) {
                return (NumberType) Integer.valueOf(number.intValue());
            } else if (targetType == Float.class) {
                return (NumberType) Float.valueOf(number.floatValue());
            }
        } else if (value instanceof String str) {
            try {
                if (targetType == Double.class) {
                    return (NumberType) Double.valueOf(str);
                } else if (targetType == Long.class) {
                    return (NumberType) Long.valueOf(str);
                } else if (targetType == Integer.class) {
                    return (NumberType) Integer.valueOf(str);
                } else if (targetType == Float.class) {
                    return (NumberType) Float.valueOf(str);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot parse String to number: " + str, e);
            }
        }

        throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
    }

}
