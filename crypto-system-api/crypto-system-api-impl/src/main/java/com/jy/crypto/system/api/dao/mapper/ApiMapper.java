package com.jy.crypto.system.api.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jy.crypto.system.api.dao.entity.Api;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ApiMapper extends BaseMapper<Api> {

    @Select("""
        <script>
        select *
        from api
        where code = #{code} and deleted = 0
        </script>
    """)
    Api selectByCode(@Param("code") String code);
}
