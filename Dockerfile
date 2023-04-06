#FROM openjdk:17-oracle
FROM adoptopenjdk/openjdk11:ubi

ARG JAR_FILE=target/*.jar

ENV BOT_NAME=default_bot_name
ENV BOT_TOKEN=2222222222:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
ENV BOT_DB_USERNAME=dbusername
ENV BOT_DB_PASSWORD=dbuserpwd
ENV SPREADSHEETS_ID=qweqweqweqweqweqweqweqweqweq

#fix: Exception in thread ... java.lang.UnsatisfiedLinkError: /opt/java/openjdk/lib/libawt_xawt.so: libXext.so.6: cannot open shared object file: No such file or directory
RUN yum -y install libXext
#fix: Exception in thread ... java.lang.UnsatisfiedLinkError: /opt/java/openjdk/lib/libawt_xawt.so: libXrender.so.1: cannot open shared object file: No such file or directory
RUN yum -y install libXrender
#fix: Exception in thread ... java.lang.UnsatisfiedLinkError: /opt/java/openjdk/lib/libawt_xawt.so: libXtst.so.6: cannot open shared object file: No such file or directory
RUN yum -y install libXtst
#May occure: Exception in thread ... java.awt.AWTError: Can't connect to X11 window server using ':0' as the value of the DISPLAY variable.

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","-Dbot.username=${BOT_NAME}","-Dbot.token=${BOT_TOKEN}","-Dspring.datasource.username=${BOT_DB_USERNAME}","-Dspring.datasource.password=${BOT_DB_PASSWORD}","-Dgoogle.spreadsheets.id=${SPREADSHEETS_ID}","/app.jar"]