FROM news-site-base:latest
LABEL authors="anton"

WORKDIR /usr/news-site/auth-server

ENV JAVA_HOME=/usr/news-site/openjdk-24.0.1/jdk-24.0.1
ENV PATH=$PATH:$JAVA_HOME/bin

ADD build/libs/auth-server-1.0.0.jar .

ENTRYPOINT ["/usr/news-site/openjdk-24.0.1/jdk-24.0.1/bin/java", "-Dspring.profiles.active=docker", "-jar", "auth-server-1.0.0.jar"]
