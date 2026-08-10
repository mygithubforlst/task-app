---
title: JAVA日志打印规范
date: 2025-03-20 10:45:35
permalink: /pages/java-log
author: admin
categories: 
  - 开发规范
tags: 
  - 
---
# JAVA日志打印规范

## 为什么要规范日志

**日志是什么？**维基百科的定义是记录服务器等电脑设备或软件的运作。日志文件提供精确的系统记录，根据日志最终定位到错误详情和根源。日志的特点是，它描述一些离散的（不连续的）事件。例如：应用通过一个滚动的文件输出 INFO 或 ERROR 信息，并通过日志收集系统，存储到一些存储引擎（Elasticsearch）中方便查询。

规范的日志是养成良好编程习惯的开始，也是关键时刻解决严重BUG的救命稻草。程序员开发的过程中可以打印debug日志，在复杂业务中提供日志来排查问题，也可以在出现生产问题的时候快速问题，及时处理。无论如何了解和学习日志的规范是程序员必备的基本功。

## 日志作用

- 线上问题定位。日志主要的作用，核心业务必须要具备完整的日志以便于问题排查。程序出异常或者出故障时快速的定位问题，方便后期解决问题。因为线上生产环境无法 debug，在测试环境去模拟一套生产环境，费时费力。所以依靠日志记录的信息定位问题，这点非常重要。还可以记录流量，后期可以通过 ELK（包括 EFK 进行流量统计）。
- 打印调试。用日志来记录变量或者某一段逻辑。记录程序运行的流程，即程序运行了哪些代码，方便排查逻辑问题。在开发和测试中可以通过debug日志调试，在关键部分添加debug日志有利于测试的准确性，开发也可以借助Debug日志进行自测。
- 用户日志行为。主要是记录一些用户敏感操作，用于监控或者运营团队反馈客户问题使用，这些行为一半具备一定的产品规范。记录用户的操作行为，用于大数据分析，比如监控、风控、推荐等等。这种日志，一般是给其他团队分析使用，而且可能是多个团队，因此一般会有一定的格式要求，开发者应该按照这个格式来记录，便于其他团队的使用。当然，要记录哪些行为、操作，一般也是约定好的，因此，开发者主要是执行的角色。
- 扯皮。主要是第三方对接的时候，如果出现类似对面突然改返回参数赖账的情况下可以拿日志作为证据。或者运营误操作也可以用日志讲道理。在关键地方记录日志。方便在和各个终端定位问题时，别人说时你的程序问题，你可以理直气壮的拿出你的日志说，看，我这里运行了，状态也是对的。这样，对方就会乖乖去定位他的代码，而不是互相推脱。

## 什么时候记录日志?

记录日志主要查看下面几个点：

- **初始化参数**：系统或者服务的启动参数。核心模块或者组件初始化过程中往往依赖一些关键配置，根据参数不同会提供不一样的服务。务必在这里记录 INFO 日志，打印出参数以及启动完成态服务表述。
- **编程语言提示异常**：如今各类主流的编程语言都包括异常机制，业务相关的流行框架有完整的异常模块。这类捕获的异常是系统告知开发人员需要加以关注的，是质量非常高的报错。应当适当记录日志，根据实际结合业务的情况使用 WARN 或者 ERROR 级别。
- **业务流程预期不符**：除开平台以及编程语言异常之外，项目代码中结果与期望不符时也是日志场景之一，简单来说所有流程分支都可以加入考虑。取决于开发人员判断能否容忍情形发生。常见的合适场景包括外部参数不正确，数据处理问题导致返回码不在合理范围内等等。
- **系统核心角色，组件关键动作**：系统中核心角色触发的业务动作是需要多加关注的，是衡量系统正常运行的重要指标，建议记录 INFO 级别日志，比如电商系统用户从登录到下单的整个流程；微服务各服务节点交互；核心数据表增删改；核心组件运行等等，如果日志频度高或者打印量特别大，可以提炼关键点 INFO 记录，其余酌情考虑 DEBUG 级别。
- **第三方服务远程调用**：微服务架构体系中有一个重要的点就是第三方永远不可信，对于第三方服务远程调用建议打印请求和响应的参数，方便在和各个终端定位问题，不会因为第三方服务日志的缺失变得手足无措。

