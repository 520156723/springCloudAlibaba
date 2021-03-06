# spring cloud alibaba学习笔记

## spring boot

- springboot三板斧

  依赖、注解、配置

- 项目构建

  mvn clean install

- 项目启动

  java -jar  + 文件名

### spring boot actuator

  **功能**：监控检查资源，如：磁盘、数据库

  **依赖**:

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

   访问localhost:8080/actuator可以查看所有监控资源链接

   **配置** application.properties

```properties
# 激活所有actuator端点
management.endpoints.web.exposure.include=*
# 激活指定端点
management.endpoints.web.exposure.include=metrics,health,info
# health端点
management.endpoint.health.show-details=always 
# info端点
info.app-name=spring-boot-demo
info.author=demo
info.email=520156723@qq.com
# configprops 查看应用配置属性
# metrics 查看jvm
```

### spring boot 配置管理

.yml 代替 .properties

- 注意
  - 严格缩进
  - 冒号后有空格
- 优势
  - 结构清晰，类似json，省代码
  - 很多框架都用yml
  - 可以保证配置的顺序，先读上面的配置

将上面的.properties翻译如下

```yml
management:
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      exposure:
        include: '*'

info:
  app-name: spring-boot-demo
  author: hqd
  email: xxx@qq.com
```

- 配置优先级

![image-20210616235620034](C:\Users\hanqiongding\AppData\Roaming\Typora\typora-user-images\image-20210616235620034.png)

- 配置方式

  - 如果想让.yml里的属性读取环境变量

    ```yml
    management:
      endpoint:
        health:
          show-details: ${SOME_ENV}
    ```

    并在idea启动配置里的Environment variables添加SOME_ENV为always

    这样构建项目时会报错，因为环境变量配在idea里，而test目录下的启动类找不到环境变量

- 忽略单元测试的构建

  `mvn clean install -DskipTests`

- 启动项目带环境变量

  `java -jar target/spring-boot-demo-0.0.1-SNAPSHOT.jar --SOME
  _ENV=always`

- 使用外部配置文件

  spring boot会读取与jar包同级的目录的配置文件，并且优先级比jar包内的配置更高

- 用命令行参数

  - 在idea启动配置里的program arguments里添加配置

    `--server.port=8081`

  - java -jar 方式启动

    在后面加 `--server.port=8081`

### Profile

**作用**：配置不同的启动环境，如dev环境、product环境

- 如何配置

  - yml

    - ---分割

      ```yml
      # 最上一段时公用环境
      management:
        endpoint:
          health:
            show-details: always
        endpoints:
          web:
            exposure:
              include: info,health,metrics,configprops
      info:
        app-name: spring-boot-demo
        author: hqd
        email: xxx@qq.com
      # 表示激活dev环境
      spring:
        profiles:
          active: dev
      ---
      # profile=x环境
      # 在此表示开发环境
      spring:
        config:
          activate:
            on-profile: dev
      ---
      # profile=y环境
      spring:
        config:
          activate:
            on-profile: prod
      server:
        tomcat:
          max-connections: 1000
          threads:
            max: 300
      ```

  - properties

    .properties文件表示默认，要加环境就要新建文件application-xxx.properties

## 微服务的拆分与编写

### 适用场景

- 大型、复杂项目
- 快速迭代需求，如要经常发布上线
- 访问压力大

### 不适用场景

- 业务稳定
- 迭代周期长

### 微服务拆分

合理的粒度，动态改变

### 组件

- 服务网关
- 服务发现组件
- 配置服务器

![image-20210617221753388](C:\Users\hanqiongding\AppData\Roaming\Typora\typora-user-images\image-20210617221753388.png)

### 数据库设计

建表工具 mysql workbench 可以生成sql脚本

### 微信小程序开发

More ActionsAppID(小程序ID): `wx33da1c16fa6b84d7`

### 前端代码使用

下载node、vscode

clone代码

装npm

改appid

npm run dev

### 创建项目

![image-20210618230125569](C:\Users\hanqiongding\AppData\Roaming\Typora\typora-user-images\image-20210618230125569.png)

#### 通用mapper解放生产力

