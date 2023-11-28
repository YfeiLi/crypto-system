package com.jy.crypto.system.account.facade.dto;

import lombok.Data;

@Data
public class AccountDto {
    private Long id;
    private String name;
    private String exchange;
    private String apiKey;
    private String apiSecret;
}
