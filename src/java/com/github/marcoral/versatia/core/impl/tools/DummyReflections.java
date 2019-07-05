package com.github.marcoral.versatia.core.impl.tools;

import java.lang.reflect.Field;

public class DummyReflections {
    public static Object getFieldValue(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            Field field;
            do {
                if(clazz == null)
                    throw new RuntimeException("No field with given name!");
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch(NoSuchFieldException e) {
                    field = null;
                }
                clazz = clazz.getSuperclass();
            } while(field == null);
            field.setAccessible(true);
            Object result = field.get(object);
            field.setAccessible(false);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
