FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/PaymentService.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]