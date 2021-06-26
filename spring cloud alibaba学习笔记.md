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

     