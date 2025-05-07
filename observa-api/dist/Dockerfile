FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# 复制 Maven 配置和源代码
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java17-debian11:nonroot

WORKDIR /app

# 只复制打包后的 JAR 文件
COPY --from=builder /build/target/observa-api.jar observa-api.jar

ENTRYPOINT ["java", "-jar", "observa-api.jar"]
