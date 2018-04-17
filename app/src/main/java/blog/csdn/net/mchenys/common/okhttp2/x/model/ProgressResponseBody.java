package blog.csdn.net.mchenys.common.okhttp2.x.model;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 包装的响体，处理进度
 */
public class ProgressResponseBody extends ResponseBody {
    /**
     * 响应体进度回调接口，比如用于文件下载中
     */
    public interface ProgressResponseListener {
        void onResponseProgress(long bytesRead, long contentLength, boolean done);
    }

    //实际的待包装响应体
    private ResponseBody responseBody;
    //进度回调接口
    private ProgressResponseListener progressListener;
    //包装完成的BufferedSource
    private BufferedSource bufferedSource;

    public void setProgressListener(ProgressResponseListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * 构造函数，赋值
     *
     * @param responseBody 待包装的响应体
     */
    public ProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }


    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return responseBody.contentLength();
    }

    /**
     * 重写进行包装source
     *
     * @return BufferedSource
     * @throws IOException 异常
     */
    @Override
    public BufferedSource source() throws IOException {
        if (bufferedSource == null) {
            //包装
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1
                progressListener.onResponseProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}