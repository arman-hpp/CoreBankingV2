package com.bank.utils.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class EnumUtils {
    public static Map<Integer, String> getEnumNames(Class<?> clazz) {
        var enumElementsMap = new HashMap<Integer, String>();
        try {
            for (Field field : clazz.getFields()) {
                field.setAccessible(true);
                enumElementsMap.put(
                        ((Enum<?>) field.get(null)).ordinal(),
                        StringUtils.capitalize(field.getName())
                );
            }

            return enumElementsMap;
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }
}
