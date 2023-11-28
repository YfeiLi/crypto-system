package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.account.facade.dto.AccountDto;
import com.jy.crypto.system.api.dto.HttpApiDetail;
import com.jy.crypto.system.api.dto.HttpSdkDetail;
import com.jy.crypto.system.api.facade.dto.HttpResult;
import com.jy.crypto.system.script.facade.ScriptFacade;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class HttpSdkClient {

    private final HttpSdkDetail sdkDetail;
    private final OkHttpClient okHttpClient;
    private ScriptFacade scriptFacade;

    public HttpSdkClient(HttpSdkDetail sdkDetail) {
        this.sdkDetail = sdkDetail;
        this.okHttpClient = customizedOkHttpClient(commonOkHttpClientBuild(), sdkDetail);
    }

    @Autowired
    public void setScriptFacade(ScriptFacade scriptFacade) {
        this.scriptFacade = scriptFacade;
    }

    public HttpResult invoke(HttpApiDetail apiDetail, Map<String, Object> params, AccountDto account) {
        // 调用脚本获取请求
        Map<String, Object> requestHandlerVariables = new HashMap<>();
        requestHandlerVariables.put("apiDetail", apiDetail);
        requestHandlerVariables.put("sdkDetail", sdkDetail);
        requestHandlerVariables.put("account", account);
        requestHandlerVariables.put("params", params);
        Request request = (Request) scriptFacade.execute(sdkDetail.getRequestHandlerScriptId(), requestHandlerVariables);
        // 调用okHttpClient
        try (Response response = okHttpClient.newCall(request).execute()) {
            // 调用脚本获取结果
            Map<String, Object> responseHandlerVariables = new HashMap<>();
            responseHandlerVariables.put("apiDetail", apiDetail);
            responseHandlerVariables.put("sdkDetail", sdkDetail);
            responseHandlerVariables.put("account", account);
            responseHandlerVariables.put("params", params);
            responseHandlerVariables.put("response", response);
            return (HttpResult) scriptFacade.execute(sdkDetail.getResponseHandlerScriptId(), responseHandlerVariables);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * OkHttpClient公共配置
     */
    private OkHttpClient.Builder commonOkHttpClientBuild() {
        return new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(10))
                .retryOnConnectionFailure(true);
    }

    /**
     * 根据sdk配置OkHttpClient
     */
    private OkHttpClient customizedOkHttpClient(OkHttpClient.Builder builder, HttpSdkDetail sdkDetail) {
        if (sdkDetail.getTimeout() != null && sdkDetail.getTimeout() != 0) {
            builder.callTimeout(Duration.ofSeconds(sdkDetail.getTimeout()));
        }
        return builder.build();
    }
}
