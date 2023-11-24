package com.jy.crypto.system.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jy.crypto.system.api.convert.ApiConverter;
import com.jy.crypto.system.api.dao.entity.Api;
import com.jy.crypto.system.api.dao.entity.ApiParam;
import com.jy.crypto.system.api.dao.entity.HttpApiConfig;
import com.jy.crypto.system.api.dao.entity.WsApiConfig;
import com.jy.crypto.system.api.dao.mapper.ApiMapper;
import com.jy.crypto.system.api.dao.mapper.HttpApiConfigMapper;
import com.jy.crypto.system.api.dao.mapper.HttpApiParamMapper;
import com.jy.crypto.system.api.dao.mapper.WsApiConfigMapper;
import com.jy.crypto.system.api.dto.ApiListItem;
import com.jy.crypto.system.api.dto.ApiPageReq;
import com.jy.crypto.system.api.dto.HttpApiDetail;
import com.jy.crypto.system.api.dto.WsApiDetail;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Api增删查改服务
 */
@RequiredArgsConstructor
@Service
public class ApiReadService {

    private final ApiMapper mapper;
    private final HttpApiConfigMapper httpApiConfigMapper;
    private final WsApiConfigMapper wsApiConfigMapper;
    private final HttpApiParamMapper httpApiParamMapper;
    private final ApiConverter converter;

    /**
     * 分页查询
     */
    public IPage<ApiListItem> getPage(ApiPageReq req) {
        return mapper.selectPage(req.asPage(), Wrappers.lambdaQuery(converter.toEntity(req)))
                .convert(converter::toListItem);
    }

    /**
     * 获取http api详情
     */
    @Cacheable(value = "httpApiDetail", key = "#code")
    public HttpApiDetail getHttpApiDetail(String code) {
        Api api = mapper.selectByCode(code);
        if (api == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "api(code=" + code + ")");
        }
        HttpApiConfig httpApiConfig = httpApiConfigMapper.selectById(api.getId());
        if (httpApiConfig == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "api config(code=" + code + ")");
        }
        List<ApiParam> paramList = httpApiParamMapper.selectList(Wrappers.<ApiParam>lambdaQuery()
                .eq(ApiParam::getApiId, api.getId()));
        HttpApiDetail detail = converter.toHttpDetail(api);
        detail.setPath(httpApiConfig.getPath());
        detail.setMethod(httpApiConfig.getMethod());
        detail.setParamList(paramList.stream()
                .map(converter::toHttpDetailParam)
                .collect(Collectors.toList()));
        return detail;
    }

    /**
     * 获取websocket api详情
     */
    @Cacheable(value = "wsApiDetail", key = "#code")
    public WsApiDetail getWsApiDetail(String code) {
        Api api = mapper.selectByCode(code);
        if (api == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "api(code=" + code + ")");
        }
        WsApiConfig wsApiConfig = wsApiConfigMapper.selectById(api.getId());
        if (wsApiConfig == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "api config(code=" + code + ")");
        }
        List<ApiParam> paramList = httpApiParamMapper.selectList(Wrappers.<ApiParam>lambdaQuery()
                .eq(ApiParam::getApiId, api.getId()));
        WsApiDetail detail = converter.toWsDetail(api);
        detail.setPath(wsApiConfig.getPath());
        detail.setEvent(wsApiConfig.getEvent());
        detail.setParamList(paramList.stream()
                .map(converter::toHttpDetailParam)
                .collect(Collectors.toList()));
        return detail;
    }
}
