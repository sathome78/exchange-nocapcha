package me.exrates;

import com.yandex.money.api.utils.Strings;

import java.util.Properties;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public abstract class BaseProperties {

    private final Properties properties = new Properties();

    public BaseProperties(String resource) {
        if (Strings.isNullOrEmpty(resource)) {
            throw new IllegalArgumentException("resource in null or empty");
        }
        try {
            properties.load(BaseProperties.class.getResourceAsStream(resource));
        } catch (Exception e) {
            throw new RuntimeException("properties not found",e);
        }
    }

    protected final String get(String propertyName) {
        return properties.getProperty(propertyName,"");
    }
}