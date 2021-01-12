package helper;

import annotation.Column;
import annotation.ManyToOne;
import annotation.OneToMany;
import annotation.OneToOne;
import service.Insertor;
import serviceImpl.InsertManyToOne;
import serviceImpl.InsertOneToMany;
import serviceImpl.InsertOneToOne;
import serviceImpl.SimpleInsert;

import java.lang.reflect.Field;

public class InsertorFactory {
    private InsertorFactory() {
    }

    public static final Insertor getInsertor(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field : fields) {
                if(field.isAnnotationPresent(OneToOne.class)){
                    return new InsertOneToOne();
                }
                if(field.isAnnotationPresent(ManyToOne.class)){
                    return new InsertManyToOne();
                }
                if(field.isAnnotationPresent(OneToMany.class)){
                    return new InsertOneToMany();
                }
        }
        return new SimpleInsert();
    }
}

