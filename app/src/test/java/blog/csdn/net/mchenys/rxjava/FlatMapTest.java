package blog.csdn.net.mchenys.rxjava;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by mChenys on 2019/6/4.
 */

public class FlatMapTest {
    List<Student> mStudent = new ArrayList<>();

    @Before
    public void init(){
        List<Student.Course> courseList = new ArrayList<>();
        courseList.add(new Student.Course("语文"));
        courseList.add(new Student.Course("英语"));
        courseList.add(new Student.Course("数学"));

        mStudent.add(new Student("小明", courseList));
        mStudent.add(new Student("小王", courseList));
        mStudent.add(new Student("小红", courseList));
        mStudent.add(new Student("小城", courseList));

    }

    @Test
    public void demo1(){
        //输出所有学生对应的课程
        Observable.from(mStudent).flatMap(new Func1<Student, Observable<Student.Course>>() {
            @Override
            public Observable<Student.Course> call(Student student) {
                //封装成新的Observable给观察者处理
                System.out.println("==============="+student.name);
                return Observable.from(student.courseList);
            }
        }).subscribe(new Action1<Student.Course>() {
            @Override
            public void call(Student.Course course) {
                System.out.println(course.name);
            }
        });
    }
}
