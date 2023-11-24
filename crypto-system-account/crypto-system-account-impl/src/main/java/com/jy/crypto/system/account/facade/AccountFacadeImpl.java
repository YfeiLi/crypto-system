package com.jy.crypto.system.account.facade;

import com.jy.crypto.system.account.service.AccountGroupService;
import com.jy.crypto.system.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountFacadeImpl implements AccountFacade {

    private final AccountService accountService;

    private final AccountGroupService accountGroupService;
}
