package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.Column;
import helper.QueryCreator;
import service.Selector;

import java.lang.reflect.Field;
import java.sql.*;

public class SimpleSelector implements Selector {
    QueryCreator query = new QueryCreator();

    @Override
    public Object select(Class entityClass, Object id) {
        String sql = query.createSqlSelect(entityClass);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Object resObject = null;

        try {
            // return int
            resObject = entityClass.newInstance();

            // connect with DB
            connection = DBConnectionImpl.getConnection();

            if (connection != null) {
                System.out.println("Ket noi thanh cong");
            }

            // dont commit when occur error and callback (transaction)
            connection.setAutoCommit(false);

            // create statement
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            /* setParameter(statement, parameters); */


            statement.setObject(1, id);

            // excute query
            resultSet = statement.executeQuery();

            connection.commit();

            Field[] fields = entityClass.getDeclaredFields();

            if (resultSet.next()) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Column.class)) {
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
        }
//        finally {
//            try {
//                if (connection != null) {
//                    DBConnectionImpl.close();
//                }
//                if (statement != null) {
//                    statement.close();
//                }
//
//                if (resultSet != null) {
//                    resultSet.close();
//                }
//
//            } catch (SQLException e2) {
//                e2.printStackTrace();
//            }
//        }

        return resObject;
    }
}
