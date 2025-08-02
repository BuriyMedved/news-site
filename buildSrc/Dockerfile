#FROM postgres:17.5
FROM ubuntu:24.04
LABEL authors="anton"

WORKDIR /usr/news-site/auth-server

ENV JAVA_HOME=/usr/news-site/openjdk-24.0.1/jdk-24.0.1
ENV PATH=$PATH:$JAVA_HOME/bin
#VOLUME postgres_data:/var/lib/postgresql/data

ADD openjdk-24.0.1_linux-x64_bin /usr/news-site/openjdk-24.0.1