## 日志记录原则

- **隔离性：**日志输出不能影响系统正常运行；
- **安全性：**日志打印本身不能存在逻辑异常或漏洞，导致产生安全问题；
- **数据安全：**不允许输出机密、敏感信息，如用户联系方式、身份证号码、token 等；
- **可监控分析：**日志可以提供给监控进行监控，分析系统进行分析；
- **可定位排查：**日志信息输出需有意义，需具有可读性，可供日常开发同学排查线上问题。

## 日志级别选择

日志主要的级别如下，日志等级从小到大分别如下:

- **DEBUG**：DEBUG日志主要是开发是阶段使用，使用场景通常是开发和测试阶段对于一些关键操作是否执行的输出，开发人员可以把各种内容详细记录到Debug信息，尽可能的在开阶段发现和排查问题。
- **INFO**：INFO日志包含了关键的日志信息，主要作用是保留工作期间的信息，开发人员可以保留关键日志便于运维提取关键逻辑的执行日志信息，因为INFO日志会在线上日志控制台实时打印，所以需要保留最为关键的信息，建议在完成之后本地调整为INFO级别测试。
- **WARN**：警告信息和ERROR并不是很好区分，然而实际上只要把WARN和ERROR的日志级别考虑为有影响但是对于业务流程影响不是特别大的行为进行警告区分即可，比如参数存在异常的情况，需要进行后续日志分析。
- **ERROR**：遇到严重影响业务执行的场景就需要打印Error日志，如果影响不是特别大，只是需要关注问题的情况则打印WARN 级别日志。

### DEBUG / INFO 的选择

DEBUG 级别本身比INFO级别低，并且线上通常开启INFO级别日志，DEBUG日志用来本地和测试最为合适，而INFO则是给运维或者反馈给运营的有力证据，INFO级别日志不能输出无意义或者无价值的信息，一定是关键信息才会输出INFO日志。

- 如果代码为核心代码，执行频率非常高，务必推敲日志设计是否合理。
- 日志的可读性，自己review代码。
- 注意日志公有化在多线程环境下的打印会互相打断。

### WARN / ERROR 的选择

和上文的描述类似，当遇到用户的敏感操作或者出现意外结果但是不产生事故的情况可以使用WARN进行警告，如果存疑可以后续查看WARN日志排查。而ERROR是需要技术上线排查问题的比较严重的情况使用，所以开发过程需要谨慎考虑ERROR的打印位置。

ERROR的核心要点是下面几个：

- 发生了什么问题，哪些功能受到影响
- 获取帮助信息：直接帮助信息或帮助信息的存储位置
- 通过报警知道解决方案或者找何人解决

**常见的 WARN 级别异常**

- 用户输入参数错误
- 非核心组件初始化失败
- 后端任务处理最终失败（如果有重试且重试成功，就不需要 WARN）
- 数据插入幂等

**常见的 ERROR 级别异常**

- 程序启动失败
- 核心组件初始化失败
- 连不上数据库
- 核心业务访问依赖的外部系统持续失败
- OOM

**不要滥用 ERROR 级别日志。**一般来说在配置了告警的系统中，WARN 级别一般不会告警，ERROR 级别则会设置监控告警甚至电话报警，ERROR 级别日志的出现意味着系统中发生了非常严重的问题，必须有人立即处理。

错误的使用 ERROR 级别日志，不区分问题的重要程度，只要是问题就采用 ERROR 级别日志，这是极其不负责任的表现，因为大部分系统中的告警配置都是根据单位时间内 ERROR 级别日志出现的数量来定的，随意打 ERROR 日志将会造成极大的告警噪音，造成重要问题遗漏。

## 规范建议

### 1. 建议使用恰当的日志级别

- **error**：比较严重的问题，影响正常业务运行
- **warn**：对业务影响不大，但是需要**开发注意**
- **info**：用于日常排查问题的关键信息，接口入参和出参等等
- **trace**：详细信息，日志文件级别
- **debug**：仅仅用于开发或者测试查看重要的内部逻辑细节，但是和线上的业务关系不是特别密切

### 2*. 建议打印关键方法出参入参

凡是和接口有关的日志，以及**关键方法**的入参和返回值都建议加上日志。

