package blog.csdn.net.mchenys.rxjava;

import java.util.List;

/**
 * Created by mChenys on 2019/6/4.
 */

public class Student {
    public String name;
    public List<Course> courseList;

    public Student(String name, List<Course> courseList) {
        this.name = name;
        this.courseList = courseList;
    }

    public static class Course {
        public String name;

        public Course(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Course{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", courseList=" + courseList +
                '}';
    }
}
