package com.jy.crypto.system.account.facade;

import com.jy.crypto.system.account.facade.dto.AccountDto;

public interface AccountFacade {

    AccountDto getById(Long id);
}
