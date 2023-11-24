package com.jy.crypto.system.infrastructure.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
       // this.strictInsertFill(metaObject, "version", Integer.class,1);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        try {
            //this.strictInsertFill(metaObject,"createBy",String.class, SecurityUtils.getUsername());
            //this.strictInsertFill(metaObject,"updateBy",String.class,SecurityUtils.getUsername());
        } catch (Exception e) {
            this.strictInsertFill(metaObject,"createBy",String.class, "");
            this.strictInsertFill(metaObject,"updateBy",String.class, "");
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        try {
            //this.strictUpdateFill(metaObject,"updateBy",String.class,SecurityUtils.getUsername());
        } catch (Exception e) {
            this.strictUpdateFill(metaObject,"updateBy",String.class, "");
        }
    }
}
