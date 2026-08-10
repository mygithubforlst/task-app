---
title: restful接口设计规范
date: 2024-01-30 15:45:35
permalink: /pages/restful-api
author: admin
categories: 
  - 开发规范
tags: 
  - 
---

## 接口要求

#### 接口格式
`URI = scheme "://" service "/" authority "/" path "/" controller[ "?" query ]`

service为服务id，系统通过服务ID实现网关的路由

authority为权限资源表达式，通常指菜单的权限表达式，网关根据这个权限表达式来判断菜单和接口的对应关系，从而根据用户对应的菜单权限来判断用户能否访问这个接口,也可以不配置authority，z在网关配置权限和接口的对应关系来实现接口鉴权

path为接口路径，通常是资源类别表达，path中的/可以有任意数目

controller为控制器定义，见下文控制器类型，审计日志根据controller判断审计日志事件类型

query为请求参数，通常是具体资源对象

原则上不推荐按照常见rest接口那样将变量作为路径参数，这将导致网关基于访问请求url实现权限校验时需要正则，导致校验效率变差。


#### 返回定义
| 参数名称         | 说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
| code     |错误码      |    string   |       |
| data     |响应数据      |    object   |       |
| msg     |响应消息      |    string   |       |
| succ     |响应状态      |    boolean   |       |
| timestamp     |时间戳      |    int64   |       |

succ操作是否成功告诉审计日志服务该项操作的结果
    
msg操作内容是该次操作的人工可读内容，例如删除了ID为某某的设备

timestamp即为操作时间戳

code是错误码，告诉前端如何处理，例如：

鉴权失败的错误码：
| 错误码         | 说明                             |
| ------------ | -------------------|
| 2010     |缺少权限      | 

鉴权失败应跳转401页面
认证失败的错误码：
| 错误码         | 说明                             |
| ------------ | -------------------|
| 2004     |token无效      | 
| 2007     |token过期      |    
| 2008     |缺少token      | 
| 2009     |解析token失败  |  
认证失败应跳转登录页面

## URI格式规范

  * URI(Uniform Resource Identifiers) 统一资源标示符
  * URL(Uniform Resource Locator) 统一资源定位符

URI的格式一般定义如下：  
`URI = scheme "://" authority "/" path [ "?" query ] `

URL是URI的一个子集(一种具体实现)，对于REST API来说一个资源一般对应一个唯一的URI(URL)。在URI的设计中，我们会遵循一些规则，使接口看起透明易读，方便使用者调用。同时，结合应用场景的需求，我们也会对URI有一些格式上的要求。

  * **关于分隔符“/”的使用**

"/"分隔符一般用来对资源层级的划分，例如  
`http://api.canvas.restapi.org/shapes/polygons/quadrilaterals/squares`

对于REST API来说，"/"只是一个分隔符，并无其他含义。为了避免混淆，"/"不应该出现在URL的末尾。例如以下两个地址实际表示的都是同一个资源：  
`http://api.canvas.restapi.org/shapes/`  
`http://api.canvas.restapi.org/shapes`

REST API对URI资源的定义具有唯一性，一个资源对应一个唯一的地址。为了使接口保持清晰干净，如果访问到末尾包含 "/" 的地址，服务端应该301到没有 "/"的地址上。当然这个规则也仅限于REST API接口的访问，对于传统的WEB页面服务来说，并不一定适用这个规则。

  * **URI中尽量使用连字符"-"代替下划线"_"的使用**

