package com.kyoo.mall.app;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 架构守护测试：把 docs/ARCHITECTURE.md 的强制规则自动化，违反即构建失败。
 * 使用 JUnit Jupiter + ArchRule.check() 的显式方式（不依赖 ArchUnit 独立引擎，版本兼容更稳）。
 * 新增限界上下文模块时，在"业务模块隔离"一节补充对应规则。
 */
class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter().importPackages("com.kyoo.mall");
    }

    // ========== 模块级规则（docs/ARCHITECTURE.md §2.1）==========

    private static final ArchRule USER不得依赖PRODUCT = noClasses()
            .that().resideInAPackage("com.kyoo.mall.user..")
            .should().dependOnClassesThat().resideInAPackage("com.kyoo.mall.product..")
            .as("业务模块之间禁止互相依赖，跨上下文协作在 app 组装层编排");

    private static final ArchRule PRODUCT不得依赖USER = noClasses()
            .that().resideInAPackage("com.kyoo.mall.product..")
            .should().dependOnClassesThat().resideInAPackage("com.kyoo.mall.user..")
            .as("业务模块之间禁止互相依赖，跨上下文协作在 app 组装层编排");

    private static final ArchRule COMMON不得依赖业务模块与SPRING = noClasses()
            .that().resideInAPackage("com.kyoo.mall.common..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.kyoo.mall.user..", "com.kyoo.mall.product..", "com.kyoo.mall.app..",
                    "org.springframework..")
            .as("common 是零 Spring 依赖的共享内核，不得依赖业务模块");

    // ========== 分层规则（docs/ARCHITECTURE.md §2.3）==========

    private static final ArchRule DOMAIN不得依赖上层与SPRING = noClasses()
            .that().resideInAPackage("com.kyoo.mall..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.kyoo.mall..application..", "com.kyoo.mall..interfaces..",
                    "com.kyoo.mall..infrastructure..", "org.springframework..")
            .as("domain 是业务核心：只依赖自身/common/lombok/JDK/MP与Jackson注解");

    private static final ArchRule APPLICATION不得依赖INTERFACES与INFRASTRUCTURE = noClasses()
            .that().resideInAPackage("com.kyoo.mall..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.kyoo.mall..interfaces..", "com.kyoo.mall..infrastructure..")
            .as("application 只依赖 domain 的接口，不感知接入层与技术实现");

    private static final ArchRule INTERFACES不得依赖INFRASTRUCTURE = noClasses()
            .that().resideInAPackage("com.kyoo.mall..interfaces..")
            .should().dependOnClassesThat().resideInAPackage("com.kyoo.mall..infrastructure..")
            .as("Controller 只调应用服务，不得直接使用 Mapper/仓储实现/技术组件");

    private static final ArchRule MAPPER只被仓储实现使用 = noClasses()
            .that().resideOutsideOfPackage("com.kyoo.mall..infrastructure.persistence..")
            .should().dependOnClassesThat().resideInAPackage("com.kyoo.mall..infrastructure.persistence.mapper..")
            .as("Mapper 是 ORM 细节，只能被 RepositoryImpl 使用，禁止注入到 Controller/AppService");

    private static final ArchRule ORM类型不得进入DOMAIN与APPLICATION = noClasses()
            .that().resideInAnyPackage("com.kyoo.mall..domain..", "com.kyoo.mall..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.baomidou.mybatisplus.extension..", "com.baomidou.mybatisplus.core..")
            .as("分页等 ORM 实现类型（Page/QueryWrapper）不得进入 domain/application，" +
                    "分页契约使用 common 的 PageQuery/PageResult；domain 只允许 MP 注解包");

    // ========== 执行 ==========

    @Test
    void userMustNotDependOnProduct() {
        USER不得依赖PRODUCT.check(classes);
    }

    @Test
    void productMustNotDependOnUser() {
        PRODUCT不得依赖USER.check(classes);
    }

    @Test
    void commonMustNotDependOnBusinessModulesOrSpring() {
        COMMON不得依赖业务模块与SPRING.check(classes);
    }

    @Test
    void domainMustNotDependOnUpperLayersOrSpring() {
        DOMAIN不得依赖上层与SPRING.check(classes);
    }

    @Test
    void applicationMustNotDependOnInterfacesOrInfrastructure() {
        APPLICATION不得依赖INTERFACES与INFRASTRUCTURE.check(classes);
    }

    @Test
    void interfacesMustNotDependOnInfrastructure() {
        INTERFACES不得依赖INFRASTRUCTURE.check(classes);
    }

    @Test
    void mapperOnlyUsedByRepositoryImpl() {
        MAPPER只被仓储实现使用.check(classes);
    }

    @Test
    void ormTypesMustNotEnterDomainOrApplication() {
        ORM类型不得进入DOMAIN与APPLICATION.check(classes);
    }
}