### 3. 建议使用合适的格式格式

可以查看脚手架里的logback-spring.xml，里边对格式进行了说明

```xml
"%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n"
```

### 4*. 建议多分支条件分支首行打印

**if...else...或者switch**等如果分支条件比较多的情况下建议在进入分支之前打印一下，当然自己调试的时候也可以用这个法子判断走的是哪个分支：

```java
String type = ???;
if(log.isDebugEnbale()) {
    log.debug("当前分支类型为:{}", type);
}
if(type == "xxx") {

} else if(type == "aaa") {

}

// 或者
switch(type) {
    case "xxx":
        // ....
        break;
    case "xxx":
        // ....
        break;
}
```

### 5. 建议判断日志级别

这一条针对debug和trace这种低级别日志，同时为了减少线上调用日志打印没有日志浪费的情况：

```java
User user = new User(666L, "xxxx", "xxxx");
if (log.isDebugEnabled()) {
    log.debug("userId is: {}", user.getId());
}
```

### 6. 建议使用日志框架SLF4J中的API

人家lombok都给了一个`@Slf4j`的注解，所以用起来把。

> 实际上是因为slf4j 也是log4j 的作者写的并且做了门面兼容广受好评，然后.....然后 apach 就学过去了，适配加门面是吧，我也会！最后结果是 Java 的日志系统开源组件极度混乱，并且烂的和一坨shit一样。从这一情况也可以看出定标准是非常重要的。

### 7*. 建议使用占位符替换字符串拼接

和java编译为class的时候会使用StringBuffer 做字符串拼接操作。发现不管是大小项目，甚至到了框架也时常看见+号拼接的情况，虽然高版本的JDK这种编译优化下的影响实际上已经很小了，但是个人还是不太喜欢这种+号拼接的写法，不够优雅。

正确用法（现在的idea也会提醒让换成占位符）

```java
logger.info("Processing trade with id: {} and symbol : {} ", id, symbol);
```

使用`+`操作符进行字符串的拼接，有一定的**性能损耗**

```java
logger.info("Processing trade with id: " + id + " and symbol: " + symbol);
```

### 8. 建议使用异步的方式来输出日志

- 日志最终会输出到文件或者其它输出流中的，如果是IO性能会有要求的建议使用异步，可以显著提升IO性能。
- 使用异步的方式来输出日志。以logback为例，要配置异步，使用 **AsyncAppender**

```xml
<appender name="FILE_ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="ASYNC"/>
</appender>
```

### 9*. 建议多打印核心功能模块日志

我们的程序是实现某种业务的，那么就最好能描述清楚这个时候走到了业务过程的哪一步。

尽量使用业务相关的描述，避免在日志中输出一些业务敏感信息。

保持编码的一致，如果不能保证就尽量使用英文而不是中文。这样当我们拿到日志之后就不会因为看到一堆乱码而不知所云了

核心功能模块的日志，其实多打印一些内容是可以接受的，但是需要注意打印的日志必须要第一时间可以定位到问题所在。

### 10*. 建议按级别、按业务分离日志文件

可以把不同类型的日志分离出去，比如`access.log`，或者error级别`error.log`，都可以单独打印到一个文件里面。根据==业务模块==拆分也是一种办法，这样各自负责的模块能清晰看到日志。

### 11*. 禁止出现e.printStackTrace()

代码里不允许出现printStackTrace，printStackTrace打印出的堆栈日志跟正常输出或者业务代码执行日志是交错混合在一起的，在并发大日志输出多的情况下，查看异常日志就变更的非常困难，因为一块日志都不在一起了。而且刷控制台的时候如果堆栈信息过多，可能导致内存浪费。

不要使用的理由：

- `e.printStackTrace()`打印出的堆栈日志跟业务代码日志是交错混合在一起的，通常排查异常日志不太方便。
- `e.printStackTrace()`语句产生的字符串记录的是堆栈信息，如果信息太长太多，字符串常量池所在的内存块没有空间了,即内存满了，那么，用户的请求就卡住啦~

应该使用如下的正确用法：

```java
try{
    // 业务代码处理
}catch(Exception e){
    log.error("你的程序有异常啦",e);
}
```

### 12*. 禁止异常日志打印不完全

