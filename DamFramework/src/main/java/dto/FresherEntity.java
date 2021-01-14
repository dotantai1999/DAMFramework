package dto;

import annotation.*;

import java.util.List;

@Entity
@Table(name = "fresher")
public class FresherEntity {
    @Id
    @Column(name = "fresher_id")
    private Integer fresherId;

    @Column(name = "fresher_name")
    private String fresherName;

    @ManyToMany
    private List<CourseEntity> courses;

    public FresherEntity(String fresherName, List<CourseEntity> courses) {
        this.fresherName = fresherName;
        this.courses = courses;
    }

    public FresherEntity(String fresherName) {
        this.fresherName = fresherName;
    }

    public String getFresherName() {
        return fresherName;
    }

    public void setFresherName(String fresherName) {
        this.fresherName = fresherName;
    }

    public List<CourseEntity> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseEntity> courses) {
        this.courses = courses;
    }

    public FresherEntity() {
    }

    @Override
    public String toString() {
        return "FresherEntity{" +
                "fresherId=" + fresherId +
                ", fresherName='" + fresherName + '\'' +
                ", courses=" + courses +
                '}';
    }
}
