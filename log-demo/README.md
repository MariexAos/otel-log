# Spring Boot Log4j2 示例项目

这是一个使用Spring Boot和Log4j2的示例项目，展示了如何使用Log4j2输出文本格式的日志。

## 项目特点

- 使用Spring Boot 3.4.4
- 配置Log4j2输出文本格式日志
- 包含控制台和文件两种日志输出方式
- 提供不同级别日志的示例

## 项目结构

```
spring-boot-log4j2-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── demo/
│   │   │               ├── controller/
│   │   │               │   └── LogDemoController.java
│   │   │               ├── service/
│   │   │               │   └── LogDemoService.java
│   │   │               └── SpringBootLog4j2DemoApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── log4j2.xml
├── pom.xml
└── README.md
```

## 运行方法

使用Maven构建并运行项目：

```bash
mvn clean package
java -jar target/spring-boot-log4j2-demo-0.0.1-SNAPSHOT.jar
```

或者直接在IDE中运行`SpringBootLog4j2DemoApplication`类。

## API接口

- `/api/logs/test?message=自定义消息` - 测试不同级别的日志输出
- `/api/logs/task?taskName=自定义任务名` - 测试服务执行任务时的日志输出

## 日志配置

- 日志配置文件：`src/main/resources/log4j2.xml`
- 日志文件存储位置：`logs/application.log`
- 日志级别：根日志级别为INFO，demo包下日志级别为DEBUG 