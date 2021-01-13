package main;

import Repository.ISession;
import Repository.SessionImpl;
import dto.*;

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



        UserEntity user = (UserEntity) session.select(UserEntity.class, 68);
        System.out.println("User: " + user);

    }
}
