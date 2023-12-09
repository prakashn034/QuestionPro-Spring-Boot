# define base docker image
FROM openjdk:17
LABEL maintainer="prakash-nayak"
ADD target/grocery-0.0.1-SNAPSHOT.jar grocery.jar
ENTRYPOINT ["java", "-jar", "grocery.jar"]