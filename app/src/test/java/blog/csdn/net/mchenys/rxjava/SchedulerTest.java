package blog.csdn.net.mchenys.rxjava;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by mChenys on 2019/6/4.
 */

public class SchedulerTest {


    @Test
    public void demo1() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for(int i=0;i<10;i++){
                    subscriber.onNext(i+"");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).//会报错,这是由于jdk没有android.os.Looper这个类及相关依赖。
                subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
    }
}
