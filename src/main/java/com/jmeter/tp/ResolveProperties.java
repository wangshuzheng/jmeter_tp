package com.jmeter.tp;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * @author wangshuzheng
 * @date 2020/7/21 2:56 下午
 * @description
 */
public final class ResolveProperties {

    /**
     * 时间格式化
     */
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 配置文件地址
     */
    private static final String PROP_FILE_NAME = "filename.properties";

    /**
     * 配置类
     */
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStream inputStream = ResolveProperties.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("load properties file fail, " + e);
            System.exit(500);
        }
    }

    public static String getPropertyByKey(String key) {
        if (null == key || key.equalsIgnoreCase("")) {
            return null;
        }
        return properties.getProperty(key);
    }

}
