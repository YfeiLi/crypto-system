package com.jy.crypto.system.api.service.client.http;

import com.jy.crypto.system.account.facade.dto.AccountDto;
import com.jy.crypto.system.api.dto.HttpApiDetail;
import com.jy.crypto.system.api.dto.HttpSdkDetail;
import com.jy.crypto.system.api.facade.dto.HttpResult;
import com.jy.crypto.system.api.facade.enums.HttpMethod;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import com.jy.crypto.system.script.facade.ScriptFacade;
import okhttp3.*;
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
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        commonOkHttpClientBuild(clientBuilder);
        customizedOkHttpClient(clientBuilder, sdkDetail);
        this.okHttpClient = clientBuilder.build();
    }

    @Autowired
    public void setScriptFacade(ScriptFacade scriptFacade) {
        this.scriptFacade = scriptFacade;
    }

    public HttpResult invoke(HttpApiDetail apiDetail, Map<String, Object> params, AccountDto account) {
        // 调用脚本获取请求
        Map<String, Object> requestHandlerVariables = new HashMap<>();
        requestHandlerVariables.put("api", apiDetail);
        requestHandlerVariables.put("sdk", sdkDetail);
        requestHandlerVariables.put("account", account);
        requestHandlerVariables.put("params", params);
        Object requestHandlerResult = scriptFacade.execute(sdkDetail.getRequestHandlerScriptId(), requestHandlerVariables);
        Request request = organizeRequest(apiDetail.getMethod(), requestHandlerResult);
        // 调用okHttpClient
        try (Response response = okHttpClient.newCall(request).execute()) {
            // 调用脚本获取结果
            Map<String, Object> responseHandlerVariables = new HashMap<>();
            responseHandlerVariables.put("api", apiDetail);
            responseHandlerVariables.put("sdk", sdkDetail);
            responseHandlerVariables.put("account", account);
            responseHandlerVariables.put("params", params);
            responseHandlerVariables.put("response", response);
            Object responseHandlerResult = scriptFacade.execute(sdkDetail.getResponseHandlerScriptId(), responseHandlerVariables);
            return organizeResult(responseHandlerResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * OkHttpClient公共配置
     */
    private void commonOkHttpClientBuild(OkHttpClient.Builder builder) {
        builder.callTimeout(Duration.ofSeconds(10))
                .retryOnConnectionFailure(true);
    }

    /**
     * 根据sdk配置OkHttpClient
     */
    private void customizedOkHttpClient(OkHttpClient.Builder builder, HttpSdkDetail sdkDetail) {
        if (sdkDetail.getTimeout() != null && sdkDetail.getTimeout() != 0) {
            builder.callTimeout(Duration.ofSeconds(sdkDetail.getTimeout()));
        }
    }

    /**
     * 组装请求
     */
    private Request organizeRequest(HttpMethod method, Object requestHandlerResult) {
        Request.Builder requestBuilder = new Request.Builder();
        if (!(requestHandlerResult instanceof Map<?, ?> requestHandlerResultMap)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
        }
        Object urlObj = requestHandlerResultMap.get("url");
        Object headersObj = requestHandlerResultMap.get("headers");
        Object bodyObj = requestHandlerResultMap.get("body");
        Object mediaTypeObj = requestHandlerResultMap.get("mediaType");
        if (urlObj == null) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
        }
        if (!(urlObj instanceof String)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
        }
        requestBuilder.url((String) urlObj);
        if (headersObj != null) {
            if (!(headersObj instanceof Map<?, ?> headers)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
            }
            for (Object keyObj : headers.keySet()) {
                Object valObj = headers.get(keyObj);
                if (!(keyObj instanceof String key) || !(valObj instanceof String val)) {
                    throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
                }
                requestBuilder.header(key, val);
            }
        }
        if (bodyObj != null) {
            if (!(bodyObj instanceof String body)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
            }
            if (mediaTypeObj == null) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
            }
            if ((!(mediaTypeObj instanceof String)) || MediaType.parse((String) mediaTypeObj) == null) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestHandlerScriptId());
            }
            requestBuilder.method(method.name(),
                    RequestBody.create(body, MediaType.parse((String) mediaTypeObj)));
        }
        return requestBuilder.build();
    }

    /**
     * 组装结果
     */
    private HttpResult organizeResult(Object responseHandlerResult) {
        HttpResult result = new HttpResult();
        if (!(responseHandlerResult instanceof Map<?, ?> responseHandlerResultMap)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandlerScriptId());
        }
        Object successObj = responseHandlerResultMap.get("success");
        Object dataObj = responseHandlerResultMap.get("data");
        Object msgObj = responseHandlerResultMap.get("msg");
        if (successObj == null) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandlerScriptId());
        }
        if (!(successObj instanceof Boolean success)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandlerScriptId());
        }
        result.setSuccess(success);
        result.setData(dataObj);
        if (msgObj != null) {
            if (!(msgObj instanceof String msg)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandlerScriptId());
            }
            result.setMsg(msg);
        }
        return result;
    }
}
