//package me.exrates.service.impl;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Properties;
//
///**
// * @author Denis Savin (pilgrimm333@gmail.com)
// */
//public class MockConfig {
//
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
//        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//        Properties properties = new Properties();
//        properties.load(new FileReader("clasdas"));
//        properties.put("alternatePassphrase", "24T8b0kXf3dckThIbgsOucmXH");
//        propertySourcesPlaceholderConfigurer.setProperties(properties);
//        return propertySourcesPlaceholderConfigurer;
//    }
//
//    @Bean
//    public Properties properties() {
//        Properties properties = new Properties();
//        properties.put("alternatePassphrase", "phrase");
//        return properties;
//    }
//}