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

public class MapTest {

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
        Observable.just(1,2,3,4).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                //通过map将参数int转成string返回
                return String.valueOf(integer);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
    }
    @Test
    public void demo2(){
        //打印出一组学生的名字
       Observable.from(mStudent).map(new Func1<Student, String>() {
           @Override
           public String call(Student student) {
               return student.name;
           }
       }).subscribe(new Action1<String>() {
           @Override
           public void call(String s) {
               System.out.println(s);
           }
       });
    }


}
