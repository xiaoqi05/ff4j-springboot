package org.ff4j.springboot.conf;


import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.ff4j.property.PropertyInt;
import org.ff4j.property.PropertyString;
import org.ff4j.springjdbc.store.EventRepositorySpringJdbc;
import org.ff4j.springjdbc.store.FeatureStoreSpringJdbc;
import org.ff4j.springjdbc.store.PropertyStoreSpringJdbc;
import org.ff4j.strategy.el.ExpressionFlipStrategy;
import org.ff4j.utils.Util;
import org.ff4j.web.ApiConfig;
import org.ff4j.web.FF4jDispatcherServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass ({FF4j.class})
//@ComponentScan (value = {"org.ff4j.spring.boot.web.api", "org.ff4j.services", "org.ff4j.aop", "org.ff4j.spring"})
public class FF4jConfiguration {

    @Value ("${ff4j.webapi.authentication}")
    private boolean authentication = false;

    @Value ("${ff4j.webapi.authorization}")
    private boolean authorization = false;


    @Autowired
    private DataSource dataSource;

    @Bean
    public FF4j getFF4j() {
        FF4j ff4j = new FF4j();
        ff4j.setFeatureStore(new FeatureStoreSpringJdbc(dataSource));
        ff4j.setPropertiesStore(new PropertyStoreSpringJdbc(dataSource));
        ff4j.setEventRepository(new EventRepositorySpringJdbc(dataSource));
        return ff4j;
    }

    @Bean
    public FF4jDispatcherServlet getFF4JServlet() {
        FF4jDispatcherServlet ds = new FF4jDispatcherServlet();
        ds.setFf4j(getFF4j());
        return ds;
    }

    @Bean
    public ApiConfig getApiConfig() {
        ApiConfig apiConfig = new ApiConfig();

        // Enable Authentication on API KEY
        apiConfig.setAuthenticate(true);
        apiConfig.createApiKey("apikey1", true, true, Util.set("ADMIN", "USER"));
        apiConfig.createApiKey("apikey2", true, true, Util.set("ADMIN", "USER"));
        apiConfig.createUser("b", "b", true, true, Util.set("ADMIN", "USER"));

        // Check Autorization as well
        apiConfig.setAutorize(true);
        apiConfig.setWebContext("/api/ff4j-web-console");
        apiConfig.setPort(8080);
        apiConfig.setFF4j(getFF4j());
        return apiConfig;
    }

}