连字符"-"一般用来分割URI中出现的字符串(单词)，来提高URI的可读性，例如：  
[http://api.example.restapi.org/blogs/mark-masse/entries/this-is-my-first-post](http://api.example.restapi.org/blogs/mark-masse/entries/this-is-my-first-post)

使用下划线"_"来分割字符串(单词)可能会和链接的样式冲突重叠，而影响阅读性。但实际上，"-"和"_"对URL中字符串的分割语意上还是有些差异的："-"分割的字符串(单词)一般各自都具有独立的含义，可参见上面的例子。而"_"一般用于对一个整体含义的字符串做了层级的分割，方便阅读，例如你想在URL中体现一个ip地址的信息：210_110_25_88 .

  * **URI中统一使用小写字母**

根据RFC3986定义，URI是对大小写敏感的，所以为了避免歧义，我们尽量用小写字符。但主机名(Host)和scheme（协议名称:http/ftp/...）对大小写是不敏感的。

  * **URI中不要包含文件(脚本)的扩展名**

例如 .php .json 之内的就不要出现了，对于接口来说没有任何实际的意义。如果是想对返回的数据内容格式标示的话，通过HTTP Header中的Content-Type字段更好一些。

### 资源的原型

  * **文档(Document)**

文档是资源的单一表现形式，可以理解为一个对象，或者数据库中的一条记录。在请求文档时，要么返回文档对应的数据，要么会返回一个指向另外一个资源(文档)的链接。以下是几个基于文档定义的访问资源URI例子：  
`http://api.soccer.restapi.org/league/access?leagueId=seattle` 
`http://api.soccer.restapi.org/leagues/team/access?teamId=trebuchet` 
`http://api.soccer.restapi.org/leagues/teams/player/access?playerId=xxx`

league、team、player都可以看作是一个基于变量的资源对象的描述

这里的access是控制器，如果不填，默认就是access，因此如果是查询资源，不用填入控制器

`http://api.soccer.restapi.org/league?leagueId=seattle` 
`http://api.soccer.restapi.org/leagues/team?teamId=trebuchet` 
`http://api.soccer.restapi.org/leagues/teams/player?playerId=xxx`

  * **集合(Collection)**

集合可以理解为是资源的一个容器(目录)，我们可以向里面添加资源(文档)。例如：  
`http://api.soccer.restapi.org/leagues/team/create` 
`http://api.soccer.restapi.org/leagues/teams/player/create`

leagues、teams都可以看作是资源容器的具体描述

  * **控制器(Controller)**

控制器资源模型，可以执行一个方法，支持参数输入，结果返回。 是为了除了标准操作:增删改查(CRUD)以外的一些逻辑操作。控制器(方法)一般定义子URI中末尾，并且不会有子资源(控制器)。例如我们向用户重发ID为245743的消息：  
`POST /alert/resend?id=245743`

 * 理论上来说，**CRUD**的操作不要体现在URI中，HTTP协议中的操作符已经对CRUD做了映射。


但是由于应用场景限制了HTTP协议中的方法为GET/POST，我们约定通过控制器表达CRUD操作，即控制器中的create/delete/update/access

例如删除的操作用REST规范执行的话，应该是这个样子：  
`POST /user/delete?userId=1234`

以下是几个错误的示例：  
`GET /deleteUser?id=1234`  
`GET /deleteUser/1234`  
`DELETE /deleteUser/1234`  

#### 控制器类型
*注意：*
*1.大写的控制器类型仅限特殊应用使用*
*2.如果不填入控制器，系统默认该请求控制器类型为access*

| 控制器类型         | 说明           | 
| ------------ | -------------------|
| LOGIN      |登录      |  
| LOGOUT     |登出      |  
| access     |访问      |    
| create     |创建      |    
| update     |更新      |     
| delete     |删除      |   
| clear      |清空      |  
| reset      |重置      |  
| disable    |注销      |  
| active     |激活      |  
| memo       |记录      |  
| exec       |执行      |  
| call       |调用      |  
| install    |安装      |  
| upgrade    |升级      |  
| uninstall  |卸载      | 
| overLimit  |越限      | 
| overPower  |越权      | 
| relate     |关联      | 
| confirm    |确认      | 
| download    |下载      | 
| upload    |上传      |
| send    |发送      | 
| receive    |接受      | 

### URI命名规范

  * 文档(Document)类型的资源用**名词(短语)单数**命名
  * 集合(Collection)类型的资源用**名词(短语)复数**命名
  * 控制器(Controller)类型的资源用**动词(短语)**命名
  * URI中有些字段可以是变量，在实际使用中可以按需替换

例如一个资源URI可以这样定义：  
`http://api.soccer.restapi.org/leagues//access?leagueId=xxx`  
其中：leagues是集合,access是控制器的访问类型,leagueId是变量

原则上不推荐将文档/集合变量作为路径参数，这将导致网关基于访问请求url实现权限校验时需要正则，导致校验效率下降。
 

### URI的query字段
在REST中,query字段一般作为查询的参数补充，也可以帮助标示一个唯一的资源。但需要注意的是，作为一个提供查询功能的URI，无论是否有query条件，我们都应该保证结果的唯一性，一个URI对应的返回数据是不应该被改变的(在资源没有修改的情况下)。HTTP中的缓存也可能缓存查询结果，这个也是我们需要知道的。

  * Query参数可以作为集合类型资源的过滤条件来使用

例如：  
`GET /users/access //返回所有用户列表`  
`GET /users/access?role=admin //返回权限为admin的用户列表`

  * Query参数可以作为集合资源列表分页标示使用

如果是一个简单的列表操作，可以这样设计：  
`GET /users/access?pageSize=25&pageStartIndex=50`  
如果是一个复杂的列表或查询操作的话，我们可以为资源设计一个Collection，因为复杂查询可能会涉及比较多的参数，建议使用Post的方式传入，例如这样：  
`POST /users/page/access`


## 返回值格式的设计

### 响应参数描述


| 参数名称         | 说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
| code     |错误码      |    string   |       |
| data     |响应数据      |    object   |       |
| msg     |响应消息      |    string   |       |
| succ     |响应状态      |    boolean   |       |
| timestamp     |时间戳      |    int64   |       |

succ操作是否成功告诉审计日志服务该项操作的结果
    
msg操作内容是该次操作的人工可读内容，例如删除了ID为某某的设备

timestamp即为操作时间戳
  
code是错误码，告诉前端如何处理，例如：

鉴权失败的错误码：
| 错误码         | 说明                             |
| ------------ | -------------------|
| 2010     |缺少权限      | 

鉴权失败应跳转401页面
认证失败的错误码：
| 错误码         | 说明                             |
| ------------ | -------------------|
| 2004     |token无效      | 
| 2007     |token过期      |    
| 2008     |缺少token      | 
| 2009     |解析token失败  |  
认证失败应跳转登录页面

## 数据格式设计

### HTTP Headers

  * **Content-Type** 标示body的数据格式
  * **Authorization** bearer token
  
通常Content-Type为**application/json;charset=UTF-8**

Authorization 为token参数所在字段

其值为："`token类型` `token`"
示例如下:
```
    bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MGEyMTY2MDk0N2M0NGE2YTQwMDMwMTA5MjE0ZjMxMyIsInZlciI6IjEuMCIsInVzZXJfbmFtZSI6IndlYiIsInByaSI6WyJNQVNURVJfQURNSU4iLCJBRE1JTiJdLCJsYXN0X2xvZ2luIjoxNTU0Nzc3NzkzMDAwLCJzY29wZSI6WyJhbGwiXSwiaXNzIjoiYXV0aF9zZXJ2ZXIiLCJleHAiOjE1NTQ4NjUwMzIsImp0aSI6IjY4ODExNjEzLTYyYzgtNDA5MC04MmE2LTc3ZTM4MmQzNTJhZiIsImNsaWVudF9pZCI6ImZyb250ZW5kIn0.Gtw9nyeD8WbkIm9D8O_tPRjXNbE6NlAk_2DssFklUOC4Nbo1_cLVWb5yxZA-6r1ifDtW_pa7jwQzbocxqANXznwFJI-ycGY16FpGhxttB_b9ALrFioQmGxtMm5qE4BEFrgjXDHhF3E3_-eSNMwUB0ZO3zYbH7C_n3CheujVMv-g
```

### 响应描述


| 参数名称         | 说明                             |    类型 |  schema |
| ------------ | -------------------|-------|----------- |
| code     |错误码      |    string   |       |
| data     |响应数据      |    object   |       |
| msg     |响应消息      |    string   |       |
| succ     |响应状态      |    boolean   |       |
| timestamp     |时间戳      |    int64   |       |

succ操作是否成功告诉审计日志服务该项操作的结果
    
msg操作内容是该次操作的人工可读内容，例如删除了ID为某某的设备

timestamp即为操作时间戳
  
code是错误码，告诉前端如何处理，例如：

鉴权失败的错误码：

| 错误码         | 说明                             |
| ------------ | -------------------|
| 2010     |缺少权限      | 

鉴权失败应跳转401页面
认证失败的错误码：

| 错误码         | 说明                             |
| ------------ | -------------------|
| 2004     |token无效      | 
| 2007     |token过期      |    
| 2008     |缺少token      | 
| 2009     |解析token失败  |  
认证失败应跳转登录页面

### body的格式

  * json是一种流行且轻便友好的格式，json是一种无序的键值对的集合，其中key是需要用双引号引起来的，value如果是数字可以不用双引号，如果是非数字的格式需要使用双引号。
    
    这是一个json格式的例子： { "firstName" : "Osvaldo", "lastName" : "Alonso", "firstNamePronunciation" : "ahs-VAHL-doe", "number" : 6, "birthDate" : "1985-11-11" } 

  * json是允许大小写混用命名的，但要避免使用特殊符号
  * 除了json我们也可以使用其他常用的格式，例如xml,html等
  * body本身只应包含资源相关的信息，不要附加其它传输状态的信息

### 错误响应描述

  * 错误信息的格式应该保持一致，例如用以下方式(json格式):
    
    { 
      "succ" : false, //接口返回出错
      "code" : "2004", //错误码
      "msg" : "token无效" //错误具体描述 } 
    
    
  如果有多个错误，可以用json数组来描述msg

  * 错误类型需要保持统一

## 客户端关注的问题

### 接口版本管理

  * 一个资源，只用一种单一的URI来标示，资源的版本不应该体现在URI中
  * 资源的版本是可以由客户端来指定的，并且提供向后兼容
  * ETag可以用来管理资源的版本，有助于客户端缓存的应用

### 接口的安全

  * 前端访问后端时使用OAuth token认证
  * 在URI中加入权限资源的描述（通常跟在服务名后面）


> 本文内容参考/引用于:  
Mark.Masse《REST.API.Design.Rulebook》
