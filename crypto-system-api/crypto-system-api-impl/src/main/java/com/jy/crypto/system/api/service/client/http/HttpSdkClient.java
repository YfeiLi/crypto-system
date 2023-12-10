package com.jy.crypto.system.api.service.client.http;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 调用
     */
    public HttpResult invoke(HttpApiDetail apiDetail, Map<String, Object> params) {
        // 调用脚本获取请求
        Map<String, Object> requestGenerateVariables = Map.of(
                "api", apiDetail, "sdk", sdkDetail,
                "params", params);
        Object requestGenerateScriptResult = scriptFacade.execute(sdkDetail.getRequestGenerateScriptId(), requestGenerateVariables);
        Request request = organizeRequest(apiDetail.getMethod(), requestGenerateScriptResult);
        // 调用okHttpClient
        try (Response response = okHttpClient.newCall(request).execute()) {
            // 调用脚本获取结果
            Map<String, Object> responseHandleVariables = Map.of(
                    "api", apiDetail, "sdk", sdkDetail,
                    "params", params, "response", Map.of(
                            "code", response.code(),
                            "headers", response.headers().toMultimap().entrySet().stream()
                                    .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                                            entry.getValue().stream().reduce((a, b) ->
                                                    String.join(",", a, b)))),
                            "body", response.body() == null ? "" : response.body().string()
                    ));
            Object responseHandleScriptResult = scriptFacade.execute(sdkDetail.getResponseHandleScriptId(), responseHandleVariables);
            return organizeResult(responseHandleScriptResult);
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
            builder.addInterceptor(chain -> {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder();
                if (sdkDetail.getHeaders() != null) {
                    for (String key : sdkDetail.getHeaders().keySet()) {
                        if (StringUtils.isEmpty(originalRequest.header(key))) {
                            String value = sdkDetail.getHeaders().get(key);
                            requestBuilder.header(key, value);
                        }
                    }
                }
                return chain.proceed(requestBuilder.build());
            });
        }
    }

    /**
     * 组装请求
     */
    private Request organizeRequest(HttpMethod method, Object requestGenerateScriptResult) {
        Request.Builder requestBuilder = new Request.Builder();
        if (!(requestGenerateScriptResult instanceof Map<?, ?> requestHandlerResultMap)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
        }
        Object urlObj = requestHandlerResultMap.get("url");
        Object headersObj = requestHandlerResultMap.get("headers");
        Object bodyObj = requestHandlerResultMap.get("body");
        Object mediaTypeObj = requestHandlerResultMap.get("mediaType");
        if (urlObj == null) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
        }
        if (!(urlObj instanceof String)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
        }
        requestBuilder.url((String) urlObj);
        if (headersObj != null) {
            if (!(headersObj instanceof Map<?, ?> headers)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
            }
            for (Object keyObj : headers.keySet()) {
                Object valObj = headers.get(keyObj);
                if (!(keyObj instanceof String key) || !(valObj instanceof String val)) {
                    throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
                }
                requestBuilder.header(key, val);
            }
        }
        if (bodyObj != null) {
            if (!(bodyObj instanceof String body)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
            }
            if (mediaTypeObj == null) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
            }
            if ((!(mediaTypeObj instanceof String)) || MediaType.parse((String) mediaTypeObj) == null) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getRequestGenerateScriptId());
            }
            requestBuilder.method(method.name(),
                    RequestBody.create(body, MediaType.parse((String) mediaTypeObj)));
        }
        return requestBuilder.build();
    }

    /**
     * 组装结果
     */
    private HttpResult organizeResult(Object responseHandleScriptResult) {
        HttpResult result = new HttpResult();
        if (!(responseHandleScriptResult instanceof Map<?, ?> responseHandlerResultMap)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandleScriptId());
        }
        Object successObj = responseHandlerResultMap.get("success");
        Object dataObj = responseHandlerResultMap.get("data");
        Object msgObj = responseHandlerResultMap.get("msg");
        if (successObj == null) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandleScriptId());
        }
        if (!(successObj instanceof Boolean success)) {
            throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandleScriptId());
        }
        result.setSuccess(success);
        result.setData(dataObj);
        if (msgObj != null) {
            if (!(msgObj instanceof String msg)) {
                throw new BusinessException(ErrorCode.SCRIPT_RESULT_TYPE_ERROR, sdkDetail.getResponseHandleScriptId());
            }
            result.setMsg(msg);
        }
        return result;
    }
}