比如下面的日志就没有任何价值：

没有打印异常 e，无法定位出现什么类型的异常

```java
try {
    //业务代码处理
} catch (Exception e) {
    // 错误
    LOG.error('你的程序有异常啦');
}

```

只打印异常 e.getMessage()，没有记录详细的堆栈异常信息，只记录错误基本描述信息，不利于排查问题。

```java
public void doSth(){
    try{
        // 业务逻辑
        ...
    } catch (Exception e){
        log.error("execute failed", e.getMessage());
    }
}
```

正面案例：

```java
try{
    // 业务代码处理
}catch(Exception e){
    log.error("你的程序有异常啦",e);
}
```

另外需要注意，`e.getMessage()`不会记录详细的堆栈异常信息，只会记录错误基本描述信息，不利于排查问题。此外，如果使用 **Hutool** 工具，里面有一个异常信息提取的工具类比较方便。

### 13*. 禁止使用Exception全盘接收

这一条注意事项很简单，但是很关键，尽量避免使用 **Exception** 全盘接收，而是需要考虑针对具体的异常给出更多有用的日志信息，这样可以减少线上问题的排查时间。

### 14. 禁止在线上环境开启 debug

不仅仅是系统会有很多debug日志出现，还存在框架的debug日志输出问题，线上开启debug很容易打满磁盘，并且容易造成CPU的磁盘IO等待，久而久之会直接影响系统服务。

### 15*. 禁止嵌套异常

嵌套异常是最容易吞噬异常的场景，很多时候方法代码块层层嵌套会**忘记里面捕获异常，外层又捕获异常但是实际根本拿不到异常**，如果异常捕获和处理混乱，那么本身就会大大增加问题排查难度。

本条的建议是在编写设计方法或者类之前，需要==提前考虑异常如何处理==，完成整个调用之后需要及时的回顾代码。

下面是对应反例：

```java
try{
    // 业务代码处理
    try{
        // 业务代码处理
    }catch(Exception e){
        log.error("你的程序有异常啦",e);
    }
}catch(Exception e){
    log.error("你的程序有异常啦",e);
}
```

### 16*. 禁止记录异常又抛出

记录之后抛出异常是非常危险的操作，因为外层可能会因为内层捕获异常之后不会再次处理，如果是自定义异常更是难以排查问题，此外这样做法会导致**堆栈二次打印**，非常浪费系统性能，

反例如下：

```java
try{
    // 业务代码处理
}catch(Exception e){
    log.error("IO exception", e);
    throw new MyException(e);
}
```



### 17*. 禁止重复打印日志

如果日志可以用一行表示，那就尽量用一行表达含义。可使用 **additivity="false"** 避免重复打印日志。

```java
log.info("该用户是会员,Id:{}",user,getUserId());
//冗余，可以跟前面的日志合并一起
log.info("开始处理会员逻辑,id:{}",user,getUserId());
```

### 18*. 禁止出现System print

代码里禁止使用System.out.println和System.error.println语句。因为调用这两个方法没法将日志信息统一打印到日志文件里，导致日志丢失。

### 19*. 禁止在千层循环语句中打印日志

不要在**上千个** `for` 循环中打印日志，这样可能会拖垮你的应用程序，如果你的程序响应时间变慢，那要考虑是不是日志打印的过多了。

 在循环语句中打印日志会造成打印日志时的多次IO，从而降低代码性能。如果非要将for循环中的信息打印出来，建议在内存中统一记录，在循环外进行打印。

正确示例：

```java
int successCount = 0;
for(int i=0; i<2000; i++) {
    //省略代码
    if (success) {
        successCount++;
    }
}
log.info("成功{}次", successCount);
```

反例：

```java
int successCount = 0;
for(int i=0; i<2000; i++){
    //省略代码
    if (success) {
        successCount++;
    }
    log.info("第{}次调用，成功{}次", i, successCount);
```

### 20*. 禁止打印集合信息

有时候方法接口可能返回一个集合信息，而集合中的每个元素可能包含了很多字段，这样就导致大量信息被打印，造成内存和磁盘的消耗，有时候可能会导致内存溢出，磁盘耗尽。建议打印集合的id信息（如果userIds太大，比如超过几百上千的，也要避免打印）或者只打印入参信息。

