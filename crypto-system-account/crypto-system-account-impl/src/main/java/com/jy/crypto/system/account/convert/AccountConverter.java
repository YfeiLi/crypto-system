package com.jy.crypto.system.account.convert;

import com.jy.crypto.system.account.dao.entity.Account;
import com.jy.crypto.system.account.dto.AccountListItem;
import com.jy.crypto.system.account.dto.AccountPageReq;
import com.jy.crypto.system.account.facade.dto.AccountDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountConverter {

    AccountListItem toListItem(Account entity);

    AccountDto toDto(Account entity);

    Account toEntity(AccountPageReq pageReq);
}
