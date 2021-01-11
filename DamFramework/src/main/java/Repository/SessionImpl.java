package Repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.*;
import java.util.Collection;
import java.util.Iterator;

import annotation.*;
import javafx.scene.control.Tab;

import javax.swing.plaf.nimbus.State;

public class SessionImpl<T> implements ISession<T>{

    public SessionImpl() {

    }

    private String createSqlInsert(Object object) {
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

    private String createSqlInsertV2(Object object) {
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

    @Override
    public Object insert(Object object) {
        if(object == null) return null;
        String sql = createSqlInsert(object);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Object id = null;

            // connect with DB
            connection = DBConnectionImpl.getConnection();

            if (connection != null) {
                System.out.println("Ket noi thanh cong");
            }

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            /* setParameter(statement, parameters); */

            // set param
            Class aClass = object.getClass();
            Field[] fields = aClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                int index = i + 1;
                Field field = fields[i];
                field.setAccessible(true);
                statement.setObject(index, field.get(object));
            }

            // excute query
            statement.executeUpdate();

            // get Id from result in resultSet
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                id = resultSet.getObject(1);
            }

            connection.commit();
            return id;

        } catch (SQLException | IllegalAccessException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    DBConnectionImpl.close();
                }
                if (statement != null) {
                    statement.close();
                }

                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public Object insertV2(Object object) {
        if(object == null) return null;
        String sql = createSqlInsertV2(object);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Object id = null;

            // connect with DB
            connection = DBConnectionImpl.getConnection();

            if (connection != null) {
                System.out.println("Ket noi thanh cong");
            }

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            /* setParameter(statement, parameters); */

            // set param
            Class aClass = object.getClass();
            Field[] fields = aClass.getDeclaredFields();
            int count = 1;
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                if(field.isAnnotationPresent(Column.class)){
                    statement.setObject(count++, field.get(object));
                }
            }

            // excute query
            statement.executeUpdate();

            // get Id from result in resultSet
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                id = resultSet.getObject(1);
            }

            connection.commit();

            // set ID cho object sau khi Insert thành công
            for (Field field : fields) {
                if(field.isAnnotationPresent(Id.class)){
                    field.setAccessible(true);
                    if (id instanceof BigInteger) {
                        String strValue = ((BigInteger) id).toString();
                        id = Integer.parseInt(strValue);
                    }
                    field.set(object, id);
                }
            }
            return id;

        } catch (SQLException | IllegalAccessException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    DBConnectionImpl.close();
                }
                if (statement != null) {
                    statement.close();
                }

                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Object update(Object object) {
        String sql = "";
        try {
            sql = createSqlUpdate(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("update sql: " + sql);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Object id = null;

            // connect with DB
            connection = DBConnectionImpl.getConnection();

            if (connection != null) {
                System.out.println("Ket noi thanh cong");
            }

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            /* setParameter(statement, parameters); */

            // set param
            Class aClass = object.getClass();
            Field[] fields = aClass.getDeclaredFields();
            fields[0].setAccessible(true);
            statement.setObject(fields.length, fields[0].get(object));
            for (int i = 1; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                statement.setObject(i, field.get(object));
            }

            // excute query
            statement.executeUpdate();

            // get Id from result in resultSet
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                id = resultSet.getObject(1);
            }

            connection.commit();
            return id;

        } catch (SQLException | IllegalAccessException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    DBConnectionImpl.close();
                }
                if (statement != null) {
                    statement.close();
                }

                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
    @Override
    public void delete(Object object) {
        String sql = null;
        try {
            sql = createSqlDelete(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.print(sql);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Long id = null;

            // connect with DB
            connection = DBConnectionImpl.getConnection();

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql.toString());
            /* setParameter(statement, parameters); */

            // set param
            Class aClass = object.getClass();
            Field[] fields = aClass.getDeclaredFields();
            int count = 1;
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (fields[i].isAnnotationPresent(Column.class) && fields[i].get(object) != null) {
                    Field field = fields[i];
//                    field.setAccessible(true);
                    statement.setObject(count++, field.get(object));
                }
            }

            System.out.println("statement: " + statement.toString());

            // excute query
            statement.executeUpdate();

            connection.commit();

        } catch (SQLException | IllegalAccessException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }

                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }
    @Override
    public Object insertOneToOne(Object object) {
//		String sql = createSqlInsertOneToOne(object);

        // get all fields
        Class aClass = object.getClass();
        Field[] fields = aClass.getDeclaredFields();

        // get OneToOne Column
        Object oneToOneObj = null;
        Object insertedId = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(JoinColumn.class) && field.isAnnotationPresent(OneToOne.class)) {
                field.setAccessible(true);
                try {
                    oneToOneObj = field.get(object);
                    insertedId = insert(oneToOneObj);
                } catch (IllegalAccessException e) {

                }
            }
        }

        // get fkey
