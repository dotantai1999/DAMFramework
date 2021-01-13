package helper;

import annotation.*;
import service.Insertor;
import serviceImpl.*;

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
                if(field.isAnnotationPresent(ManyToMany.class)){
                    return new InsertManyToMany();
                }
        }
        return new SimpleInsert();
    }
}

