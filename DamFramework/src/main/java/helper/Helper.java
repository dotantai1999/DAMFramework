package helper;

import annotation.Column;
import annotation.OneToOne;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Helper {
    public static Object mapEntityDataWithResultSet(Class entityClass, ResultSet rs){
        Object resObject = null;
        try {
            resObject = entityClass.newInstance();
            Field[] fields = entityClass.getDeclaredFields();

            if (rs.next()) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Column.class)) {
                        String columnName = field.getAnnotation(Column.class).name();
                        Object columnValue = rs.getObject(columnName);
                        field.set(resObject, columnValue);
                    }
                }
            }

            return resObject;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public static boolean hasRelationship (Class entityClass, Class annotation) {
        Field[] fields = entityClass.getDeclaredFields();

        for(Field field : fields) {
            if(field.isAnnotationPresent(annotation)){
                return true;
            }
        }

        return false;
    }
}