- spring boot 整合通用mapper

  ```xml
  <dependency>
  	<groupId>tk.mybatis</groupId>
      <artifactId>mapper-spring-boot-starter</artifactId>
      <version>版本号</version>
  </dependency>
  ```

  @mapperscan加在启动类上
  
  ```java
  @SpringBootApplication
  @MapperScan("per.hqd")//扫描该包的接口
  public class SpringBootDemoApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(SpringBootDemoApplication.class, args);
  	}
  
  }
  ```
  
  加插件依赖
  
  ```xml
  <plugin>
  				<groupId>org.mybatis.generator</groupId>
  				<artifactId>mybatis-generator-maven-plugin</artifactId>
  				<version>1.3.6</version>
  				<configuration>
  					<configurationFile>
  						${basedir}/src/main/resources/generator/generatorConfig.xml
  					</configurationFile>
  					<overwrite>true</overwrite>
  					<verbose>true</verbose>
  				</configuration>
  				<dependencies>
  					<dependency>
  						<groupId>mysql</groupId>
  						<artifactId>mysql-connector-java</artifactId>
  						<version>8.0.16</version>
  					</dependency>
  					<dependency>
  						<groupId>tk.mybatis</groupId>
  						<artifactId>mapper</artifactId>
  						<version>4.1.5</version>
  					</dependency>
  				</dependencies>
  			</plugin>
  		</plugins>
  ```
  
  
  
  加generrator.xml配置
  
  ```xml
  <!DOCTYPE generatorConfiguration
          PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
          "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
  
  <generatorConfiguration>
      <!--$的引用从下面文件中读取-->
      <properties resource="generator/config.properties"/>
  
      <context id="Mysql" targetRuntime="MyBatis3Simple" defaultModelType="flat">
          <property name="beginningDelimiter" value="`"/>
          <property name="endingDelimiter" value="`"/>
  
          <plugin type="tk.mybatis.mapper.generator.MapperPlugin">
              <property name="mappers" value="tk.mybatis.mapper.common.Mapper"/>
              <property name="caseSensitive" value="true"/>
              <property name="lombok" value="Getter,Setter,ToString"/>
          </plugin>
  
          <jdbcConnection driverClass="${jdbc.driverClass}"
                          connectionURL="${jdbc.url}"
                          userId="${jdbc.user}"
                          password="${jdbc.password}">
          </jdbcConnection>
  
          <!--生成下面三类代码-->
          <!--实体-->
          <javaModelGenerator targetPackage="pre.hqd.boot.domain.entity.${moduleName}"
                              targetProject="src/main/java"/>
  
          <!--mapper.xml-->
          <sqlMapGenerator targetPackage="pre.hqd.boot.dao.${moduleName}"
                           targetProject="src/main/resources"/>
  
          <!--mapper接口-->
          <javaClientGenerator targetPackage="pre.hqd.boot.dao.${moduleName}"
                               targetProject="src/main/java"
                               type="XMLMAPPER"/>
  
          <!--为哪张表生成代码-->
          <table tableName="${tableName}">
              <generatedKey column="id" sqlStatement="JDBC"/>
          </table>
      </context>
  </generatorConfiguration>
  ```
  
  
  
  加config.properties
  
  ```properties
  jdbc.driverClass=com.mysql.cj.jdbc.Driver
  jdbc.url=jdbc:mysql://localhost:3306/content_center?nullCatalogMeansCurrent=true
  jdbc.user=root
  jdbc.password=263519
  # 模块名称
  moduleName=user
  # 表名
  tableName=user
  ```
  
  - 注意生成代码可能会遇到多个库同表名的情况
  - `show create table 表名`  查看建表语句

#### lombok使用

​	[官方网址](www.projectlombok.org)

- @Data 组合了@Getter@Setter@ToString@RequiredArgsConstructor@EquealsAndHashCode和lombok.value

- @NoArgsConstructor生成无参构造方法

- @AllArgsConstructor生成有参构造方法

- @RequiredArgsConstructor为带final的成员生成构造方法

- @Builder用于new对象（建造者模式）

- 简化logger编写

  ```java
  package per.hqd.contentcenter;
  
  import lombok.AllArgsConstructor;
  import lombok.Builder;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  import lombok.extern.slf4j.Slf4j;
  
  @Slf4j//等同于public static final Logger logger = LoggerFactory.getLogger(LombokTest.class);
  public class LombokTest {
      //public static final Logger logger = LoggerFactory.getLogger(LombokTest.class);
      public static void main(String[] args) {
          UserRegisterDTO userRegisterDTO1 = new UserRegisterDTO();
          userRegisterDTO1.setEmail("xxx@yy.com");
          userRegisterDTO1.setPassword("zzz1");
          //等同于
          UserRegisterDTO.UserRegisterDTOBuilder userRegisterDTO2 = UserRegisterDTO.builder()
                  .email("xxx@yy.com")
                  .password("zzz2");
          
          log.info("构造出来的dto是 = {}", userRegisterDTO2);//等同于logger.info()
      }
  }
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UserRegisterDTO{
      private String email;
      private String password;
  }
  
  ```

#### 取消@Autowired爆红

- @Autowired(required = false) 表示可以不是spring容器注入的

  如果不是spring容器注入的bean会爆红

- 换@Autowired为@Resource

- 在mapper接口上加@Component或@Repository

- 避免类的每个field上都写@AutoWired的方法

  ```java
  @RestController
  @RequiredArgsConstructor(onConstructor = @__(@Autowired))
  public class TestController {
      private final ShareMapper shareMapper;
      //等同于
      //@Autowried
      //private ShareMapper shareMapper
  }
  ```

  编译后的class文件

  ```java
  @RestController
  @RequiredArgsConstructor(onConstructor = @__(@Autowired))
  public class TestController {
      @Autowired
      public TestController(final ShareMapper shareMapper) {
          this.shareMapper = shareMapper;
      }
  }
  ```

  

### 编写微服务

1. 分析业务，业务流程图、架构图
2. 设计api、数据模型（表结构）
3. 编写

[api文档](https://t.itmuch.com/doc.html#_version_information)

- RestTemplate

  - 是轻量级http client，用于http调用

  - 内容服务中去调用用户服务时使用

     

## spring cloud alibaba

[spring cloud alibaba](https://github.com/alibaba/spring-cloud-alibaba/blob/master/README-zh.md)是spring cloud的子项目

- 版本介绍

  - 语义化版本控制

    GAV中的v，如

    ```xml
    <version> 2.1.5<version>
    <!--2表示主版本；1表示次版本，架构无大变化；5表示增量版本，bug修复 -->
    <!--release：里程碑；SNAPSHOT：开发版；M：里程碑；RELEASE:正式版本 -->
    ```

    release train 发布列车，按字母顺序

    SR：service release bug修复版本

    顺序：release-》SR1 -》 SR2

### [Nacos](https://nacos.io/zh-cn/docs/what-is-nacos.html)

- 是什么？服务发现组件

  - 服务实例有定时任务向服务发现中心获取注册的服务，用本地缓存中的资源而不是每次调用前都去请求服务发现中心
  - 每个服务实例向服务注册中心发送心跳，服务发现中心由此判断实例状态

- 使用 

  - [github地址](https://github.com/alibaba/nacos/releases)

  - 版本问题：查看spring cloud alibaba的依赖中的nacos client版本是什么，下对应的nacos service

  - 启动nacos service 默认用户密码都是nacos

    解压下载包后进入bin，启动shutdown.cmd

- 服务注册到nacos

  - 加依赖 nacos discovery
  - 写配置 service addr 和 application name

- 领域模型

  - 命名空间，不同的namespace是隔离的
  - 分组，可以把不同微服务 放一个分组里管理
  - service，每个service都是一个微服务
  - cluster，一个service可有多个集群。集群可以部署到不同机房，如分别部署到杭州、北京，用来容灾
  - instance，每个cluster可以有多个实例

- 元数据

  - 分类

    1. 服务级别元数据
    2. 集群元数据
    3. 实例级别元数据

  - 作用

    提供描述信息

    让微服务调用更灵活，实现版本控制 

### Ribbon

- 是什么？客户端负载均衡器
- 怎么用？
  - 依赖：nacos discovery就引入了
  - 注解：往需要整合ribbon的bean上加@LoadBalanced
  - 配置：无配置
- 扩展。如果不满意默认配置，可以通过实现接口二次开发
- 负载均衡规则
  - 顺序循环获取实例-轮询
  - 随机实例
  - 并发请求小的实例
- 细粒度配置
  - java类实现配置@Configuration、@RibbonClient
  - yml配置文件配置（优先级高）
- 饥饿加载
- 设置权重负载均衡
  - 值越大，权重越大
  - 写一个继承AbstractLoadBalancerRule的类，并且改bean。最后改客户端上的权重
- nacos集群容灾
  - 作用：多个集群，一个集群表示某个地区的所有实例，该地区地震了导致不可用，可以找另一个集群的实例。
  - 举例子：内容中心在北京，会优先调用北京的用户中心，如果北京的用户中心挂了，就会调用南京的用户中心。
- 元数据
  - 作用：指定本服务只能调用指定版本的微服务
  - 例子：v1的用户中心只能调用v1版本的内容中心
- namespace命名空间
  - 作用：防止跨命名空间调用服务
  - 例子：public的实例不能调用dev的实例

### [Feign](https://github.com/OpenFeign/feign)

- 作用：远程http调用，原本用restTemplate。是一个声明式的http客户端。使调用某个url不再硬编码，而是想springmvc一样的接口调用。

- 源码：

  - Feign入口Feign.Builder（建造者模式），底层请求默认用httpUrlConnection（无连接池，性能不太好）。

  - 优化：todo
  - 支持springmvc的注解，是用springmvcconstract实现的
  - Encoder：将client接口对象转成http请求消息体。
  - Decoder：反之
  - Logger：日志管理器：Slf4jLogger

- 细粒度配置日志

  - 四种级别：
    - NONE（默认）：无日志
    - BASIC：仅记录请求方法、URL、响应状态码、执行时间
    - HEADERS：在BASIC基础上加上请求和响应的header
    - FULL：HEADERS基础下，加上记录请求响应的body、元数据（用于开发环境）

- 二次开发

  - 连接超时时间、解码编码器、拦截器等

- 比较

  - 属性配置简单，
  - 代码配置灵活，但线上修改需要打包、发布
  - 最佳实现：尽量用属性配置，配置方式要统一

- Feign的继承

  FeignClient和对应的服务的Controller层代码一样，可以用继承去做

  - Get请求：用Feign的继承就不能用@GetMapping+@PathVariable了，而是用@RequestMapping+@RequestParam的形式

  - Post请求：@PostMapping + @RequestBody 换成@RequestMapping+@RequestBody

- Feign与RestTemplate对比

  Feign可读性、维护性更好

  RestTemplate灵活性和性能更好

  关键是要统一

- Feign性能优化

  - 添加连接池

    feign-httpclient 

  - 日志级别

    建议BASIC

- [Feign常见问题](https://www.imooc.com/article/289005)

  - 用@PathVariable必须指定value属性
  - FeignClient里的name是可以从配置文件去读取的，支持占位符${feign.name}

### [Sentinel](https://sentinelguard.io/zh-cn/)

- 雪崩cascading failure

  某个服务挂了导致调用链上的服务都挂了

  一般是一个线程等待太久，导致资源不足

- 容错方案

  - 超时，缩短等待超时时间
  - 限流，超过阈值的请求直接拒绝
  - 仓壁模式，做隔离，比如每个controller一个线程池，池满走拒绝策略，不影响其他controller
  - [断路器模式](https://martinfowler.com/bliki/CircuitBreaker.html)，如果某个接口调用失败超过阈值，就会切成断路器打开状态，不去调用该接口，过了某个设定时间后，断路器变成半开状态，允许调用一次接口，如果成功就把断路器关闭，失败则保持断路器打开状态，等待下一次变成半开状态。
  - 是什么？轻量级的流量控制、熔断降级Java库
  - 怎么用？只要加依赖，验证整合成功：actuator/sentinel

- [Sentinel控制台 ](https://github.com/alibaba/Sentinel/releases)

  - 下载jar包，之前java -jar启动，账号密码都是sentinel
  - yml加配置

- 流控

  - 直接流控：自己接口的QPS、线程数达到阈值就拒绝请求

  - 关联流控：关联接口QPS达到阈值就让自己拒绝被请求

    - 使用场景：（保护关联资源）修改接口如果频繁请求，就应该限流查询接口

  - 链路流控（细粒度）：指定链路（资源）上的流量超过阈值就限流

    - 需要注解@SentinelResource("common")，被注解的接口作为common资源，如果这个请求的某个接口超阈值就限流这个接口

  - 拒绝策略

    - 快速失败：即直接丢弃请求，抛异常
    - Warm Up（预热）：先是把阈值设定为 阈值/codeFactor，缓慢增加阈值，预热时长结束后才达到满阈值。
      - 场景：秒杀场景，防止qps激增打挂微服务，让他慢慢增。
    - 排队等待：阈值类型需要是QPS，请求超阈值后排队等待，超过等待时间就抛弃请求。
      - 场景：突发流量，一会多流量一会又少流量，使流量匀速通过而又不丢失请求

  - 降级

    - RT：秒级平均响应时间

      - 平均响应时间（秒级统计）超出阈值&&在时间窗口内通过的请求 >= 设定值

        就会触发降级->断路器打开->时间窗口结束->关闭降级

      - RT最大为4900ms

    - 异常比例： QPS >= 5 && 异常比例（秒级统计）超过阈值 

      就会触发降级->断路器打开->时间窗口结束->关闭降级

    - 异常数：异常数（分钟统计）超过阈值

      就会触发降级->断路器打开->时间窗口结束->关闭降级

      - 时间窗口 > 60s

  - 热点规则

    - 功能：细粒度限流，可以缩小到一个请求的某个参数
    - 如果一个请求带上指定的参数，那么就会对它限流，例如QPS超过阈值限流
    - 还可以对这个参数的指定值限流，比如值为1的时候走另一个限流规则
    - 场景：适用于对某个请求中的特定参数的特定值去限流，其他的值不去限流，细粒度限流场景。比如某个接口的某些参数QPS非常高，但是其他参数不高，那么只去限制那个参数的流量，传其他参数的时候可以正常调用该接口。
    - 注意：参数必须是基本类型或者String
    - 源码：com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowChecker.passCheck()

  - 系统规则

    - load: `uptime`查看load值，1分钟内、5分钟、15分钟内的系统负载

    - 系统Load1：1分钟的load
    - 系统容量：maxQps * minRt

  - 授权规则

    - 指定某个资源只允许某个微服务访问。或者不允许某个微服务访问

- Sentinel API

  - SphU
    - 定义资源，并让资源受到监控，限流资源
  - Tracer
    - 对异常进行统计
  - ContextUtil
    - 实现调用来源，标记调用 

- SentinelResource注解

  - 同Sentinel API的功能一样，但更简洁
  
  - 定义资源
  - 对资源限流
  
- RestTemplate整合Sentinel

  - 往RestTemplate的bean里加上@SentinelRestTemplate

  - ```java
    // @SentinelRestTemplate注解中blockHandler、fallback实现异常信息的
    // 用法同@SentinelResource
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface SentinelRestTemplate {
        String blockHandler() default "";
    
        Class<?> blockHandlerClass() default void.class;
    
        String fallback() default "";
    
        Class<?> fallbackClass() default void.class;
    }
    ```

  - 源码SentinelBeanPostProcessor类中。

- Feign整合Sentinel

  - 配置文件开启
  - 限流
  - 处理异常
  - 源码org.springframework.cloud.alibaba.sentinel.feign.SentinelFeign

- 规则持久化

  - 防止每次重启服务就要重新配置规则

  - 拉模式

    - 控制台把规则推给微服务，微服务保存为本地文件
      - 这里用到了Java SPI（[Service Provider Interface)](https://www.cnblogs.com/warehouse/p/9335530.html)

  - 推模式

    - nacos做配置中心

    - 变化

      sentinel控制台不再直接跟微服务中的sentinel客户端直接交互，而是跟远程配置中心交互（nacos），把规则推到nacos上，sentinel客户端从nacos上获取规则，并监听nacos上的规则变更

    - 使用

      [懒人包](https://github.com/eacdy/Sentinel-Dashboard-Nacos/releases)

      nacos 控制台中会增加该配置

    - 优点：一致性好、性能好

    - 缺点：改动多、麻烦、引入额外依赖

- 生产环境使用sentinel

  - 阿里云的[AHAS](https://ahas.console.aliyun.com/)可做规则持久化 

- [集群流控](https://github.com/alibaba/Sentinel/wiki/集群流控)

  - 依赖token server和token client，每个微服务都是一个token client
  - 目前不可用于生产环境，废弃

- 错误页优化

  - 区分限流、降级的异常，页面上显示不同信息
  - 处理：继承UrlBlockHandler（新版本升级为BlockExceptionHandler ）

- 区分请求来源

  - 标记请求来源，对某些来源进行流控
  - 授权规则里的流控应用需要来源
  - 实现RequestOriginParser

- sentinel对Restful URL支持

  - 背景：/share/1 配置的规则对 /share/2不生效，我们想同一类的url用同一种规则

    如/share/{number}用同一种

  - 实现UrlCleaner接口 ，底层是SphU定义了同一资源

- 本质

  UrlBlockHandler、RequestOriginParser、UrlCleaner都是基于spring mvc 的过滤器

  源码CommonFilter

- [常用配置项](https://www.imooc.com/article/289562)

### 引入MQ

- 作用：把耗时操作异步返回

- spring 里的异步

  - @Async
  - WebClient

- 适用场景

  - 异步处理
  - 流量削峰填谷
  - 微服务解耦

- [各种MQ对比](https://www.imooc.com/article/290040)

- [RocketMQ搭建](https://www.imooc.com/article/290089)

  - 两个服务启动

    D:\microservice\rocketmq-all-4.5.1-bin-release\bin

    1.name server 

    ` .\mqnamesrv.cmd`

    2.broker

    `.\mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true`

    

  - [控制台搭建](https://www.imooc.com/article/290092)

    `java -jar D:\microservice\rocketmq-all-4.5.1-bin-release\bin\rocketmq-console-ng-1.0.1.jar`

    [控制台文档](https://github.com/apache/rocketmq-externals/blob/master/rocketmq-console/doc/1_0_0/UserGuide_CN.md)

- 术语与概念

  - 主题（Topic）

    一类消息的集合（类比广播的电台频道89.7）

  - 消息模型

    生产者（Producer）：生产消息

    消费者（Consumer）：消费消息

    消息代理（Borker）：消息代理，存储消息、转发消息

  - 部署结构

    Name Server(名字服务)：生产者消费者通过名字服务找到各主题相应的Broker IP列表

    Broker Server（代理服务器）：消息中转角色，负责存储消息和转发消息

  - 消费模式

    拉模式：由消费者从broker server拉消息

    推模式：由broker server主动推送给消费端

  - 传播模式

    集群：同组的消费者平均分摊消息

    广播：同组的消费者都接收到同样消息

  - 消息类型

    普通消息、顺序消息、定时/延时消息、事务消息

    

- [Rocket开发指南](https://git.imooc.com/coding-358/rocketmq-dev-guide)

- 编写生产者

  group放在配置

  xxxTemplate springboot的消息队列工具，xxx表示任意mq

- 编写消费者

  group放在@RocketMQMessageListener注解中

  @xxxMessageListener 是spring编写消费者的工具类，xxx表示任意mq

- rocketmq事务消息（二次确认）

  - 术语
    - 半消息：被标记为暂时无法消费的消息
    - 消息回查：当MQ Server 发现消息长时间处于半消息状态，回去向生产者发送请求，询问该消息的最终状态（提交或者回滚）
  - 消息的三种状态
    - commit提交状态，消费者可以消费
    - rollback回滚事务消息，broker删除该消息，消费者不能消费
    - unknown未知状态，broker需要回查确认该消息的状态

  - 简要流程
    1. 生成者发送半消息到MQServer
    2. MQServer返回生成者接受成功
    3. 生产者执行本地事务
    4. 执行完后生产者再发送确认消息给MQServer，提交/回滚
    5. 如果步骤4长时间未返回，MQServer发送消息回查
    6. 生产者检查本地事务状态，然后回复MQServer提交或回滚

- Spring Could Stream

  - 一个用于构建消息驱动的微服务框架

  - 架构

    应用、绑定器、消息中间件

    Binder(目标绑定器)：与消息中间件通信的组件

    Bindings(目标绑定)：连接应用与消息中间件的桥梁，用于消息的消费和生产。Bindings由Bingder创建

    channel:

    ​	input、output是相对于微服务来说的，消息进入微服务就是in

  - 使用

    - 依赖spring-cloud-starter-stream-rocketmq

    - 生产者：注解启动类加@EnableBinding(Source.class)

      消费者：@EnableBinding(Sink.class)

      Source接口和Sink接口是SpringCloudStream提供的发送接收消息的接口

      Processor接口继承了Source和Sink既可以发送也可以接收消息

    - 写配置

      消费者还要添加消费group配置

  - 自定义接口

    替换Source成自定义的接口去发消息，接受消息的Sink也可以替换
  
    需要在@EnableBinding({Sink.class, MySink.class})添加自定义接口MySink 
    
  - [消息过滤](https://www.imooc.com/article/290424)
  
    - 注解实现
    - tags实现
    - sql语法过滤
  
  - 监控
  
    /actuator中多了两个端点
  
    bindings端点和channels端点
  
    /actuator/health
  
    多了binders端点
  
  - [异常处理](https://www.imooc.com/article/290435)
  
    - 应用处理
    - 系统处理
  
  - [总结](https://www.imooc.com/article/290489)

### 网关SpringCloudGateway

- 为什么引入网关

  前端请求->网关->各个微服务

  多了一层，可以做统一管理，权限校验、负载均衡等

- springcloudgateway介绍

  - 底层是Netty、Reactor、WebFlux
  - 优点
    - 性能是Zuul 1.x的1.6倍，[性能对比]()(https://www.imooc.com/article/285068)
    - 功能强大
    - 容易扩展
  - 缺点
    - 不是servlet编程模型，而是Netty+Webflux，有学习成本
    - 不能在servlet容器下工作，不能打成war包，不能部署到tomcat
    - 不支持SpringBoot1.x

- 网关项目创建

  1. 新建一个项目导入选择gateway
  2. 统一springboot、springcloud版本，整合整合springcloudalibaba和nacos
  3. 配置yml

- springcloudgateway转发规则

  访问 ${GATEWAY_URL}/{微服务X}/**

  会转发到微服务X的/**路径

  例子：localhost:8040/user-center/users/1

  会转发到user-center用户的/users/1接口

### 认证预授权

- 无状态与有状态

  - 无状态

    服务器不再去维持session（记录用户的登录状态）

    实现：用户访问携带token，服务器做解密校验

    优点：去中心化，无存储，简单易扩容缩容

    缺点：服务端控制力不强（如改token过期时间）

- 常用方案

  - 处处安全方案（[[OAuth2实现单点登录SSO](https://www.cnblogs.com/cjsblog/p/10548022.html)](https://www.cnblogs.com/cjsblog/p/10548022.html)）

    - Spring Cloud Security、Keycloak

  - 外部无状态，内部有状态

    场景：老服务用的session store，新服务用的是token

    实现：用户访问带上sessionId和token，然后通过网关转发，如果转发到的是老服务用session验证，如果是新服务用token认证

  - 网关认证授权，内部裸奔

    网关去做token的解密和颁发 

  - 内部裸奔改进版

    网关不做token解密只做转发，认证授权中心服务颁发token， 每个微服务去做解密token

- 访问控制模型

  - RBAC（Role-Based access control）
    - 为用户分配角色，为角色分配权限

- JWT（json web token）：一个字符串

  - 一个jwt组成

    1.Header（头）：记录令牌类型、签名的算法

    2.Payload（有效载荷）：携带用户信息

    3.Signature（签名）：防止Token被篡改、确保安全性——计算出来的签名

  - 两个公式

    - Token = Base64(Header).Base64(Payload).Base64(Signature)

      示例：aaaa.bbbb.cccc

    - Signature = Header指定的签名算法(Base64(header).Base64(payload),秘钥)

      如HS256（"aaaa.bbbb", 秘钥）

  - [代码实现](https://www.imooc.com/article/290892)

    - jjwt实现

- 小程序登录

  - 流程：登录，执行微信的api（wx.login)，获得一个code，拿着code去调用user服务的登录接口/users/login，这个接口拿code去请求微信api，确认登录过小程序了——》如果注册了就颁发token，返回用户信息，否则先走注册。

  也就是说登录交给了微信小程序去做，user服务只需要验证用户是否已经登录成功。

  - 确认登录过小程序了

    引入小程序API工具包WxJava

    微信appId `wx33da1c16fa6b84d7` 小号登录！

    小程序密钥`d6f1114fa22d43cccfd03c0eb3819d8a`

  - 代码编写，前端需要修改请求后端的url域名

- aop实现登录状态检查

  - 做法

    - 法1：过滤器，校验token
    - 法2：拦截器，校验token
    - spring  aop，注解实现校验

  - 坑

    用feign时调用微服务时没带token 

    客户端到内容中心的header里有token，但是用feign调用用户中心时是不会在header中带token的。

- Feign传递Token

  - 通过@RequestHeader

  - 实现拦截器RequestInterceptor（Feign的拦截器）

    所有feign请求经过拦截器时，可以把token写入header。

    这里的content相当于是user的客户端，没带token

- RestTemplate传递Token

  - 通过exchange（）
  - 实现ClientHttpRequestInterceptor（RestTemplate的拦截器）

- 用户权限的验证

  注解aop实现

### 文档生成工具[Awesome](http://www.itmuch.com/other/doc-generate/)

### nacos配置中心

- 整合nacos-config

- 对应关系

  ![image-20210813222135079](C:\Users\hanqiongding\AppData\Roaming\Typora\typora-user-images\image-20210813222135079.png)

- 创文件bootstrap.yaml（自动配置）

  ```yaml
  spring:
    cloud:
      nacos:
        config:
          server-addr: 127.0.0.1:8848
          file-extension: yaml
          group: 1
    application:
      # 对应nacos上的 Date ID 为  content-center-dev.yaml
      name: content-center
    profiles:
      active: prod
  ```

  

- nacos控制台上添加配置

- 运行时修改配置生效 

  类上加@RefreshScope

- 控制台查看历史配置

- 应用的配置共享

  bootstrap.yaml的环境配置，在nacos上没有，会用默认的环境配置

  共享方式1：shared-dataids

  共享方式2：ext-config

  优先级：shared-dataids < ext-config < 自动配置

- 引导上下文

  优先级：远程配置 > 本地配置

  在远程配置中可以开启允许本地配置覆盖

  ```yaml
  spring:
    cloud:
      config:
        # 是否允许本地配置覆盖远程配置
        override-none: true
        # 一切以本地为准
        allow-override: true
        # 系统环境变量或者系统属性才能覆盖远程配置文件的配置
        # 本地配置文件中的配置优先级低于远程配置e
        override-system-properties: false
  ```

- 数据持久化

  - 服务发现组件存在：~/nacos/naming
  - 配置服务器：$NACOS_HOME/data/derby-data(内嵌的数据库)
  - 配置快照：~/nacos/config

- [搭建生产可用的nacos集群](https://www.imooc.com/article/288153)

### 调用链监控sleuth

- 调用链监控

  - 是什么

    用于快速定位问题，比如一个api调用发生异常，快速找到原因 

  - 本质

    服务a调用服务b

    一个表，记录了服务a发起调用时、a获取调用结果时、b被调用时，b返回结果时的监控数据（如，服务名、api、所处状态、时间戳）。

  - 实现工具

    spring cloud sleuth + zipkin

    skywalking + pinpoint

- sleuth

  - 是什么

    是调用链监控的客户端，在每个微服务上负责产生监控数据

  - 术语

    - span（跨度）

      每条数据就是一个span，是基本工作单元

    - trace（跟踪）

      一组span，如上面a调b产生的4个span就是一个trace

    - annotation（标注）

      - cs（client sent 客户端发送）：客户端发送一个请求，该annotation描述了span的开始。
      - sr（server received 服务端接受）：服务端获取请求并准备处理它。
      - ss（server sent服务端发送）：该annotation表明完成请求处理（当响应发回客户端时）
      - cr（client received客户端接收）：span结束的标识。

  - 使用

    spring-starter-sleuth

- zipkin

  - 是什么

    记录监控数据的数据库+界面

  - [怎么用](https://www.imooc.com/article/291572)

  - 数据持久化

    - elasticsearch

      - 搭建

        - [下载](http://www.elastic.co/cn/downloads/past-releases#elasticsearch)

          注意es7开始需要jdk11
        
      - 启动 
      
        cd bin
      
        ./elasticsearch -d （后台启动）
      
      - 访问
      
        localhost:9200
      
      - 存数据到zipkin
      
        - [配置环境变量](https://github.com/openzipkin/zipkin/tree/master/zipkin-server#environment-variables)
      
          | 环境变量     |                | desc         |
          | ------------ | -------------- | ------------ |
          | STORAGE_TYPE | elasticsearch  | 指定存储类型 |
          | ES_HOST      | localhost:9200 |              |
          |              |                |              |
      
        - 带参数启动zipkin
      
          `STORAGE_TYPE=elasticsearch ES_HOSTS=localhost:9200 java -jar zipkin-server-2.12.9-exec.jar`
    
  - 看服务的依赖关系

    [ZIpkin Dependencies](https://github.com/openzipkin/zipkin-dependencies)

    - 执行命令看依赖分析

    - 步骤：1下载 

    - 其他命令

      - 分析昨天的数据(oS/x下的命令)

      STORAGE_TYPE=elasticsearch java -jar zipkin-dependencies.jar `date -uv-1d +%F`

      - 分析昨天的数据( Linux下的命令)

      STORAGE TYPE=elasticsearch java -jar zipkin-dependencies.jar `date -u -d '1 day ago’ +%F`

      - 分析昨天的数据(oS/x下的命令)

        STORAGE_TYPE=elasticsearch java -jar zipkin-dependencies.jar `date -uv-1d +%F`


