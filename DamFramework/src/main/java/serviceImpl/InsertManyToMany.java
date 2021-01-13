package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.ManyToMany;
import annotation.OneToMany;
import annotation.Table;
import helper.QueryCreator;
import service.Insertor;
import sun.java2d.pipe.SpanShapeRenderer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

public class InsertManyToMany implements Insertor {
    QueryCreator query = new QueryCreator();

    @Override
    public Object insert(Object object) {
        SimpleInsert si = new SimpleInsert();
        Object idA = si.insert(object);

        Class classA = object.getClass();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields){
            if(field.isAnnotationPresent(ManyToMany.class)){
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<?> classB = (Class<?>) stringListType.getActualTypeArguments()[0];
                String newTableName = query.createTable(classA, classB);
                String columnA = ((Table) classA.getAnnotation(Table.class)).name() + "_id";
                String columnB = ((Table) classB.getAnnotation(Table.class)).name() + "_id";

                try {
                    field.setAccessible(true);
                    Object list = field.get(object);

                    Collection listC = (Collection) list;
                    Iterator iter = listC.iterator();

                    while(iter.hasNext()){
                        Object idB = null;
                        Object listItem = iter.next();

                        idB = Insertor.insertedId(listItem);
                        if(idB == null){
                            idB = si.insert(listItem);
                        }

                        String sql = "INSERT INTO " + newTableName + "(" + columnA + ", " + columnB + ") VALUES(" + idA + ", " + idB + ")";
                        Connection conn = null;
                        Statement stmt = null;

                        conn = DBConnectionImpl.getConnection();

                        // dont commit when occur error and callback (transaction)
                        conn.setAutoCommit(false);

                        stmt = conn.createStatement();

                        stmt.executeUpdate(sql);

                        conn.commit();

                        DBConnectionImpl.close();
                    }
                } catch (IllegalAccessException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return idA;
    }
}
