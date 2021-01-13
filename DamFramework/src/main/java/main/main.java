package main;

import Repository.ISession;
import Repository.SessionImpl;
import dto.*;

import java.util.LinkedList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        ISession session = new SessionImpl();

        FresherEntity fresher1 = new FresherEntity("NTSon01");
        FresherEntity fresher2 = new FresherEntity("NTSon02");

        List<CourseEntity> courses = new LinkedList<>();
        CourseEntity course1 = new CourseEntity("React");
        CourseEntity course2 = new CourseEntity("Java");
        courses.add(course1);
        courses.add(course2);

        fresher1.setCourses(courses);

        Integer id = (Integer) session.insert(fresher1);
        System.out.println("Inserted id: " + id);
    }
}
