package com.jy.crypto.system.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jy.crypto.system.api.convert.ApiSdkConverter;
import com.jy.crypto.system.api.dao.entity.ApiSdk;
import com.jy.crypto.system.api.dao.entity.HttpSdkConfig;
import com.jy.crypto.system.api.dao.entity.WsSdkConfig;
import com.jy.crypto.system.api.dao.mapper.ApiSdkMapper;
import com.jy.crypto.system.api.dao.mapper.HttpSdkConfigMapper;
import com.jy.crypto.system.api.dao.mapper.WsSdkConfigMapper;
import com.jy.crypto.system.api.dto.ApiSdkListItem;
import com.jy.crypto.system.api.dto.ApiSdkPageReq;
import com.jy.crypto.system.api.dto.HttpSdkDetail;
import com.jy.crypto.system.api.dto.WsSdkDetail;
import com.jy.crypto.system.api.facade.enums.ApiType;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Api Sdk增删查改服务
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ApiSdkReadService {

    private final ApiSdkMapper mapper;
    private final HttpSdkConfigMapper httpSdkConfigMapper;
    private final WsSdkConfigMapper wsSdkConfigMapper;
    private final ApiSdkConverter converter;

    /**
     * 分页查询
     */
    public IPage<ApiSdkListItem> getPage(ApiSdkPageReq req) {
        return mapper.selectPage(req.asPage(), Wrappers.lambdaQuery(converter.toEntity(req)))
                .convert(converter::toListItem);
    }

    /**
     * 获取http sdk详情
     */
    public HttpSdkDetail getHttpSdkDetail(String code) {
        ApiSdk apiSdk = mapper.selectByCode(code);
        if (apiSdk == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + code + ")");
        }
        HttpSdkConfig httpSdkConfig = httpSdkConfigMapper.selectById(apiSdk.getId());
        if (httpSdkConfig == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk config(code=" + code + ")");
        }
        return covertToHttpSdkDetail(apiSdk, httpSdkConfig);
    }

    /**
     * 获取所有的http sdk详情
     */
    public List<HttpSdkDetail> getAllHttpSdkDetail() {
        List<HttpSdkDetail> detailList = new ArrayList<>();
        List<ApiSdk> apiSdkList = mapper.selectList(Wrappers.<ApiSdk>lambdaQuery()
                .eq(ApiSdk::getType, ApiType.HTTP));
        if (apiSdkList.isEmpty()) {
            return detailList;
        }
        List<HttpSdkConfig> configList = httpSdkConfigMapper
                .selectBatchIds(apiSdkList.stream().map(ApiSdk::getId).collect(Collectors.toList()));
        for (ApiSdk apiSdk : apiSdkList) {
            configList.stream()
                    .filter(item -> item.getId().equals(apiSdk.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            config -> detailList.add(covertToHttpSdkDetail(apiSdk, config)),
                            () -> log.warn("sdk config(code=" + apiSdk.getCode() + ") not found")
                    );
        }
        return detailList;
    }

    /**
     * 将ApiSdk和HttpSdkConfig结合为HttpSdkDetail
     */
    private HttpSdkDetail covertToHttpSdkDetail(ApiSdk apiSdk, HttpSdkConfig sdkConfig) {
        HttpSdkDetail detail = converter.toHttpDetail(apiSdk);
        detail.setTimeout(sdkConfig.getTimeout());
        detail.setHeaders(sdkConfig.getHeaders());
        detail.setRequestGenerateScriptId(sdkConfig.getRequestGenerateScriptId());
        detail.setResponseHandleScriptId(sdkConfig.getResponseHandleScriptId());
        return detail;
    }

    /**
     * 获取ws sdk详情
     */
    public WsSdkDetail getWsSdkDetail(String code) {
        ApiSdk apiSdk = mapper.selectByCode(code);
        if (apiSdk == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk(code=" + code + ")");
        }
        WsSdkConfig wsSdkConfig = wsSdkConfigMapper.selectById(apiSdk.getId());
        if (wsSdkConfig == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "sdk config(code=" + code + ")");
        }
        return convertToWsSdkDetail(apiSdk, wsSdkConfig);
    }

    /**
     * 获取所有的ws sdk详情
     */
    public List<WsSdkDetail> getAllWsSdkDetail() {
        List<WsSdkDetail> detailList = new ArrayList<>();
        List<ApiSdk> apiSdkList = mapper.selectList(Wrappers.<ApiSdk>lambdaQuery()
                .eq(ApiSdk::getType, ApiType.HTTP));
        if (apiSdkList.isEmpty()) {
            return detailList;
        }
        List<WsSdkConfig> wsApiConfigList = wsSdkConfigMapper
                .selectBatchIds(apiSdkList.stream().map(ApiSdk::getId).collect(Collectors.toList()));
        for (ApiSdk apiSdk : apiSdkList) {
            wsApiConfigList.stream()
                    .filter(item -> item.getSdkCode().equals(apiSdk.getCode()))
                    .findFirst()
                    .ifPresent(config -> detailList.add(convertToWsSdkDetail(apiSdk, config)));
        }
        return detailList;
    }

    /**
     * 将ApiSdk和WsSdkConfig结合为WsSdkDetail
     *
     */
    private WsSdkDetail convertToWsSdkDetail(ApiSdk apiSdk, WsSdkConfig wsSdkConfig) {
        WsSdkDetail sdkDetail = converter.toWsDetail(apiSdk);
        sdkDetail.setConnectHashCodeScriptId(wsSdkConfig.getConnectHashCodeScriptId());
        sdkDetail.setUrlGenerateScriptId(wsSdkConfig.getUrlGenerateScriptId());
        sdkDetail.setSubscribeMsgGenerateScriptId(wsSdkConfig.getSubscribeMsgGenerateScriptId());
        sdkDetail.setUnsubscribeMsgGenerateScriptId(wsSdkConfig.getUnsubscribeMsgGenerateScriptId());
        sdkDetail.setMsgReceiveFilterScriptId(wsSdkConfig.getMsgReceiveFilterScriptId());
        return sdkDetail;
    }
}
