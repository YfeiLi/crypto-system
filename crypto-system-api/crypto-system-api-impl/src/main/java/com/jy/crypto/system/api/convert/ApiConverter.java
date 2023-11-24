package com.jy.crypto.system.api.convert;

import com.jy.crypto.system.api.dao.entity.Api;
import com.jy.crypto.system.api.dao.entity.ApiParam;
import com.jy.crypto.system.api.dto.ApiListItem;
import com.jy.crypto.system.api.dto.ApiPageReq;
import com.jy.crypto.system.api.dto.HttpApiDetail;
import com.jy.crypto.system.api.dto.WsApiDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApiConverter {

    ApiListItem toListItem(Api entity);

    HttpApiDetail toHttpDetail(Api entity);

    HttpApiDetail.Param toHttpDetailParam(ApiParam entity);

    WsApiDetail toWsDetail(Api entity);

    Api toEntity(ApiPageReq pageReq);
}
