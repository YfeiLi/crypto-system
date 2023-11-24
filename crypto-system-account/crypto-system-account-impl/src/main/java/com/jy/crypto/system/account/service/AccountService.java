package com.jy.crypto.system.account.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jy.crypto.system.account.convert.AccountConverter;
import com.jy.crypto.system.account.dao.mapper.AccountMapper;
import com.jy.crypto.system.account.dto.AccountListItem;
import com.jy.crypto.system.account.dto.AccountPageReq;
import lombok.RequiredArgsConstructor;
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
}
