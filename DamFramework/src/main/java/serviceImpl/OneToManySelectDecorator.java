package serviceImpl;

import Repository.DBConnectionImpl;
import Repository.ISession;
import Repository.SessionImpl;
import annotation.*;
import helper.QueryCreator;
import service.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class OneToManySelectDecorator extends SelectDecorator{

    public OneToManySelectDecorator(Selector wrappeeSelector) {
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
            if(field.isAnnotationPresent(OneToMany.class)){
                field.setAccessible(true);

                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<?> classB = (Class<?>) stringListType.getActualTypeArguments()[0];
                QueryCreator query = new QueryCreator();

                String sql = query.createGetListOneToManyId(entity, classB);

                if(sql == null) continue;

                Connection connection = null;
                Statement statement = null;
                ResultSet rs = null;

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
                    rs = statement.executeQuery(sql);

                    LinkedList<Object> listObjectId = new LinkedList<>();

                    while(rs.next()){
                        listObjectId.add(rs.getObject(1));
                    }

                    LinkedList<Object> listOneToManyObject = new LinkedList<>();
                    for(Object objectId : listObjectId){
                        ISession innerSession = new SessionImpl();
                        Object oneToManyObject = innerSession.select(classB, objectId);
                        listOneToManyObject.add(oneToManyObject);
                    }

                    connection.commit();

                    field.set(entity, listOneToManyObject);

                    return entity;
                }catch (SQLException | IllegalAccessException e){
                    e.printStackTrace();
                }finally {
                    if(rs != null)
                        try {rs.close();} catch (SQLException e){e.printStackTrace();}
                    if(statement!= null)
                        try {statement.close();} catch (SQLException e){e.printStackTrace();}
                    if(connection != null)
                        try {connection.close();} catch (SQLException e){e.printStackTrace();}
                }
            }
        }

        return entity;
    }
}
