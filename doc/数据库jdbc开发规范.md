---
title: 数据库jdbc开发规范
date: 2024-04-18 15:45:35
permalink: /pages/jdbc-dev
author: admin
categories: 
  - 开发规范
tags: 
  - 
---
# 开发规范
1. 构建数据模型时，不要使用混合主键形式的实体表，使用单主键ID，且在同一个服务中ID类型应一致，以适配ORM框架

2. 数据库映射和自动代码生成：使用SophiCloud开发模版时，使用附带的自动代码生成模块MpGenerator，将数据库表映射为对应实体并构建CRUD代码结构。

3. ORM框架：SophiCloud开发模版使用对象关系映射（ORM）框架Mybatis及其增强工具Mybatis-plus。Mybatis-plus可以提供数据库中立的抽象层，将数据库操作转换为对象操作，从而减少直接编写SQL的需求。Mybatis通常提供对多种数据库的支持，并处理数据库差异问题。mybatis框架支持的数据库如下:
```
    MySQL，Oracle，DB2，H2，HSQL，SQLite，PostgreSQL，SQLServer，Phoenix，Gauss ，ClickHouse，Sybase，OceanBase，Firebird，Cubrid，Goldilocks，csiidb，informix，TDengine，redshift
    达梦数据库，虚谷数据库，人大金仓数据库，南大通用(华库)数据库，南大通用数据库，神通数据库，瀚高数据库，优炫数据库，星瑞格数据库
```

4. SQL标准：学习和遵循SQL标准，使用标准SQL语法，减少对特定数据库的依赖。SQL标准语法参考w3c规范https://www.w3school.com.cn/sql/index.asp

5. 避免数据库特定功能：避免使用特定于某个数据库的功能、语法或扩展（比如日期格式化等函数）。这些功能可能在其他数据库中不可用或具有不同的实现方式。尽量使用通用的SQL语句和功能。如果不通用，应在程序中处理。
   
6. 禁止持久层使用泛型类接收数据，例如Map<String,Object>，使用实体类来接收数据。因为不同数据库返回的列名大小写不固定。

# 常见问题说明
## 1. sophic的数据表问题
对于sophic平台建模的数据表，个别类型，对于不同数据库，数据类型差异比较大。比如时间戳类型，在mysql数据库中类型是：无符号int(8)类型，但是在达梦、oracle等数据库中是timestamp(3)类型。那么对于java程序，是无法统一对应的。

### 解决办法：
使用mybatis的数据类型处理器，统一处理为java 统一的类型，这样在java crud时，对于java程序来说，可以专注业务，不用关心不同数据库类型的差别。
比如：数据库的int类型、timestamp、date、datetime等类型，统一映射为java的Date类型。那么java在接收数据以及传参（增删改以及查询过滤条件参数），统一类型为Date。这样便统一了java程序。


## 2. 字段大小写问题
对于不同类型数据库，比如mysql查询出来的列应该是小写，那么在java查询数据，使用Map<String,Object>接收时，其中数据库中列名对应Map中的key，这个key名字是小写，但是在达梦数据库中，会是大写。那么在取得数据之后，做数据处理的时候或者前端获取对应列数据的时候，这个key大小写就不固定。

### 解决办法：
1. 持久层，统一使用实体类接收，禁止使用泛型类接收。
2. 对于已开发系统，mybaits提供了Map key转换类接口，可以自定义类去统一key命名规则


