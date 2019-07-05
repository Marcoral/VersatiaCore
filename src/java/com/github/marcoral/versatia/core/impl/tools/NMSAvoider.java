package com.github.marcoral.versatia.core.impl.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSAvoider {
    @SuppressWarnings("unchecked")
	public static <T> T invokeNMSMethodOnObject(Object object, String methodName) {
        try {
            Method method = object.getClass().getMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(object);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Oops! It seems that this version of Versatia is not compatible with this NMS version!");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}