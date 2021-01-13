package service;

import annotation.Column;
import annotation.Id;

import java.lang.reflect.Field;

public interface Insertor {
    Object insert(Object obj);

    static Object insertedId(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        for(Field field: fields){
            field.setAccessible(true);
            if(field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class)){
                try {
                    if(field.get(obj) != null){
                        return field.get(obj);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
