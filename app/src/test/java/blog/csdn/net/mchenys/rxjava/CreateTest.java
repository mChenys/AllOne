package blog.csdn.net.mchenys.rxjava;

import org.junit.Test;

import java.io.Serializable;

import rx.Observable;
import rx.Subscriber;


/**
 * Created by mChenys on 2019/6/4.
 */

public class CreateTest {

    @Test
    public void demo1() {
        //通过create方法创建
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //注意方法的调用顺序,如果onCompleted写在最前面,则onNext不会回调
                subscriber.onStart();
                subscriber.onNext("demo1");
                subscriber.onCompleted();
            }
        });
        //添加订阅
        observable.subscribe(new Subscriber<String>() {
            @Override
            public void onStart() {
                System.out.println("onStart");
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError");
            }

            @Override
            public void onNext(String s) {
                System.out.println("s=" + s);
            }
        });
    }

    @Test
    public void demo2() {
        //通过just方法创建,简化书写,需要预先将参数设置好
        Observable.just("one", 2, "three",4,"five").subscribe(new Subscriber<Serializable>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Serializable serializable) {
                //将上面的参数依次输出
                if (serializable instanceof String) {
                    System.out.println("字符串:" + serializable);
                } else if (serializable instanceof Number) {
                    System.out.println("数字:" + serializable);
                }
            }
        });
    }

    @Test
    public  void demo3(){
        //通过from方法创建,简化书写,需要传入任意类型的数组
        Observable.from(new Object[]{"one", 2, "three",4,"five"}).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                //将数组中的元素一次输出
                if (o instanceof String) {
                    System.out.println("字符串:" + o);
                } else if (o instanceof Number) {
                    System.out.println("数字:" + o);
                }
            }
        });
    }
}
