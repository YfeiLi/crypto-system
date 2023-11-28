package com.jy.crypto.system.account.facade;

import com.jy.crypto.system.account.facade.dto.AccountDto;
import com.jy.crypto.system.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountFacadeImpl implements AccountFacade {

    private final AccountService accountService;

    @Override
    public AccountDto getById(Long id) {
        return accountService.getById(id);
    }
}
