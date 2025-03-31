FROM openjdk:21
COPY target/fashion-trunk-api.jar fashion-trunk-api.jar
ENTRYPOINT ["java", "-jar", "fashion-trunk-api.jar"]
EXPOSE 8080