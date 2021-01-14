package helper;

import Repository.DBConnectionImpl;
import annotation.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

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
                sql += "AND " + field.getAnnotation(Column.class).name() + " = ? ";
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

                if (field.isAnnotationPresent(Id.class)) {
                    idField = field.getAnnotation(Column.class).name();
                    continue;
                }

                if (count++ == 1) {
                    sql += field.getAnnotation(Column.class).name() + " = ?";
                } else {
                    sql += ", " + field.getAnnotation(Column.class).name() + " = ?";
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
        if (zClass.isAnnotationPresent(Table.class) && zClass.isAnnotationPresent(Entity.class)) {
            Table tableClass = (Table) zClass.getAnnotation(Table.class);
            tableName = tableClass.name();
        }

        sql += tableName + " WHERE ";

        String idField = "";

        Field[] fields = zClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(Id.class)) {
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

        if (classA.isAnnotationPresent(Table.class) && classA.isAnnotationPresent(Entity.class)) {
            Table table = (Table) classA.getAnnotation(Table.class);
            tableA = table.name();
        }
        if (classB.isAnnotationPresent(Table.class) && classB.isAnnotationPresent(Entity.class)) {
            Table table = (Table) classB.getAnnotation(Table.class);
            tableB = table.name();
        }

        String tableName = tableA + "_" + tableB;

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DBConnectionImpl.getConnection();
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName
                    + " (" + tableA + "_id INTEGER,"
                    + tableB + "_id INTEGER)";

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

        StringBuilder fields = new StringBuilder();
        StringBuilder params = new StringBuilder();

        for (Field field : zClass.getDeclaredFields()) {
            if (fields.length() > 1) {
                fields.append(",");
                params.append(",");
            }
            String columnName = "";
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                columnName = column.name();
            }
            if (field.isAnnotationPresent(JoinColumn.class)) {
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

        StringBuilder fields = new StringBuilder();
        StringBuilder params = new StringBuilder();

        for (Field field : zClass.getDeclaredFields()) {
            String columnName = "";
            if (field.isAnnotationPresent(Column.class)) {
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

    public String createOneToOneSelectQuery(Object entity, Field oneToOneField) {
        String columnName = oneToOneField.getAnnotation(JoinColumn.class).name();
        String refColumnName = oneToOneField.getAnnotation(JoinColumn.class).referenceColumnName();
        String tableName = entity.getClass().getAnnotation(Table.class).name();
        String refTableName = oneToOneField.getType().getAnnotation(Table.class).name();

        HashMap<String, Object> idData = getIdColumn(entity, "colName", "value");

        String sql = "SELECT " + refTableName + ".*" +
                " FROM " + tableName + " JOIN " + refTableName +
                " ON " + tableName + "." + columnName + " = " + refTableName + "." + refColumnName +
                " WHERE " + tableName + "." + idData.get("colName") + " = " + idData.get("value");

        return sql;
    }

    public String createGetRefColValueQuery(Object entity, Field refField) {
        String fieldColName = refField.getAnnotation(JoinColumn.class).name();
        String tableName = entity.getClass().getAnnotation(Table.class).name();

        String idValueKey = "idValue";
        String idColNameKey = "idColName";
        HashMap<String, Object> idData = getIdColumn(entity, idValueKey, idColNameKey);

        String sql = "SELECT " + fieldColName + " FROM " + tableName + " WHERE " + idData.get(idColNameKey) + " = " + idData.get(idValueKey);
        return sql;
    }

    public String createGetListOneToManyId(Object entity, Class<?> classB) {
        String refTableName = classB.getAnnotation(Table.class).name();

        String refTableIdColName = null;
        String refColName = null;

        Field[] bFields = classB.getDeclaredFields();
        for (Field bField : bFields) {
            if(bField.isAnnotationPresent(Id.class)){
                refTableIdColName = bField.getAnnotation(Column.class).name();
            }
            if (bField.isAnnotationPresent(ManyToOne.class) && bField.isAnnotationPresent(JoinColumn.class)) {
//                Class bFieldClass = bField.getClass();

                if (entity.getClass() == bField.getType()) {
                    refColName = bField.getAnnotation(JoinColumn.class).referenceColumnName();
                }
            }
        }

        HashMap<String, Object> entityId = getIdColumn(entity, "idValue", "idColName");

        if(refTableIdColName != null && refColName != null){
            String sql = "SELECT " + refTableIdColName + " FROM " + refTableName + " WHERE " + refColName + " = " + entityId.get("idValue");
            return sql;
        }

        return null;
    }

    public HashMap<String, Object> getIdColumn(Object obj, String idValueKey, String colNameKey) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    String idColumnName = field.getAnnotation(Column.class).name();
                    Object idColumnValue = field.get(obj);
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(idValueKey, idColumnValue);
                    result.put(colNameKey, idColumnName);
                    return result;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String createGetManyToOneObjectIdQuery(Object entity, Field manyToOneField) {
        String manyToOneColName = manyToOneField.getAnnotation(JoinColumn.class).name();
        String entityTableName = entity.getClass().getAnnotation(Table.class).name();
        HashMap<String, Object> entityId = getIdColumn(entity,"idValue", "idColName");


        String sql = "SELECT " + manyToOneColName +
                " FROM " + entityTableName +
                " WHERE " + entityId.get("idColName") + " = " + entityId.get("idValue");

        return sql;
    }
}
