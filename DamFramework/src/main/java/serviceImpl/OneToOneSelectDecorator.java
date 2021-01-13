package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.JoinColumn;
import annotation.OneToOne;
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
                QueryCreator query = new QueryCreator();
                String sql = query.createOneToOneSelectQuery(entity, field);
                System.out.println("join query: " + sql);

                Connection connection = null;
                Statement statement = null;
                ResultSet resultSet = null;

                Object resObject = null;

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

                    resObject = Helper.mapEntityDataWithResultSet(field.getType(), resultSet);
                    field.set(entity, resObject);
                }catch (IllegalAccessException | SQLException e){
                    e.printStackTrace();
                }
            }
        }

        return entity;
    }
}
