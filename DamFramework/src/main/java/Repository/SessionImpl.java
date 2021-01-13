package Repository;

import annotation.Column;
import helper.InsertorFactory;
import service.Selector;
import serviceImpl.OneToOneSelectDecorator;
import serviceImpl.SimpleSelector;

import java.lang.reflect.Field;
import java.sql.*;

public class SessionImpl<T> extends ISession<T> {

    public SessionImpl() {
    }

    public Object insert(Object object) {
        this.insertor = InsertorFactory.getInsertor(object);
        return insertor.insert(object);
    }

    @Override
    public Object update(Object object) {
        String sql = "";
        try {
            sql = query.createSqlUpdate(object);
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
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
            sql = query.createSqlDelete(object);
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
            statement = connection.prepareStatement(sql);
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
    public Object get(Class zClass, Object id) {
        String sql = query.createSqlSelect(zClass);

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
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            /* setParameter(statement, parameters); */


            statement.setObject(1, id);

            // excute query
            resultSet = statement.executeQuery();

            connection.commit();

            // get resultSet metadata ---> get column name
            ResultSetMetaData rsMetadata = resultSet.getMetaData();

            Field[] fields = zClass.getDeclaredFields();

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

    @Override
    public Object select(Class zClass, Object id) {
        Selector selector = new SimpleSelector();
        Selector wrapSelector = new OneToOneSelectDecorator(selector);
        return wrapSelector.select(zClass, id);
    }


}
