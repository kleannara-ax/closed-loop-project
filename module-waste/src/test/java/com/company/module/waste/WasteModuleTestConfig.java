package com.company.module.waste;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Module-Waste 테스트 전용 Application Context
 * - 자체 H2 인메모리 DB를 생성하여 독립적으로 테스트 실행
 * - Core 모듈이나 운영 DB(MariaDB)에 의존하지 않음
 */
@SpringBootApplication(scanBasePackages = "com.company.module.waste")
@EntityScan(basePackages = "com.company.module.waste.entity")
@EnableJpaRepositories(basePackages = "com.company.module.waste.repository")
public class WasteModuleTestConfig {
}
