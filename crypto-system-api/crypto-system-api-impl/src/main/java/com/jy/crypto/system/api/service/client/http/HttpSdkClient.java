package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.api.dto.HttpApiDetail;
import com.jy.crypto.system.api.dto.HttpSdkDetail;
import com.jy.crypto.system.api.facade.dto.HttpResult;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope("prototype")
public class HttpSdkClient {

    private HttpSdkDetail sdkDetail;
    private OkHttpClient okHttpClient;

    public HttpSdkClient(HttpSdkDetail sdkDetail) {
        this.sdkDetail = sdkDetail;
        this.okHttpClient = customizedOkHttpClient(sdkDetail);
    }

    public HttpResult invoke(HttpApiDetail apiDetail, Map<String, Object> params) {
        // 校验参数

        // 设置url

        // 设置body

        // 调用beforeHandler获取请求

        // 调用okHttpClient

        // 处理结果

        return null;
    }

    private OkHttpClient customizedOkHttpClient(HttpSdkDetail sdkDetail) {
        return new OkHttpClient.Builder().build();
    }
}
