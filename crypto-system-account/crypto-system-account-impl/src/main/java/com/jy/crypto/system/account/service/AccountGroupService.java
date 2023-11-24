package com.jy.crypto.system.account.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jy.crypto.system.account.convert.AccountGroupConverter;
import com.jy.crypto.system.account.dao.mapper.AccountGroupMapper;
import com.jy.crypto.system.account.dto.AccountGroupListItem;
import com.jy.crypto.system.account.dto.AccountGroupPageReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountGroupService {

    private final AccountGroupMapper mapper;
    private final AccountGroupConverter converter;

    public IPage<AccountGroupListItem> getPage(AccountGroupPageReq req) {
        return mapper.selectPage(req.asPage(), Wrappers.lambdaQuery(converter.toEntity(req)))
                .convert(converter::toListItem);
    }
}
