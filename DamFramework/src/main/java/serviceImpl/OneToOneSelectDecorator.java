package serviceImpl;

import Repository.DBConnectionImpl;
import Repository.ISession;
import Repository.SessionImpl;
import annotation.JoinColumn;
import annotation.OneToOne;
import annotation.Table;
import helper.Helper;
import helper.QueryCreator;
import service.Selector;

import java.lang.reflect.Field;
import java.sql.*;

public class OneToOneSelectDecorator extends SelectDecorator {

    public OneToOneSelectDecorator(Selector wrappeeSelector) {
        super(wrappeeSelector);
    }

    @Override
    public Object select(Class entityClass, Object id) {
        Object entity = wrappeeSelector.select(entityClass, id);
        return additionalSelect(entity, id);
    }

    @Override
    public Object additionalSelect(Object entity, Object entityId) {
        Field[] fields = entity.getClass().getDeclaredFields();

        for(Field field : fields){
            if(field.isAnnotationPresent(OneToOne.class) && field.isAnnotationPresent(JoinColumn.class)){
                field.setAccessible(true);
                String colName = field.getAnnotation(JoinColumn.class).name();
                QueryCreator query = new QueryCreator();
                String sql = query.createGetRefColValueQuery(entity, field);

                Connection connection = null;
                Statement statement = null;
                ResultSet resultSet = null;

                Object refColValue = null;

                try {
                    // connect with DB
                    connection = DBConnectionImpl.getConnection();

                    if (connection != null) {
                        System.out.println("Ket noi thanh cong");
                    }

                    // dont commit when occur error and callback (transaction)
                    connection.setAutoCommit(false);

                    statement = connection.prepareStatement(sql);

                    // excute query
                    resultSet = statement.executeQuery(sql);

                    connection.commit();

                    if(resultSet.next()){
                        refColValue = resultSet.getObject(colName);
                    }

                    ISession innerSession = new SessionImpl();
                    Object oneToOneObject = innerSession.select(field.getType(), refColValue);
                    field.set(entity, oneToOneObject);

                    return entity;
                }catch (SQLException | IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }

        return entity;
    }


//    public Object additionalSelect(Object entity, Object entityId) {
//        Field[] fields = entity.getClass().getDeclaredFields();
//
//        for(Field field : fields){
//            if(field.isAnnotationPresent(OneToOne.class) && field.isAnnotationPresent(JoinColumn.class)){
//                field.setAccessible(true);
//                QueryCreator query = new QueryCreator();
//                String sql = query.createOneToOneSelectQuery(entity, field);
//                System.out.println("join query: " + sql);
//
//                Connection connection = null;
//                Statement statement = null;
//                ResultSet resultSet = null;
//
//                Object resObject = null;
//
//                try {
//                    // connect with DB
//                    connection = DBConnectionImpl.getConnection();
//
//                    if (connection != null) {
//                        System.out.println("Ket noi thanh cong");
//                    }
//
//                    // dont commit when occur error and callback (transaction)
//                    connection.setAutoCommit(false);
//
//                    statement = connection.prepareStatement(sql);
//
//                    // excute query
//                    resultSet = statement.executeQuery(sql);
//
//                    connection.commit();
//
//                    resObject = Helper.mapEntityDataWithResultSet(field.getType(), resultSet);
//                    field.set(entity, resObject);
//                }catch (IllegalAccessException | SQLException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return entity;
//    }
}
