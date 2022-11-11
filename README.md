# 高轨空间态势显示系统（hos）

## 概述
**hos**动力学系统下空间天气子系统，对各项空间环境指标进行图形化展示，以及空间环境相关算法计算。  

## 开发工具
|  工具 |  地址 |
|---|---|
| Intellij IDEA | https://www.jetbrains.com/idea/downloa  |
|  Postman | https://www.getpostman.com/downloads/  |
|  Navicat| https://www.navicat.com.cn/ |
## 后端技术
| 技术                     | 说明              |   地址   |
|------------------------|-----------------| ---- |
| Spring Boot            | 新一代 JavaEE 开发标准 |   [GitHub](https://github.com/spring-projects/spring-boot)   |
| MyBatisPlus            | MyBatis 的增强工具   |   [官网](https://mybatis.plus)   |
| Spring Cloud OpenFeign | 声明式 REST 客户端    | [官网](https://spring.io/projects/spring-cloud-openfeign) |
| Swagger【knife4j增强】     | API 文档生成工具      |   [GitHub](https://github.com/swagger-api/swagger-ui)   |
| HikariCP               | 数据库连接池          |   [GitHub](https://github.com/brettwooldridge/HikariCP)  |
| Redis                  | 内存数据库           |    [官网](https://redis.io/)    |
## 框架集成
|   集成   |   完成   |
| ---- | ---- |
|   Spring Boot   |   ✔   |
|   MyBatisPlus   |   ✔   |
| Spring Cloud OpenFeign | ✔ |
|   Swagger【knife4j增强】   |   ✔   |
|   HikariCP   |   ✔   |
|   Redis   |   ✔   |
| stk-alg-service-srv【STK算法服务】 | ✔ |
## 模块清单
|   模块名称   |   功能描述   |
| ---- | ---- |
|   hos-api   |   API接口层   |
|   hos-business   |   业务逻辑层   |
|   hos-commons   |   公共模块   |
|   hos-feign-remote   |   Feign远程调用STK算法服务   |



