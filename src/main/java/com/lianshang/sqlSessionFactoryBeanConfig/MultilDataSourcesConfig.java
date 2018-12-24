//package com.lianshang.sqlSessionFactoryBeanConfig;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//import java.io.PrintWriter;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.SQLFeatureNotSupportedException;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * 描述:
// *
// * @AUTHOR 孙龙云
// * @date 2018-12-20 下
// * 午3:37
// */
////@Configuration
//@Slf4j
//public class MultilDataSourcesConfig {
//
//    //写两个无用的datasource,让mybatis-plus的自动配置无效
//    @Bean("d1")
//    public DataSource getD1() {
//        return getDataSource();
//    }
//
//    @Bean("d2")
//    public DataSource getD2() {
//        return getDataSource();
//    }
//
//    private DataSource getDataSource() {
//        return new DataSource() {
//            @Override
//            public Connection getConnection() throws SQLException {
//                return null;
//            }
//
//            @Override
//            public Connection getConnection(String username, String password) throws SQLException {
//                return null;
//            }
//
//            @Override
//            public <T> T unwrap(Class<T> iface) throws SQLException {
//                return null;
//            }
//
//            @Override
//            public boolean isWrapperFor(Class<?> iface) throws SQLException {
//                return false;
//            }
//
//            @Override
//            public PrintWriter getLogWriter() throws SQLException {
//                return null;
//            }
//
//            @Override
//            public void setLogWriter(PrintWriter out) throws SQLException {
//
//            }
//
//            @Override
//            public void setLoginTimeout(int seconds) throws SQLException {
//
//            }
//
//            @Override
//            public int getLoginTimeout() throws SQLException {
//                return 0;
//            }
//
//            @Override
//            public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
//                return null;
//            }
//        };
//    }
//}
