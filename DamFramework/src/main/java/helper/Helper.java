package helper;

import annotation.Column;
import annotation.OneToOne;
import annotation.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static Class getClassObjectWithClassName(String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return clazz;
    }

    public static String getTableName(Class zClass) {

        if (zClass.isAnnotationPresent(Table.class)) {
            Table table = (Table) zClass.getAnnotation(Table.class);
            return table.name();
        }
        return "";
    }

    public static List<String> getListColumn(Class zClass) {
        List<String> result = new ArrayList<>();
        if (zClass.isAnnotationPresent(Table.class)) {
            Field[] fields = zClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    result.add(column.name());
                }
            }
        }
        return result;
    }

    public static List<String> getListAttributeName(Class zClass) {
        List<String> result = new ArrayList<>();
        if (zClass.isAnnotationPresent(Table.class)) {
            Field[] fields = zClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    String column = field.getName();
                    result.add(column);
                }
            }
        }
        return result;
    }

    // select * from Fresher fresher , Address address join Customer customer where ...
    public static String getClassNameFromQuery(String query) {
        String result = "";
        if (query.toLowerCase().startsWith("select")) {
            result = query.split("from ")[1].split(" ")[0];
        }
        return result;
    }

}
