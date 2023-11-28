package com.jy.crypto.system.account.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jy.crypto.system.account.convert.AccountConverter;
import com.jy.crypto.system.account.dao.entity.Account;
import com.jy.crypto.system.account.dao.mapper.AccountMapper;
import com.jy.crypto.system.account.dto.AccountListItem;
import com.jy.crypto.system.account.dto.AccountPageReq;
import com.jy.crypto.system.account.facade.dto.AccountDto;
import com.jy.crypto.system.infrastructure.exception.BusinessException;
import com.jy.crypto.system.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountMapper mapper;
    private final AccountConverter converter;

    public IPage<AccountListItem> getPage(AccountPageReq req) {
        return mapper.selectPage(req.asPage(), Wrappers.lambdaQuery(converter.toEntity(req)))
                .convert(converter::toListItem);
    }

    @Cacheable(value = "account", key = "#id")
    public AccountDto getById(Long id) {
        Account account = mapper.selectById(id);
        if (account == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "account(id=" + id + ")");
        }
        return converter.toDto(account);
    }
}
