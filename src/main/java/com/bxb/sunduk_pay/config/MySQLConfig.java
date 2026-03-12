package com.bxb.sunduk_pay.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuration class for setting up MySQL database connectivity
 * and JPA repositories.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.bxb.sunduk_pay.repository",
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager"
)
public class MySQLConfig {
    
    @Primary
    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://walletapp-mysql:3306/sundukpay");
        dataSource.setUsername("walletuser");
        dataSource.setPassword("walletpass123");
        dataSource.setMaximumPoolSize(30);
        dataSource.setMinimumIdle(10);
        dataSource.setConnectionTimeout(2000);
        dataSource.setIdleTimeout(300000);
        dataSource.setMaxLifetime(1800000);
        return dataSource;
    }
    
    @Primary
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        
        HashMap<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        
        return builder
                .dataSource(dataSource)
                .packages("com.bxb.sunduk_pay.model")
                .persistenceUnit("mysql")
                .properties(props)
                .build();
    }
    
    @Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }
}