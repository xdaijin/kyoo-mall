package com.kyoo.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 组装层启动类。包位于 com.kyoo.mall 根部，组件扫描自动覆盖各限界上下文模块
 * （com.kyoo.mall.user / product / common / app）。
 */
@SpringBootApplication
@MapperScan("com.kyoo.mall.**.infrastructure.persistence")
public class KyooMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyooMallApplication.class, args);
    }
}
