FROM daocloud.io/java:openjdk-8u40-jre
MAINTAINER weitong
COPY target/happymo-1.0-SNAPSHOT.jar /momoweb.jar
ENTRYPOINT ["java", "-jar", "momoweb.jar"]
