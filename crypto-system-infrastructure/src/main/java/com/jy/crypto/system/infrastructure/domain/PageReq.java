package com.jy.crypto.system.infrastructure.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageReq {
    @Min(1)
    private Integer pageNum = 1;
    @Min(1)
    private Integer pageSize = 10;

    public <T> Page<T> asPage() {
        return new Page<>(pageNum, pageSize);
    }
}