//        Object oneToOneId = null;
//        Class<?> oClass = oneToOneObj.getClass();
//        fields = oClass.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class)) {
//                field.setAccessible(true);
//                try {
//                    oneToOneId = field.get(oneToOneObj);
//                    System.out.println(oneToOneId);
//                } catch (IllegalAccessException e) {
//
//                }
//            }
//        }

        String sql = createSqlInsert(object);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Long id = null;

            // connect with DB
            connection = DBConnectionImpl.getConnection();

            if (connection != null) {
                System.out.println("Ket noi thanh cong");
            }

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            /* setParameter(statement, parameters); */

            // set param
            aClass = object.getClass();
            fields = aClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                int index = i + 1;
                Field field = fields[i];
                field.setAccessible(true);
                if (field.isAnnotationPresent(JoinColumn.class) && field.isAnnotationPresent(OneToOne.class)) {
                    statement.setObject(index, insertedId);
                } else {
                    statement.setObject(index, field.get(object));
                }
            }

            // excute query
            statement.executeUpdate();

            // get Id from result in resultSet
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }

            connection.commit();
            return id;

        } catch (SQLException | IllegalAccessException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    DBConnectionImpl.close();
                }
                if (statement != null) {
                    statement.close();
                }

                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public Object insertOneToMany(Object object) {
        Object parentId = insertV2(object);

        Class classA = object.getClass();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields){
            if(field.isAnnotationPresent(OneToMany.class)){
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<?> classB = (Class<?>) stringListType.getActualTypeArguments()[0];
                String newTableName = createTable(classA, classB);
                String columnA = ((Table) classA.getAnnotation(Table.class)).name();
                String columnB = ((Table) classB.getAnnotation(Table.class)).name();

                try {
                    field.setAccessible(true);
                    Object list = field.get(object);

                    Collection listC = (Collection) list;
                    Iterator iter = listC.iterator();

                    while(iter.hasNext()){
                        BigInteger childId = (BigInteger) insert(iter.next());

                        String sql = "INSERT INTO " + newTableName + "(" + columnA + "_id" + ", " + columnB + "_id" + ") VALUES(" + parentId + ", " + childId + ")";
                        Connection conn = null;
                        Statement stmt = null;

                        conn = DBConnectionImpl.getConnection();

                        // dont commit when occur error and callback (transaction)
                        conn.setAutoCommit(false);

                        stmt = conn.createStatement();

                        stmt.executeUpdate(sql);

                        conn.commit();
                    }
                } catch (IllegalAccessException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return parentId;
    }


    public Object get(Class zClass, Object id){
        String sql = createSqlSelect(zClass);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Object resObject = null;

        try {
            // return int
            resObject = zClass.newInstance();

            // connect with DB
            connection = DBConnectionImpl.getConnection();

            if (connection != null) {
                System.out.println("Ket noi thanh cong");
            }

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            /* setParameter(statement, parameters); */


            statement.setObject(1, id);

            // excute query
            resultSet = statement.executeQuery();

            connection.commit();

            // get resultSet metadata ---> get column name
            ResultSetMetaData rsMetadata = resultSet.getMetaData();

            Field[] fields = zClass.getDeclaredFields();

            if (resultSet.next()){
                for(Field field : fields) {
                    field.setAccessible(true);
                    if(field.isAnnotationPresent(Column.class)){
                        String columnName = field.getAnnotation(Column.class).name();
                        Object columnValue = resultSet.getObject(columnName);
                        field.set(resObject, columnValue);
                    }
                }
            }

        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    DBConnectionImpl.close();
                }
                if (statement != null) {
                    statement.close();
                }

                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }

        return resObject;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static String createSqlDelete(Object object) throws IllegalAccessException {
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
    private static String createSqlUpdate(Object object) throws IllegalAccessException {
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

    private static String createSqlSelect(Class zClass) {
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

    private static String createTable(Class classA, Class classB) {
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
}