### 业务功能完善

- mybatis分页
  - 插件[mabatis-pagehelper](https://github.com/pagehelper/Mybatis-PageHelper)
  - 小心冲突，看conflicts
  - stream.peek

### 代码规范

- 注释原则

  - 每一步的主要流程
  - 核心方法
  - 条件、分支、判断前
  - 注释统计工具插件statistic

- [阿里巴巴代码规约](https://github.com/alibaba/p3c)

  改到右上角有个勾

- [SonaQube](imooc.com/article/291857)

  - 是代码质量管理系统，

### 微服务监控

- 监控组件
  - spring boot actuator
  - sentinel dashboard
  - spring cloud sleuth + zipkin

- 让spring boot actuator可视化
  - [spring boot admin](https://github.com/codecentric/spring-boot-admin)
  - [官方文档](https://codecentric.github.io/spring-boot-admin/2.1.6/)
  - loggers可动态修改日志级别
  - metrics、heapdump、threaddump端点都可以监控jvm
- java自带jvm监控工具
  - jconsole
    - 命令行输入jconsole
  - jvisualvm
    - 命令行输入jvisualvm

- 分析GC日志、线程dump日志、堆dump可视化

  - 启动参数vm option

    - `-Xmx500m -XX:+PrintGCDetails -Xloggc:gc.log`

      - 表示最大堆内存设置为5m

      - 打印gc详情

      - gc日志打印到gc.log里去

  - 对gc日志可视化分析

    - [GCEasy](https://gceasy.io/)   (替代品GCPlot)

      - 把gc日志丢进去即可

      - **Throughput**：jvm执行业务代码时间/jvm的总时间
        - jvm总时间 = jvm执行业务代码时间+gc时间+。。

  - 线程dump日志

    - [FastThread](https://fastthread.io/)   (替代品spring-boot-admin)

  - 堆dump日志

    - [HeapHero](https://heaphero.io/)   (替代品jvisualvm)

- 日志监控

  ELK ( Elasticsearch + Logstash + Kibana )

  [sleuth用ELK收集分析日志](imooc.com/article/291751)

### 完美融合异构微服务

- spring cloud微服务可以完美调用其他非spring cloud的微服务

  - 完美调用
    - 服务发现、负载均衡、容错处理

- [spring-cloud-wii](https://github.com/eacdy/spring-cloud-wii)

  

# 注解

- @Component 加上该注解的类会被扫描到spring容器中进行管理

- @Bean 代替xml形式的bean初始化

- @Repository 作用于持久层，有更多的操作数据库异常描述，加上该注解也会被扫描进spring容器

- @Service 作用于业务层，加上该注解也会被扫描进spring容器

- @Controller作用于表现层，加上该注解也会被扫描进spring容器

- @Configuration作用于配置类，代替xml配置去初始化bean常与@Bean配合使用，由于里面有@Component注解，所以也会被spring管理，之后就不再赘述。

- @SpringBootApplication
  - @ComponentScan扫描**当前启动类同级的包下**所有带@Component的类
    - 父子上下文重叠，指的是父子扫同样的包了，可能出现事务不生效等情况
    - 解决办法：把子上下文要扫的包放到springboot启动类的上一级
  
  
  
- @RestController等同于@Controller、@ResponseBody

  注在类上表示是个Controller

- @PostMapping("/test-post")等同于

  @RequestMapping(value = "/test-post", method = RequestMethod.POST)

  @GetMapping同理

  - @PostMapping和@RequestBody配合传json请求body
  - @GetMapping和@PathVariable配合传uri中占位的参数

- @RequestParam与@GetMapping配合表示uri后的参数，@RequestParam（required = false）表示不是必填

- @Aspect 注解使用实现aop

  基于cglib的类继承AOP是不能在private方法上生效的

- @Transactional(rollbackFor = Exception.class)//当发生异常时，数据库操作回滚

- 注解上的@Retention(RetentionPolicy.RUNTIME)，表示注解和其内容可以在运行期取到

- stream的peek和map区别

  map会返回操作后的新元素

  peek无返回，并且操作的是原来的元素

# 技巧

- 当想用protected方法，而不想改源代码的之后，

  可以通过继承该方法的类，来获取方法改成public。

- 字符串的比较相等用Objects.equals(a, b)更安全

- 集合判空CollectionUtils.isEmpty(list)

- 看依赖版本用Dependency Analyzer

- 抽一段代码成方法command+option+m

- 对选中的内容变成常量 command+option+c

- 列出最近打开文件  ctrl + e

- idea自带的调试，添加一个header

  ```http
  GET http://localhost:8081/users/1
  # 添加一个header
  X-token: eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwid3hOaWNrbmFtZSI6ImhxZCIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNjI4NTI2NDIwLCJleHAiOjE2Mjk3MzYwMjB9.F_dJO31Igw8SVi_kOghilEMCxhAhCDfZDaaZ7HdLy1g
  
  ```

  

# 所需启动服务

### MQ

两个服务启动

D:\microservice\rocketmq-all-4.5.1-bin-release\bin

1.name server 

` .\mqnamesrv.cmd`

2.broker

`.\mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true`

### nacos

 d: ; cd D:\microservice\nacos\bin ;  .\startup.cmd

### zipkin

d: ; cd D:\microservice\zipkin ; java -jar .\zipkin-server-2.12.9-exec.jar

访问http://127.0.0.1:9411/

### ELASTICSEARCH

`STORAGE_TYPE=elasticsearch ES_HOSTS=localhost:9200 java -jar zipkin-server-2.12.9-exec.jar`

