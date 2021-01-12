package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.Column;
import annotation.Id;
import annotation.JoinColumn;
import annotation.OneToOne;
import helper.QueryCreator;
import service.Insertor;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;

public class InsertOneToOne implements Insertor {

    QueryCreator query = new QueryCreator();

    @Override
    public Object insert(Object object) {
        SimpleInsert si = new SimpleInsert();

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
                    insertedId = si.insert(oneToOneObj);
                } catch (IllegalAccessException e) {

                }
            }
        }

        String sql = query.createSqlInsertV2(object);
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
            aClass = object.getClass();
            fields = aClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                int index = i + 1;
                Field field = fields[i];
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)){
                    statement.setObject(index, field.get(object));
                }
                if (field.isAnnotationPresent(JoinColumn.class) && field.isAnnotationPresent(OneToOne.class)) {
                    statement.setObject(index, insertedId);
                }
            }

            // excute query
            statement.executeUpdate();

            // get Id from result in resultSet
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                id = resultSet.getObject(1);
            }

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
}
