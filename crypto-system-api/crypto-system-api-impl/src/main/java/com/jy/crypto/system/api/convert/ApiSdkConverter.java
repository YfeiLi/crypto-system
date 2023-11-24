package com.jy.crypto.system.api.convert;

import com.jy.crypto.system.api.dao.entity.ApiSdk;
import com.jy.crypto.system.api.dto.ApiSdkListItem;
import com.jy.crypto.system.api.dto.ApiSdkPageReq;
import com.jy.crypto.system.api.dto.HttpSdkDetail;
import com.jy.crypto.system.api.dto.WsSdkDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApiSdkConverter {

    ApiSdkListItem toListItem(ApiSdk entity);

    HttpSdkDetail toHttpDetail(ApiSdk entity);

    WsSdkDetail toWsDetail(ApiSdk entity);

    ApiSdk toEntity(ApiSdkPageReq pageReq);
}
