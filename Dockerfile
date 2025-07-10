# Multi Stage Docker setup to build and run

# use maven as the base
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# copy the source code
COPY pom.xml .

COPY src ./src

# change the working directory
WORKDIR /app

RUN mvn clean package --no-transfer-progress -DskipTests

# move to the next stage with distroless java17 version (light weight)
FROM gcr.io/distroless/java17

WORKDIR /app

COPY --from=build /app/target/cachegrid-0.0.1-SNAPSHOT.jar ./cachegrid-0.0.1.jar

CMD ["cachegrid-0.0.1.jar"]
