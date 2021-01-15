package main;

import Repository.ISession;
import Repository.SessionImpl;
import dto.*;
import helper.Helper;
import helper.HqlQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {
    public static void main(String[] args) {
        ISession session = new SessionImpl();

//        FresherEntity fresher1 = new FresherEntity("NTSon01");
//        FresherEntity fresher2 = new FresherEntity("NTSon02");
//
//        List<CourseEntity> courses = new LinkedList<>();
//        CourseEntity course1 = new CourseEntity("React");
//        CourseEntity course2 = new CourseEntity("Java");
//        courses.add(course1);
//        courses.add(course2);
//
//        fresher1.setCourses(courses);
//
//        Integer id = (Integer) session.insert(fresher1);
//        System.out.println("Inserted id: " + id);


//        java.util.Date utilDate = new java.util.Date();
//        java.sql.Date now = new java.sql.Date(utilDate.getTime());
//        UserEntity user = new UserEntity("DDTai", "asdf", "ddt@gmail.com", now);
//
//        List<PostEntity> posts = new LinkedList<>();
//        posts.add(new PostEntity("Title 001"));
//        posts.add(new PostEntity("Title 002"));
//
//        user.setListPosts(posts);
//        Integer id = (Integer) session.insert(user);
//        System.out.println("Inserted Id: " + id);



//        UserEntity user = (UserEntity) session.select(UserEntity.class, 68);
//        System.out.println("User: " + user);
//
//        FresherEntity fr = (FresherEntity) session.select(FresherEntity.class, 2);
//        System.out.println("Fresher: " + fr);

//        String query = "select * from dto.FresherEntity";
//        String className = Helper.getClassNameFromQuery(query);
//        Class zClass = Helper.getClassObjectWithClassName(className);
//        HashMap<String, String> mapAttrCol = Helper.getMapAttributeColumn(zClass);
//        System.out.println(mapAttrCol);
        HqlQuery q = new HqlQuery("insert INTO dto.FresherEntity (sfas, adfas) values(dsbgvds,hdghsadf)");
        System.out.println(Helper.getClassNameFromQuery(q.getQuery()).get(0));

//        q.createSql();
//        List<Map<String, Object>> result = q.excuteQuery();
//        System.out.println(result);
//        System.out.println(q.getTargetQuery());

        //System.out.println(rs);





    }
}
