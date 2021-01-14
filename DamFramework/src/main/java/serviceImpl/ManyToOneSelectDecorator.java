package serviceImpl;

import Repository.DBConnectionImpl;
import Repository.ISession;
import Repository.SessionImpl;
import annotation.JoinColumn;
import annotation.ManyToOne;
import helper.QueryCreator;
import service.Selector;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManyToOneSelectDecorator extends SelectDecorator{

    public ManyToOneSelectDecorator(Selector wrappeeSelector) {
        super(wrappeeSelector);
    }

    @Override
    public Object select(Class entityClass, Object id) {
        Object entity = wrappeeSelector.select(entityClass, id);
        return additionalSelect(entity, id);
    }

    @Override
    public Object additionalSelect(Object entity, Object id) {
        Field[] fields = entity.getClass().getDeclaredFields();

        for(Field field : fields){
            if(field.isAnnotationPresent(ManyToOne.class) && field.isAnnotationPresent(JoinColumn.class)){
                String colName = field.getAnnotation(JoinColumn.class).name();
                field.setAccessible(true);

                QueryCreator query = new QueryCreator();
                String sql = query.createGetManyToOneObjectIdQuery(entity, field);

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

                    statement = connection.createStatement();

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

        return null;
    }
}
