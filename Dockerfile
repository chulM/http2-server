FROM java:openjdk-8-jdk

WORKDIR /prod/program/bootapp
ADD ./build/libs/http2-app.jar app.jar
ADD ./src/main/resources/sample.jks sample.jks

# images jdk version 1.8.0.111
#ADD ./src/main/resources/alpn-boot-8.1.9.v20160720.jar alpn-boot.jar
ADD set_env.sh set_env.sh

RUN apt-get update
RUN apt-get install net-tools
RUN apt-get install -t jessie-backports -y openssl
RUN apt-get -t jessie-backports install -y nginx

COPY ./nginx/nginx /etc/nginx

EXPOSE 80
EXPOSE 443

VOLUME ["/prod/logs/bootapp", "/prod/logs/bootapp/gc"]


CMD service nginx restart &&
#java -Xbootclasspath/p:/prod/program/bootapp/alpn-boot.jar \
-Dhttp2.cert.path=/prod/program/bootapp/sample.jks \
-Dhttp2.cert.password=tobe0701 -Dserver.host=localhost -Dserver.port=8081 -jar app.jar
