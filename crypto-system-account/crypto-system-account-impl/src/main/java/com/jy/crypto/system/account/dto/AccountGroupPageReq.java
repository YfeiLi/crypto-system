package com.jy.crypto.system.account.dto;

import com.jy.crypto.system.infrastructure.domain.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountGroupPageReq extends PageReq {

    private String name;
}
