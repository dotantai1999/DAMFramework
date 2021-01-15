package dto;

import annotation.*;

import java.util.List;

@Entity
@Table(name = "course")
public class CourseEntity {
    @Id
    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "course_title")
    private String courseTitle;

    @ManyToMany
    private List<FresherEntity> freshers;

    public CourseEntity(String courseTitle, List<FresherEntity> freshers) {
        this.courseTitle = courseTitle;
        this.freshers = freshers;
    }

    public CourseEntity(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public List<FresherEntity> getFreshers() {
        return freshers;
    }

    public void setFreshers(List<FresherEntity> freshers) {
        this.freshers = freshers;
    }

    public CourseEntity() {
    }

    @Override
    public String toString() {
        return "CourseEntity{" +
                "courseId=" + courseId +
                ", courseTitle='" + courseTitle + '\'' +
                ", freshers=" + freshers +
                '}';
    }
}
