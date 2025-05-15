package com.bank.core.utils;

import com.bank.core.annotations.EnumDescription;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for extracting descriptions from enums annotated with @EnumDescription.
 */
@SuppressWarnings("unused")
public final class EnumDescriptionUtils {
    /**
     * Retrieves the description of an enum constant from its @EnumDescription annotation.
     *
     * @param enumValue the enum constant, must not be null
     * @return the description from @EnumDescription, or empty string if not found
     * @throws IllegalArgumentException if enumValue is null
     */
    public static String getEnumDescription(Enum<?> enumValue) {
        if (enumValue == null) {
            throw new IllegalArgumentException("Enum value cannot be null");
        }

        return Optional.ofNullable(getEnumField(enumValue))
                .filter(field -> field.isAnnotationPresent(EnumDescription.class))
                .map(field -> field.getAnnotation(EnumDescription.class).value())
                .orElse("");
    }

    /**
     * Retrieves a map of all enum constants' ordinals to their @EnumDescription values.
     *
     * @param enumClass the enum class, must not be null and must be an enum
     * @return an unmodifiable map of ordinal to description, empty if no descriptions found
     * @throws IllegalArgumentException if enumClass is null or not an enum
     */
    public static Map<Integer, String> getEnumDescriptions(Class<?> enumClass) {
        validateEnumClass(enumClass);

        return Collections.unmodifiableMap(
                Stream.of(enumClass.getFields())
                        .filter(field -> field.isAnnotationPresent(EnumDescription.class))
                        .collect(Collectors.toMap(
                                EnumDescriptionUtils::getEnumOrdinal,
                                EnumDescriptionUtils::getAnnotationValue,
                                (v1, _) -> v1 // In case of duplicates, keep first
                        ))
        );
    }

    private static Field getEnumField(Enum<?> enumValue) {
        try {
            Field field = enumValue.getClass().getField(enumValue.name());
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static void validateEnumClass(Class<?> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("Enum class cannot be null");
        }
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("Class must be an enum: " + enumClass.getName());
        }
    }

    private static Integer getEnumOrdinal(Field field) {
        try {
            return ((Enum<?>) field.get(null)).ordinal();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access enum field: " + field.getName(), e);
        }
    }

    private static String getAnnotationValue(Field field) {
        return field.getAnnotation(EnumDescription.class).value();
    }
}
