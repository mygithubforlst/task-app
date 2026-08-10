package com.nrec.service.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库相关配置
 *
 * @author chenjia
 * @date 2018/3/8
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.nrec.service.*.mapper")
public class DatasourceConfig {

    @Value("${spring.datasource.url}")
    private String url;


    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(paginationInterceptor());
        //interceptor.addInnerInterceptor(new PaginationInnerInterceptor()); 如果有多数据源可以不配具体类型 否则都建议配上具体的DbType
        return interceptor;
    }


    /**
     * mybatis-plus分页插件
     */
    @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor page;
        final String mySqlType = "mysql";
        if (url.contains(mySqlType)) {
            page = new PaginationInnerInterceptor(DbType.MYSQL);
        } else if (url.contains("oracle")){
            page = new PaginationInnerInterceptor(DbType.ORACLE);
        }else if (url.contains("postgresql")) {
            page = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        }else if (url.contains("kingbase")) {
            page = new PaginationInnerInterceptor(DbType.KINGBASE_ES);
        } else if (url.contains("dm")) {
            page = new PaginationInnerInterceptor(DbType.DM);
        } else {
            page = new PaginationInnerInterceptor();
        }
        page.setMaxLimit(10000L);
        return page;
    }

}