```java
//正例
private List<User> getUserList(String param) {
    List<User> userList = getUserListFromDB(param);
    List<Long> userIds = extractUserIds(userList);
    //如果userIds太大也要避免打印
    log.inf(" userIds:{}", JSON.toString(userIds));
    return userList;
}
//或者只打印入参
private List<User> getUserList(String param) {
    List<User> userList = getUserListFromDB(param);
    log.inf(" param:{}", param);
    return userList;
}

//反例
private List<User> getUserList(String param) {
    List<User> userList = getUserListFromDB(param);
    log.inf(" userList:{}", JSON.toString(userList));
    return userList;
}
```

### 21*. 禁止打印敏感信息

日志中尽量不要包含敏感信息，对于敏感信息如用户身份证号码，密码可以加密后存储；以防止日志文件不慎外泄时保全用户的数据安全。

### 22*. 禁止打印没有意义的日志

不记录对于排查故障毫无意义的日志信息，日志信息一定要带有业务信息。

反面例子：

不带任何业务信息的日志，对排查故障毫无意义。

```java
public void doSth(){
    log.info("do sth and print log");
    // 业务逻辑
    ...
}
```

对于无异常分支的代码打印日志，一般流程下，异常分支都会打日志，如果没有出现异常，那就说明正常执行了。

```java
public void doSth(){
    doIt1();
    log.info("do sth 111");
    doIt2();
    log.info("do sth 222");
}
```

正面案例

日志一定要带相关的业务信息，有利于排查问题快速定位到原因。

```java
public void doSth(){
    log.info("do sth and print log, id={}", id);
    // 业务逻辑
    ...
}
```

对于打印过多的无意义日志，可以直接干掉或者以 debug 级别打印。

### 23*. 禁止打印日志的代码出现bug

打印日志的时候，代码不能有bug，否则影响正常业务。如NPE等。

```java
//正例
public void handleLogic(User user) {
    //user判空
    log.info("handleLogic begin userId:{}", user != null ? user.getId() : "");
    //...
}

//反例
public void handleLogic(User user) {
    //此处如果user为null会报NPE，导致后续业务无法执行
    log.info("handleLogic begin userId:{}", user.getId());
    //...
}
```

### 24. 禁止直接用 JSON 工具将对象转换成 String

日志单行大小必须不超过 200K

```java
public void doSth(){
    log.info("do sth and print log, data={}", JSON.toJSONString(data));
    // 业务逻辑
    ...
}
```

**分析：**

- fastjson 等序列化组件是通过调用对象的 get 方法将对象进行序列化，如果对象里某些 get 方法被覆写，存在抛出异常的情况，则可能会因为打印日志而影响正常业务流程的执行。
- 打日志过程中对一些对象的序列化过程也是比较耗性能的。首先序列化过程本身时一个计算密集型过程，费 cpu。其次这个过程会产生很多中间对象，对内存也不太友好。

**正例：**

可以使用对象的 toString()方法打印对象信息，如果代码中没有对 toString()有定制化逻辑的话，可以使用 apache 的 ToStringBulider 工具。

```java
public void doSth(){
    log.info("do sth and print log, data={}", data.toString());
    log.info("do sth and print log, data={}", ToStringBuilder.reflectionToString(data, ToStringStyle.SHORT_PREFIX_STYLE));
}
```

## 日志打印技巧

### 简单案例

直接看一些较为优秀的开源框架，或者阅读一些JDK源码的异常处理是不错的方式，这里简单介绍一些例子：

根据具体的异常信息捕获而日志打印：

```java
try {  
    File defaultAclFile = new File(fileName);  
    if (!defaultAclFile.exists()) {  
        defaultAclFile.createNewFile();  
    }  
} catch (IOException e) { 
    // 进行具体的异常信息捕获而日志打印
    log.warn("create default acl file has exception when update accessConfig. ", e);  
}
```

使用String.format 代替 + 拼接以及自定义异常的定义和抛出：

