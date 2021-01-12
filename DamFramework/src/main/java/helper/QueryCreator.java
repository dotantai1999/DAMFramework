package helper;

import Repository.DBConnectionImpl;
import annotation.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryCreator {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String createSqlDelete(Object object) throws IllegalAccessException {
        String tableName = "";
        Class zClass = object.getClass();

        if (zClass.isAnnotationPresent(Entity.class) && zClass.isAnnotationPresent(Table.class)) {
            Table tableClass = (Table) zClass.getAnnotation(Table.class);
            tableName = tableClass.name();
        }

        Field[] fields = zClass.getDeclaredFields();

        Column column = null;
        String sql = "DELETE FROM " + tableName + " WHERE 1 = 1 ";

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class) && field.get(object) != null) {
                sql += "AND " + field.getAnnotation(Column.class).name() + " = ? " ;
            }
        }

        System.out.println("gen sql: " + sql);

        return sql;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String createSqlUpdate(Object object) throws IllegalAccessException {
        String tableName = "";
        Class zClass = object.getClass();

        if (zClass.isAnnotationPresent(Entity.class) && zClass.isAnnotationPresent(Table.class)) {
            Table tableClass = (Table) zClass.getAnnotation(Table.class);
            tableName = tableClass.name();
        }

        Field[] fields = zClass.getDeclaredFields();

        Column column = null;
        String sql = "UPDATE " + tableName + " SET ";

        String idField = "";

        int count = 1;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {

                if(field.isAnnotationPresent(Id.class)){
                    idField = field.getAnnotation(Column.class).name();
                    continue;
                }

                if(count++ == 1){
                    sql += field.getAnnotation(Column.class).name() + " = ?" ;
                }else{
                    sql += ", " + field.getAnnotation(Column.class).name() + " = ?" ;
                }
            }
        }
        sql += " WHERE " + idField + " = ?";

        System.out.println("gen sql: " + sql);

        return sql;
    }

    public String createSqlSelect(Class zClass) {
        String sql = "SELECT * FROM ";
        String tableName = "";
        if(zClass.isAnnotationPresent(Table.class) && zClass.isAnnotationPresent(Entity.class)) {
            Table tableClass = (Table) zClass.getAnnotation(Table.class);
            tableName = tableClass.name();
        }

        sql += tableName + " WHERE ";

        String idField = "";

        Field[] fields = zClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                if(field.isAnnotationPresent(Id.class)){
                    idField = field.getAnnotation(Column.class).name();
                    break;
                }
            }
        }

        sql += idField + " = ?";
        System.out.println("select sql: " + sql);

        return sql;
    }

    public String createTable(Class classA, Class classB) {
        String tableA = "";
        String tableB = "";

        if(classA.isAnnotationPresent(Table.class) && classA.isAnnotationPresent(Entity.class)){
            Table table = (Table) classA.getAnnotation(Table.class);
            tableA = table.name();
        }
        if(classB.isAnnotationPresent(Table.class) && classB.isAnnotationPresent(Entity.class)){
            Table table = (Table) classB.getAnnotation(Table.class);
            tableB = table.name();
        }

        String tableName = tableA + "_" + tableB;

        Connection conn = null;
        Statement stmt = null;

        try{
            conn = DBConnectionImpl.getConnection();
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName
                    + " (" + tableA + "_id INTEGER,"
                    +  tableB + "_id INTEGER)";

            stmt = conn.createStatement();
            assert stmt != null;
            stmt.executeUpdate(sql);
            return tableName;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String createSqlInsert(Object object) {
        String tableName = "";
        Class zClass = object.getClass();
        if (zClass.isAnnotationPresent(Entity.class) && zClass.isAnnotationPresent(Table.class)) {
            Table tableClass = (Table) zClass.getAnnotation(Table.class);
            tableName = tableClass.name();
        }

        StringBuilder fields = new StringBuilder("");
        StringBuilder params = new StringBuilder("");

        for (Field field : zClass.getDeclaredFields()) {
            if (fields.length() > 1) {
                fields.append(",");
                params.append(",");
            }
            String columnName = "";
            if (field.isAnnotationPresent(Column.class) ) {
                Column column = field.getAnnotation(Column.class);
                columnName = column.name();
            }
            if (field.isAnnotationPresent(JoinColumn.class)){
                JoinColumn column = field.getAnnotation(JoinColumn.class);
                columnName = column.name();
            }
            fields.append(columnName);
            params.append("?");
        }

        Class<?> parentClass = zClass.getSuperclass();
        while (parentClass != null) {
            for (Field field : parentClass.getDeclaredFields()) {
                if (fields.length() > 1) {
                    fields.append(",");
                    params.append(",");
                }

                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    fields.append(column.name());
                    params.append("?");
                }
            }

            parentClass = parentClass.getSuperclass();
        }

        String sql = "INSERT INTO " + tableName + "(" + fields.toString() + ") VALUES (" + params.toString() + ")";
        return sql;
    }

    public String createSqlInsertV2(Object object) {
        String tableName = "";
        Class zClass = object.getClass();
        if (zClass.isAnnotationPresent(Entity.class) && zClass.isAnnotationPresent(Table.class)) {
            Table tableClass = (Table) zClass.getAnnotation(Table.class);
            tableName = tableClass.name();
        }

        StringBuilder fields = new StringBuilder("");
        StringBuilder params = new StringBuilder("");

        for (Field field : zClass.getDeclaredFields()) {
            String columnName = "";
            if (field.isAnnotationPresent(Column.class) ) {
                if (fields.length() > 1) {
                    fields.append(",");
                    params.append(",");
                }
                Column column = field.getAnnotation(Column.class);
                columnName = column.name();
                fields.append(columnName);
                params.append("?");
            }

            if (field.isAnnotationPresent(OneToOne.class) && field.isAnnotationPresent(JoinColumn.class)) {
                if (fields.length() > 1) {
                    fields.append(",");
                    params.append(",");
                }
                JoinColumn column = field.getAnnotation(JoinColumn.class);
                columnName = column.name();
                fields.append(columnName);
                params.append("?");
            }

            if (field.isAnnotationPresent(ManyToOne.class) && field.isAnnotationPresent(JoinColumn.class)) {
                if (fields.length() > 1) {
                    fields.append(",");
                    params.append(",");
                }
                JoinColumn column = field.getAnnotation(JoinColumn.class);
                columnName = column.name();
                fields.append(columnName);
                params.append("?");
            }
        }

        Class<?> parentClass = zClass.getSuperclass();
        while (parentClass != null) {
            for (Field field : parentClass.getDeclaredFields()) {
                if (fields.length() > 1) {
                    fields.append(",");
                    params.append(",");
                }

                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    fields.append(column.name());
                    params.append("?");
                }
            }

            parentClass = parentClass.getSuperclass();
        }

        String sql = "INSERT INTO " + tableName + "(" + fields.toString() + ") VALUES (" + params.toString() + ")";
        return sql;
    }
}