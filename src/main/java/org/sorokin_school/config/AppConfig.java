package org.sorokin_school.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.sorokin_school")
@PropertySource("classpath:application.properties")
public class AppConfig {
}