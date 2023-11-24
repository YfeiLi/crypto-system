package com.jy.crypto.system.account.convert;

import com.jy.crypto.system.account.dao.entity.AccountGroup;
import com.jy.crypto.system.account.dto.AccountGroupListItem;
import com.jy.crypto.system.account.dto.AccountGroupPageReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountGroupConverter {

    AccountGroupListItem toListItem(AccountGroup entity);

    AccountGroup toEntity(AccountGroupPageReq pageReq);
}
