package blog.csdn.net.mchenys.rxjava;

import org.junit.Test;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by mChenys on 2019/6/4.
 */

public class ActionTest {

    @Test
    public void demo1(){
        //Action1表示处理的参数只有1个,一共有Action0~ActionN
        Observable.just("one").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
    }

}
