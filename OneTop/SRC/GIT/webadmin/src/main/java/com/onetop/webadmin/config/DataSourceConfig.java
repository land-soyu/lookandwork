package com.onetop.webadmin.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * DB셋팅 config
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Primary
    @Bean(name="webAdminDataSource")
    @ConfigurationProperties("spring.datasource.hikari")
    DataSource webAdminDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name="namedParameterWebAdminJdbcTemplate")
    @Autowired
    NamedParameterJdbcTemplate namedParameterWebAdminJdbcTemplate(@Qualifier("webAdminDataSource") DataSource adjustmentDataSource){
        return new NamedParameterJdbcTemplate(adjustmentDataSource);
    }

}