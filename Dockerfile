FROM java:openjdk-8-jre

WORKDIR /prod/program/bootapp
ADD ./build/libs/http2-app.jar app.jar

EXPOSE 8080
VOLUME ["/prod/logs/bootapp", "/prod/logs/bootapp/gc"]
CMD java -jar app.jar