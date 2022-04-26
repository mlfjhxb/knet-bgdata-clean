package cn.knet.bgdata.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
public class ClickHouseConfig {
    private static String clickhouseAddress;

    private static String clickhouseUsername;

    private static String clickhousePassword;

    private static String clickhouseDB;

    private static Integer clickhouseSocketTimeout;

    @Value("${spring.datasource.clickhouse.address}")
    public void setClickhouseAddress(String address) {
        ClickHouseConfig.clickhouseAddress = address;
    }

    @Value("${spring.datasource.clickhouse.username}")
    public void setClickhouseUsername(String username) {
        ClickHouseConfig.clickhouseUsername = username;
    }

    @Value("${spring.datasource.clickhouse.password}")
    public void setClickhousePassword(String password) {
        ClickHouseConfig.clickhousePassword = password;
    }

    @Value("${spring.datasource.clickhouse.db}")
    public void setClickhouseDB(String db) {
        ClickHouseConfig.clickhouseDB = db;
    }

    @Value("${spring.datasource.clickhouse.socketTimeout}")
    public void setClickhouseSocketTimeout(Integer socketTimeout) {
        ClickHouseConfig.clickhouseSocketTimeout = socketTimeout;
    }

    public static Connection getConn() {
        ClickHouseConnection conn = null;
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setUser(clickhouseUsername);
        properties.setPassword(clickhousePassword);
        properties.setDatabase(clickhouseDB);
        properties.setSocketTimeout(clickhouseSocketTimeout);
        ClickHouseDataSource clickHouseDataSource = new ClickHouseDataSource(clickhouseAddress, properties);
        try {
            conn = clickHouseDataSource.getConnection();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
