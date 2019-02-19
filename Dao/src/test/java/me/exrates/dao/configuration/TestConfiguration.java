package me.exrates.dao.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:/test-db.properties")
public class TestConfiguration {

    @Value("${db.slave.user}")
    private String dbSlaveUser;
    @Value("${db.slave.password}")
    private String dbSlavePassword;
    @Value("${db.slave.url}")
    private String dbSlaveUrl;
    @Value("${db.slave.classname}")
    private String dbSlaveClassname;

    @Bean(name = "slaveHikariDataSourceTest")
    public DataSource slaveHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dbSlaveClassname);
        hikariConfig.setJdbcUrl(dbSlaveUrl);
        hikariConfig.setUsername(dbSlaveUser);
        hikariConfig.setPassword(dbSlavePassword);
        hikariConfig.setMaximumPoolSize(50);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @DependsOn("slaveHikariDataSourceTest")
    @Bean(name = "slaveTemplateTest")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(@Qualifier("slaveHikariDataSourceTest") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}