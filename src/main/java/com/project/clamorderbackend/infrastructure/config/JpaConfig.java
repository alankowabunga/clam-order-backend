package com.project.clamorderbackend.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA Configuration
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.project.clamorderbackend.domain.repository")
@EnableJpaAuditing
public class JpaConfig {
}
