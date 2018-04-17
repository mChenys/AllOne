//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package blog.csdn.net.mchenys.common.okhttp2.x.listener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;

import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;

/**
 * 请求结果回调接口 如果创建RequestCallBackHandler在主线程,则回调在UI线程,如果创建在子线程,则回调在子线程
 */
public abstract class RequestCallBackHandler implements RequestCallBack {
    public Handler handler;
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = -1;
    //如果请求的Activity/FragmentActivity已经finish则不回调结果
    private WeakReference<Context> context;
    private WeakReference<Fragment> fragment;
    private WeakReference<android.support.v4.app.Fragment> v4Fragment;

    public RequestCallBackHandler() {
        if (Looper.myLooper() != null) {
            handler = new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message msg) {
                    RequestCallBackHandler.this.handleMessage(msg);
                }
            };
        }
    }

    public RequestCallBackHandler(Context context) {
        this();
        this.context = new WeakReference(context);
    }

    public RequestCallBackHandler(Fragment fragment) {
        this();
        this.fragment = new WeakReference(fragment);
    }


    public RequestCallBackHandler(android.support.v4.app.Fragment fragment) {
        this();
        this.v4Fragment = new WeakReference(fragment);
    }

    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                Object[] result = (Object[]) msg.obj;
                this.onResponse(result[0], (OkResponse) result[1]);
                break;
            case FAILURE_MESSAGE:
                Exception e = (Exception) msg.obj;
                this.onFailure(e);
        }
    }

    protected Message obtainMessage(int what, Object obj) {
        Message msg = null;
        if (handler != null) {
            msg = handler.obtainMessage(what, obj);
        } else {
            msg = new Message();
            msg.what = what;
            msg.obj = obj;
        }
        return msg;
    }

    protected void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    public abstract void onFailure(Exception e);

    public abstract Object doInBackground(OkResponse response);

    public abstract void onResponse(Object obj, OkResponse response);

    @Override
    public void onReceiveFailure(Exception e) {
        this.sendMessage(obtainMessage(FAILURE_MESSAGE, e));
    }

    @Override
    public void onReceiveResponse(OkResponse response) {
        if (isContextEffective()) {
            Object obj = this.doInBackground(response);
            sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{obj, response}));
        }
    }

    private boolean isContextEffective() {
        if (this.fragment != null && this.fragment.get() != null) {
            if (((Fragment) this.fragment.get()).getActivity() == null || ((Fragment) this.fragment.get()).getActivity().isFinishing()) {
                return false;
            }
        } else if (this.v4Fragment != null && this.v4Fragment.get() != null) {
            if (((android.support.v4.app.Fragment) this.v4Fragment.get()).getActivity() == null || ((android.support.v4.app.Fragment) this.v4Fragment.get()).getActivity().isFinishing()) {
                return false;
            }
        } else if (this.context != null && this.context.get() != null) {
            if (this.context.get() instanceof Activity) {
                if (((Activity) this.context.get()).isFinishing()) {
                    return false;
                }
            } else if (this.context.get() instanceof FragmentActivity && ((FragmentActivity) this.context.get()).isFinishing()) {
                return false;
            }
        }

        return true;
    }
}
