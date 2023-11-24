package com.jy.crypto.system.account.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jy.crypto.system.account.dto.AccountGroupListItem;
import com.jy.crypto.system.account.dto.AccountGroupPageReq;
import com.jy.crypto.system.account.dto.AccountListItem;
import com.jy.crypto.system.account.dto.AccountPageReq;
import com.jy.crypto.system.account.service.AccountGroupService;
import com.jy.crypto.system.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("account")
public class AccountController {

    private final AccountService accountService;

    private final AccountGroupService accountGroupService;

    @GetMapping
    public IPage<AccountListItem> getAccountPage(AccountPageReq req) {
        return accountService.getPage(req);
    }

    @GetMapping("group")
    public IPage<AccountGroupListItem> getAccountGroupPage(AccountGroupPageReq req) {
        return accountGroupService.getPage(req);
    }
}
