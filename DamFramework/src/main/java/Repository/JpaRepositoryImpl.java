package Repository;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import annotation.*;

public class JpaRepositoryImpl<T> implements IJpaRepository<T> {

    public JpaRepositoryImpl() {

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
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                fields.append(column.name());
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
        String sql = createSqlInsert(object);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Object id = null;

            // connect with DB
            connection = EntityManagerFactoryImpl.getConnection();

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
        return null;
    }

    @Override
    public void update(Object object) {

    }

    @Override
    public void delete(Object object) {
        String sql = createSqlDelete(object);
        System.out.print(sql);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Long id = null;

            // connect with DB
            connection = EntityManagerFactoryImpl.getConnection();

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql.toString());
            /* setParameter(statement, parameters); */

            // set param
            Class aClass = object.getClass();
            Field[] fields = aClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(Column.class) && fields[i].isAnnotationPresent(Id.class)) {
                    Field field = fields[i];
                    field.setAccessible(true);
                    statement.setObject(1, field.get(object));
                }
            }

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
            if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(OneToOne.class)) {
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
            connection = EntityManagerFactoryImpl.getConnection();

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
                if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(OneToOne.class)) {
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
        return null;
    }

//	private String createSqlInsertOneToOne(Object object){
//
//		return
//	}

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String createSqlDelete(Object object) {
        String tableName = "";
        Class zClass = object.getClass();

        if (zClass.isAnnotationPresent(Entity.class) && zClass.isAnnotationPresent(Table.class)) {
            Table tableClass = (Table) zClass.getAnnotation(Table.class);
            tableName = tableClass.name();
        }

        Field[] fields = zClass.getDeclaredFields();

        Column column = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class)) {
                column = field.getAnnotation(Column.class);
            }
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + column.name() + " = ?";
        return sql;
    }
}
