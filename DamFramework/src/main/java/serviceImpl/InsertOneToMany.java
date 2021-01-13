package serviceImpl;

import Repository.DBConnectionImpl;
import annotation.ManyToMany;
import annotation.ManyToOne;
import annotation.OneToMany;
import annotation.Table;
import helper.QueryCreator;
import service.Insertor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

public class InsertOneToMany implements Insertor {
    QueryCreator query = new QueryCreator();

    public Object insert(Object object) {
        SimpleInsert si = new SimpleInsert();
        Object idA = si.insert(object);

        Class classA = object.getClass();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields){
            if(field.isAnnotationPresent(OneToMany.class)){
                try {
                    field.setAccessible(true);
                    Object list = field.get(object);

                    Collection listC = (Collection) list;
                    Iterator iter = listC.iterator();

                    while(iter.hasNext()){
                        Object listItem = iter.next();

                        Field[] bFields = listItem.getClass().getDeclaredFields();
                        for(Field bField : bFields) {
                            if(bField.isAnnotationPresent(ManyToOne.class)){
                                bField.setAccessible(true);
                                bField.set(listItem, object);
                            }
                        }

                        Insertor insertor = new InsertManyToOne();
                        Object listItemId = insertor.insert(listItem);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return idA;
    }
}
