package blog.csdn.net.mchenys.common.download;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;
import blog.csdn.net.mchenys.common.utils.HttpUtils;
import blog.csdn.net.mchenys.common.utils.IOUtils;

/**
 * 下载管理类,单例模式+观察者设计模式(DownloadManager为被观察的对象)
 * 观察者设计模式本质上也是接口回调,区别在于接口回调是1对1的回调.而观察者和被观察者则是1对多的回调
 * Created by mChenys on 2015/11/28.
 */
public class DownloadManager {

    private static final DownloadManager mInstance = new DownloadManager();
    private List<DownloadObserver> mObservers = new ArrayList<>();
    //6种下载状态- 未下载 - 等待下载 - 正在下载 - 下载暂停 - 下载失败 - 下载成功
    public static final int STATE_UNDO = 0;//未下载
    public static final int STATE_WAITING = 1;//等待下载
    public static final int STATE_DOWNLOADING = 2;//正在下载
    public static final int STATE_PAUSE = 3;//下载暂停
    public static final int STATE_ERROR = 4;//下载失败
    public static final int STATE_SUCCESS = 5;//下载成功

    //下载对象的集合, 使用线程安全的hashmap
    ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<>();
    //下载任务的集合, 使用线程安全的hashmap
    ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();

    private DownloadManager() {

    }

    public static DownloadManager getDownloadManager() {
        return mInstance;
    }

    /**
     * 注册观察者
     *
     * @param observer
     */
    public synchronized void addDownloadObserver(DownloadObserver observer) {
        if (null != observer && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    /**
     * 注销观察者
     *
     * @param observer
     */
    public synchronized void removeDownloadObserver(DownloadObserver observer) {
        if (null != observer && mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    /**
     * 通知观察者下载状态发生了变化
     */
    public synchronized void notifyDownloadStateChanged(DownloadInfo info) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadStateChanged(info);
        }
    }

    /**
     * 通知观察者,下载进度发生了变化
     */
    public synchronized void notifyDownloadProgressChanged(DownloadInfo info) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadProgressChanged(info);
        }
    }

    /**
     *  根据id获取下载对象
     * @param info
     * @return
     */
    public DownloadInfo getDownloadInfo(DownloadInfo info) {
        if (null != info) {
            return mDownloadInfoMap.get(info.id);
        }
        return null;
    }

    /**
     * 观察者接口
     */
    public interface DownloadObserver {
        // 下载状态发生变化
        void onDownloadStateChanged(DownloadInfo info);

        // 下载进度发生变化
        void onDownloadProgressChanged(DownloadInfo info);
    }

    /**
     * 执行下载任务
     *
     * @param df
     */
    public synchronized void download(DownloadInfo df) {
        if (null == df) return;
        // 断点续传, 首先要判断是否之前下载过, 如果下载过的话,就应该使用之前的那个对象来进行下载
        DownloadInfo info = mDownloadInfoMap.get(df.id);
        if (null == info) {
            // 第一次下载
            info = df;
        }
        info.currentState = STATE_WAITING; //将下载状态先改成等待下载
        notifyDownloadStateChanged(info); //通知观察者
        mDownloadInfoMap.put(info.id, info);//保存当前下载的对象

        //开始下载
        DownloadTask task = new DownloadTask(info);
        ThreadManager.getThreadPool().execute(task);
        mDownloadTaskMap.put(info.id, task); //保存当前的下载任务
    }



    /**
     * 下载任务类
     */
    private class DownloadTask implements Runnable {
        DownloadInfo info;

        public DownloadTask(DownloadInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            // 访问网络,下载文件
            // 状态切换为下载
            info.currentState = STATE_DOWNLOADING;
            notifyDownloadStateChanged(info);

            //初始化下载文件对象
            File file = new File(info.path);
            OkResponse response = null;
            // 判断文件是否存在, 决定是否第一次下载还是继续下载; 如果文件长度不合法也要重新下载
            if (!file.exists() || file.length() != info.currentPos
                    || info.currentPos == 0) {
                // 第一次下载
                file.delete();// 删除废弃的文件(文件不存在的话也能删除,不会出异常)
                info.currentPos = 0;
                //开始下载
                Map<String, String> bodyMap = new HashMap<>();
                bodyMap.put("name", info.name);
                response = HttpUtils.getResponse(true,info.downloadUrl,null,bodyMap);
            } else {
                // 继续下载
                // range参数表示的是从文件的哪个位置开始继续下载
                Map<String, String> bodyMap = new HashMap<>();
                bodyMap.put("name", info.name);
                bodyMap.put("range", String.valueOf(file.length()));
                response = HttpUtils.getResponse(true,info.downloadUrl,null,bodyMap);
            }
            if (null != response && null != response.getInputStream()) {
                //获取服务器的输出流
                InputStream in = response.getInputStream();
                FileOutputStream out = null;
                try {
                    // 参2:表示如果文件已存在,是否要追加在当前文件上
                    out = new FileOutputStream(file, true);
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    // 在下载过程中,只有状态是下载,才继续读文件, 否则要立即停止
                    while ((len = in.read(buffer)) != -1
                            && info.currentState == STATE_DOWNLOADING) {
                        out.write(buffer, 0, len);
                        // FileOutputStream对flush是空实现,字节流是直接写到硬盘的
                        // out.flush();
                        //记录当前的下载位置
                        info.currentPos += len;
                        //通知观察者, 下载进度更新
                        notifyDownloadProgressChanged(info);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(out);
                    IOUtils.close(in);
                }
                // 下载结束,校验文件的完整性
                if (file.length() == info.size) {
                    //下载成功
                    info.currentState = STATE_SUCCESS;
                    notifyDownloadStateChanged(info); //通知观察者,下载成功
                } else if (info.currentState == STATE_PAUSE) {
                    notifyDownloadStateChanged(info); //暂停下载,通知观察者
                } else {
                    //下载失败,例如网络中断了
                    file.delete();//移除无效文件
                    info.currentState = STATE_ERROR;
                    info.currentPos = 0;
                    //通知观察者,下载失败
                    notifyDownloadStateChanged(info);
                }
            } else {
                //下载失败
                file.delete();//移除无效文件
                info.currentState = STATE_ERROR;
                info.currentPos = 0;
                //通知观察者,下载失败
                notifyDownloadStateChanged(info);
            }
            // 下载结束
            // 从下载任务集合中移除下载任务
            mDownloadTaskMap.remove(info.id);
        }
    }
    /**
     * 暂停下载
     *
     * @param df
     */
    public synchronized void pause(DownloadInfo df) {
        DownloadInfo info = mDownloadInfoMap.get(df.id);
        if (info != null) {
            // 只有在等待下载或者正在下载时,才可以暂停
            if (info.currentState == STATE_WAITING
                    || info.currentState == STATE_DOWNLOADING) {
                DownloadTask task = mDownloadTaskMap.get(info.id);
                if (task != null) {
                    // 停止任务, 从线程池中移除当前任务
                    ThreadManager.getThreadPool().cancel(task);
                }

                // 更新下载状态
                info.currentState = STATE_PAUSE;
                // 通知下载状态变化
                notifyDownloadStateChanged(info);
            }
        }
    }

    /**
     * 安装apk
     * @param info
     */
    public synchronized void install(Context context,DownloadInfo info) {
        if (null == info) return;
        //只有下载成功了才可以安装
        if (info.currentState == STATE_SUCCESS && info.path.endsWith(".apk")) {
            // 跳到系统的安装页面进行安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + info.path)
                    , "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
}
