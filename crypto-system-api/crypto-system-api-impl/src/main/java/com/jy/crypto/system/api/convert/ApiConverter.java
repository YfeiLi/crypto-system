package com.jy.crypto.system.api.convert;

import com.jy.crypto.system.api.dao.entity.Api;
import com.jy.crypto.system.api.dao.entity.ApiParam;
import com.jy.crypto.system.api.dto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApiConverter {

    ApiListItem toListItem(Api entity);

    HttpApiDetail toHttpDetail(Api entity);

    ApiParamDto toDto(ApiParam entity);

    WsApiDetail toWsDetail(Api entity);

    Api toEntity(ApiPageReq pageReq);
}
