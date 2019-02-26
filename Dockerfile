FROM openjdk:11.0.2-jdk
COPY api-1.0.jar /opt/
EXPOSE 80
WORKDIR /opt/
CMD ["java", "-Xms128m", "-Xmx1500m", "-Dfile.encoding=UTF-8", "-jar", "api-1.0.jar"]