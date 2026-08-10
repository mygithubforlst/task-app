---
title: 数据库jdbc连接配置说明
date: 2024-01-30 15:45:35
permalink: /pages/jdbc-config
author: admin
categories: 
  - 开发规范
tags: 
  - 
---
# 数据库链接参数说明
参数名称                    参数说明

user                      数据库用户名（用于连接数据库）

password                   用户密码（用于连接数据库）

useUnicode              是否使用Unicode字符集，如果参数characterEncoding设置为gb2312或gbk，本参数值必须设置为true

characterEncoding       当useUnicode设置为true时，指定字符编码。比如可设置为gb2312或gbk

autoReconnect                当数据库连接异常中断时，是否自动重新连接？

autoReconnectForPools     是否使用针对数据库连接池的重连策略

failOverReadOnly         自动重连成功后，连接是否设置为只读？

maxReconnects           autoReconnect设置为true时，重试连接的次数

initialTimeout        autoReconnect设置为true时，两次重连之间的时间间隔，单位：秒

connectTimeout       和数据库服务器建立socket连接时的超时，单位：毫秒。 0表示永不超时，适用于JDK 1.4及更高版本

socketTimeout          socket操作（读写）超时，单位：毫秒。 0表示永不超时

# 数据库链接说明
## MySQL 

```
jdbc.driverClassName=com.mysql.cj.jdbc.Driver
dbc.url=jdbc:mysql://host:port/db
jdbc.username=root
jdbc.password=root
```
```
setenv DB_TYPE mysql
# mysql5-7,8也可以使用
setenv DB_DIALECT org.hibernate.dialect.MySQL57Dialect
# mysql8
setenv DB_DIALECT org.hibernate.dialect.MySQL8Dialect
```
## Oracle (sid)

```
jdbc.driverClassName=oracle.jdbc.OracleDriver
jdbc.url=jdbc:oracle:thin:@host:port:sid
jdbc.username=scott
jdbc.password=scott
```
```
setenv DB_TYPE oracle
setenv DB_DIALECT org.hibernate.dialect.Oracle10gDialect
```
## Oracle （serviceName）

```
jdbc.driverClassName=oracle.jdbc.OracleDriver
jdbc.url=jdbc:oracle:thin:@//host:port/serviceName
jdbc.username=scott
jdbc.password=scott
```

## PostgreSQL 

```
jdbc.driverClassName=org.postgresql.Driver
jdbc.url=jdbc:postgresql://host:port/db
jdbc.username=
jdbc.password=
```

```
setenv DB_TYPE postgresql
setenv DB_DIALECT org.hibernate.dialect.PostgreSQL95Dialect
```

## Informix

```
jdbc.driver=com.informix.jdbc.IfxDriver
jdbc.url=jdbc:informix-sqli://host:port/db:INFORMIXSERVER=server
jdbc.username=
jdbc.password=
```

## DM（达梦）

```
jdbc.driver=dm.jdbc.driver.DmDriver
jdbc.url=jdbc:dm://host:port/db
jdbc.username=
jdbc.password=
```
```
setenv DB_TYPE oracle
setenv DB_DIALECT org.hibernate.dialect.DmDialect
```
## kingbase（金仓）

```
jdbc.driver=com.kingbase.Driver
jdbc.url=jdbc:kingbase://host:port/db
jdbc.username=
jdbc.password=
```
setenv DB_TYPE postgresql
setenv DB_DIALECT org.hibernate.dialect.Kingbase8Dialect

## MS SQL Server 2000 (JTDS) 

```
jdbc.driverClassName=net.sourceforge.jtds.jdbc.Driver
jdbc.url=jdbc:jtds:sqlserver://host:port/db
jdbc.username=
jdbc.password=
```

## SQLServer (Microsoft) 

```
jdbc.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
jdbc.url=jdbc:sqlserver://host:port;DatabaseName=db
jdbc.username=
jdbc.password=
```

## HSQLDB 

```
jdbc.driverClassName=org.hsqldb.jdbcDriver
jdbc.url=jdbc:hsqldb:hsql://localhost:9001/bookstore
jdbc.username=
jdbc.password=
```
## DB2

```
jdbc.driver=com.ibm.db2.jcc.DB2Driver
jdbc.url=jdbc:db2://host:port/db
jdbc.username=
jdbc.password=
```

## Sybase

```
jdbc.driver=com.sybase.jdbc.SybDriver
jdbc.url=jdbc:sybase:Tds:host:port/db
jdbc.username=
jdbc.password=
```

# 数据库连接问题

## 问题：Communications link failure 

原因：jdbcurl中配置了connectTimeout=1000&socketTimeout=3000，导致执行时间比较长的sql会直接断连，产生Communications link failure 

```yaml
    url: jdbc:p6spy:mysql://198.120.100.103:5506/standard?useUnicode=true&characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai
```