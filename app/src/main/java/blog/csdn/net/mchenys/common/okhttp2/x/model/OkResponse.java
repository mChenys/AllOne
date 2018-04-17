package blog.csdn.net.mchenys.common.okhttp2.x.model;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 包装的请求结果
 */
public class OkResponse {
    private String result;
    private Map<String, List<String>> headers;
    private int code;
    private int responseType;
    private InputStream inputStream;

    public OkResponse(String result, Map<String, List<String>> headers, int code, int responseType) {
        this.result = result;
        this.headers = headers;
        this.code = code;
        this.responseType = responseType;
    }

    public OkResponse(String result, Map<String, List<String>> headers, int code, InputStream inputStream) {
        this.result = result;
        this.headers = headers;
        this.code = code;
        this.inputStream = inputStream;
    }

    public String getResult() {
        return result;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public int getCode() {
        return code;
    }

    public int getResponseType() {
        return responseType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String toString() {
        return "OkResponse{" +
                "result='" + result + '\'' +
                ", headers=" + headers +
                ", code=" + code +
                ", responseType=" + responseType +
                '}';
    }
}