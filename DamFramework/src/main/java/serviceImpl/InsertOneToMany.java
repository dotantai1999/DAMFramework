package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.OneToMany;
import annotation.Table;
import helper.QueryCreator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

public class InsertOneToMany {
    QueryCreator query = new QueryCreator();

    public Object insertOneToMany(Object object) {
        SimpleInsert si = new SimpleInsert();
        Object parentId = si.insert(object);

        Class classA = object.getClass();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields){
            if(field.isAnnotationPresent(OneToMany.class)){
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<?> classB = (Class<?>) stringListType.getActualTypeArguments()[0];
                String newTableName = query.createTable(classA, classB);
                String columnA = ((Table) classA.getAnnotation(Table.class)).name();
                String columnB = ((Table) classB.getAnnotation(Table.class)).name();

                try {
                    field.setAccessible(true);
                    Object list = field.get(object);

                    Collection listC = (Collection) list;
                    Iterator iter = listC.iterator();

                    while(iter.hasNext()){
                        BigInteger childId = (BigInteger) si.insert(iter.next());

                        String sql = "INSERT INTO " + newTableName + "(" + columnA + "_id" + ", " + columnB + "_id" + ") VALUES(" + parentId + ", " + childId + ")";
                        Connection conn = null;
                        Statement stmt = null;

                        conn = DBConnectionImpl.getConnection();

                        // dont commit when occur error and callback (transaction)
                        conn.setAutoCommit(false);

                        stmt = conn.createStatement();

                        stmt.executeUpdate(sql);

                        conn.commit();
                    }
                } catch (IllegalAccessException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return parentId;
    }
}
