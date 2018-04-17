package blog.csdn.net.mchenys.common.okhttp2.x.listener;


import blog.csdn.net.mchenys.common.okhttp2.x.model.OkResponse;

/**
 * 请求结果回调接口 在子线程中回调
 */
public interface RequestCallBack {
    void onReceiveFailure(Exception e);

    void onReceiveResponse(OkResponse response);
}