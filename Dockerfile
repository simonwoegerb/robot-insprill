####################################################################################################
## Builder
####################################################################################################
FROM gradle:7.6.0-jdk17-alpine AS builder

WORKDIR /robot-insprill

COPY . .

RUN gradle build --no-daemon

####################################################################################################
## Final image
####################################################################################################
FROM eclipse-temurin:17.0.5_8-jre-alpine

# Copy our build
COPY --from=builder /robot-insprill/build/libs/robot-insprill*.jar /robot-insprill.jar

CMD ["java", "-jar", "robot-insprill.jar"]
