package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.Column;
import annotation.Id;
import annotation.JoinColumn;
import annotation.ManyToOne;
import helper.QueryCreator;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;

public class SimpleInsert {

    QueryCreator query = new QueryCreator();

    public Object insert(Object object) {
        if(object == null) return null;
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
            Class aClass = object.getClass();
            Field[] fields = aClass.getDeclaredFields();
            int count = 1;
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                if(field.isAnnotationPresent(Column.class)){
                    statement.setObject(count++, field.get(object));
                }
                if(field.isAnnotationPresent(ManyToOne.class) && field.isAnnotationPresent(JoinColumn.class)){
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    String refColName = joinColumn.referenceColumnName();

                    // trường được đánh Annotation là ManyToOne
                    Object manyToOneObject = field.get(object);

                    // các fields trong class của đối manyToOneObject
                    Field[] refEntityFields = manyToOneObject.getClass().getDeclaredFields();

                    for (Field field1 : refEntityFields) {
                        field1.setAccessible(true);
                        if(field1.isAnnotationPresent(Column.class)){
                            Column column = field1.getAnnotation(Column.class);
                            if(refColName.equals(column.name())){
                                statement.setObject(count++, field1.get(object));
                            }

                        }
                    }
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
}