```java
try {  
    byte[] signature = sign(data.getBytes(charset), key.getBytes(charset), algorithm);  
    return new String(Base64.encodeBase64(signature), DEFAULT_CHARSET);  
} catch (Exception e) {
    // 使用String.format 代替 + 拼接
    String message = String.format(CAL_SIGNATURE_FAILED_MSG, CAL_SIGNATURE_FAILED, e.getMessage());  
    log.error(message, e);  
    // 自定义异常
    throw new AclException("CAL_SIGNATURE_FAILED", CAL_SIGNATURE_FAILED, message, e);  
}
```

有时候异常处理有着意想不到行为，我认为这种处理看上去不错但是实际上很容易“埋雷”，如果作者没有在Doc中进行相关介绍，会是十分危险的行为。↓

```java
try {  
    return Long.parseLong(value);  
} catch (NumberFormatException e) {  
    // 我认为这种处理看上去不错但是实际上很容易“埋雷”
    return new BigDecimal(value).longValue();  
}
```

### 使用建议

使用lombok加入注解的方式使用日志变量实例。

```java
import lombok.extern.slf4j.Slf4j; 
@Slf4j 
public class LogTest { 
    public static void main(String[] args) { 
        log.info("this is log test"); 
    } 
}
```

### 问题排查的日志

#### 对接外部的调用封装

程序中对接外部系统与模块的依赖调用前后都记下日志，方便接口调试。出问题时也可以很快理清是哪块的问题

```java
LOG.debug("Calling external system:{}" , parameters);  
Object result = null;  
try {  
    result = callRemoteSystem(params);  
    LOG.debug("Called successfully. result is {}" , result);  
} catch (Exception e) {  
    LOG.warn("Failed at calling xxx system." , e);  
}  
```

#### 状态变化

程序中重要的状态信息的变化应该记录下来，方便查问题时还原现场，推断程序运行过程

```java
boolean isRunning = true;  
LOG.info("System is running");  
//...  
isRunning = false;  
LOG.info("System was interrupted by {}",Thread.currentThread().getName()); 
```

#### 系统入口与出口

```java
这个粒度可以是重要方法级或模块级。记录它的输入与输出，方便定位 
    void execute(Object input) {  
    LOG.debug("Invoke parames : {}" , input);  
    Object result = null;  

    //business logical  

    LOG.debug("Method result : {}" , result);  
}  
```

#### 业务异常

任何业务异常都应该记下来

```java
try {  
    //business logical  
} catch (IOException e) {  
    LOG.warn("Description xxx" , e);  
} catch (BusinessException e) {  
    LOG.warn("Let me know anything"，e);  
} catch (Exception e) {  
    LOG.error("Description xxx", e);  
}  
void invoke(Object primaryParam) {  
    if (primaryParam == null) {  
        LOG.warn(原因...);  
        return;  
    }  
} 
```

#### 非预期执行

为程序在“有可能”执行到的地方打印日志。如果我想删除一个文件，结果返回成功。但事实上，那个文件在你想删除之前就不存在了。最终结果是一致的，但程序得让我们知道这种情况，要查清为什么文件在删除之前就已经不存在呢

```java
int myValue = xxxx;  
int absResult = Math.abs(myValue);  
if (absResult < 0) {  
    LOG.info("Original int {} has nagetive abs {}" ,myValue, absResult);  
}  
```

#### 很少出现的else情况

else可能吞掉你的请求，或是赋予难以理解的最终结果

```java
Object result = null;  
if (running) {  
    result = xxx;  
} else {  
    result = yyy;  
    LOG.debug("System does not running, we change the final result");  
}  
```

### 程序运行状态的日志

程序在运行时就像一个机器人，我们可以从它的日志看出它正在做什么，是不是按预期的设计在做，所以这些正常的运行状态是要有的。

#### 程序运行时间

```java
long startTime = System.currentTime();  
... 
LOG.info("execution cost : {} ms",  (System.currentTime() - startTime));　 
```

#### 大批量数据的执行进度

```java
LOG.debug("current progress: {}%",(currentPos * 100 / totalAmount)); 
```

#### 关键变量及正在做哪些重要的事情

执行关键的逻辑，做IO操作等等

```java
String getJVMPid() {  
    String pid = "";  
    // Obtains JVM process ID  
    LOG.info("JVM pid is {}" , pid);  
    return pid;  
}  

void invokeRemoteMethod(Object params) {  
    LOG.info("Calling remote method : {}" , params);  
    //Calling remote server  
} 
```



























