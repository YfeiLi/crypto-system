package com.jy.crypto.system.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jy.crypto.system.api.dao.entity.ApiSdk;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ApiSdkMapper extends BaseMapper<ApiSdk> {

    @Select("""
        <script>
        select *
        from api_sdk
        where code = #{code} and deleted = 0
        </script>
    """)
    ApiSdk selectByCode(@Param("code") String code);
}
