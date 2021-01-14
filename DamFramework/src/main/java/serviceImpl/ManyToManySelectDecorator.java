package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.ManyToMany;
import annotation.Table;
import helper.QueryCreator;
import service.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class ManyToManySelectDecorator extends SelectDecorator{

    public ManyToManySelectDecorator(Selector wrappeeSelector) {
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

        for(Field field : fields) {
            if (field.isAnnotationPresent(ManyToMany.class)) {
                field.setAccessible(true);

                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<?> manyToManyClass = (Class<?>) stringListType.getActualTypeArguments()[0];

                QueryCreator query = new QueryCreator();
                String sql = query.createManyToManyListIdQuery(entity, field);

                Connection connection = null;
                Statement statement = null;
                ResultSet rs = null;

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
                    rs = statement.executeQuery(sql);

                    LinkedList<Object> listObjectId = new LinkedList<>();

                    while (rs.next()) {
                        listObjectId.add(rs.getObject(1));
                    }

                    LinkedList<Object> listManyToManyObject = new LinkedList<>();
                    for (Object objectId : listObjectId) {
                        SimpleSelector ss = new SimpleSelector();
                        Object oneToManyObject = ss.select(manyToManyClass, objectId);
                        listManyToManyObject.add(oneToManyObject);
                    }

                    connection.commit();

                    field.set(entity, listManyToManyObject);

                    return entity;
                } catch (SQLException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
