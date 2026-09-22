package com.kyoo.mall.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 组装层启动类，位于 com.kyoo.mall.app 包。
 * 注意：@SpringBootApplication 默认只扫描启动类所在包，因此必须用
 * scanBasePackages 显式指回根包，组件扫描才能覆盖各限界上下文模块
 * （com.kyoo.mall.user / product / common / app）。
 */
@SpringBootApplication(scanBasePackages = "com.kyoo.mall")
@MapperScan("com.kyoo.mall.**.infrastructure.persistence.mapper")
public class KyooMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyooMallApplication.class, args);
    }
}
