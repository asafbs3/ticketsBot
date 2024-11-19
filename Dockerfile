FROM openjdk:11
COPY target/ticketbot*.jar /usr/src/ticketbot.jar
COPY src/main/resources/application.properties /opt/conf/application.properties
CMD ["java", "-jar", "/usr/src/ticketbot.jar", "--spring.config.location=file:/opt/conf/application.properties"]

