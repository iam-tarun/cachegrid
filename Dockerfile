# Multi Stage Docker setup to build and run

# use maven as the base
FROM maven:3.9-openjdk-17 AS build

# copy the source code
COPY ./src ./pom.xml /app/

# change the working directory
WORKDIR /app

RUN mvn clean package --no-transfer-progress -q

# move to the next stage with jre 17
FROM eclipse-temurin:17-jre-alpine

COPY --from=build /app/target/cachegrid-0.0.1-SNAPSHOT.jar /app/cachegrid-0.0.1.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "cachegrid-0.0.1.jar"]
