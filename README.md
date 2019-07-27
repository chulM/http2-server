# netty-http2
- http2 codec with netty framework
- jdk 1.8 or Later
- TLS 1.2 or Later

### requirement
- https://netty.io/wiki/requirements-for-4.x.html  

# demo

### netty-server
- https://netty.io/4.1/xref/io/netty/example/http2/helloworld/server/package-summary.html

### netty-client
- https://netty.io/4.1/xref/io/netty/example/http2/helloworld/client/package-summary.html

### Javascript
- https://github.com/http2/http2-test


# run
- docker

```docker
docker-compose up
```

# classpath set ALPN 
- application run with jetty
```sh
jvm args : -Xbootclasspath/p:{path}/alpn-boot-${version}.jar
```

- gradle setting 
```groovy
runtime('io.netty:netty-tcnative-boringssl-static:2.0.20.Final')
```

# issues

- Web Browser로 접속 시 반드시 https를 사용해야 한다.
- 따라서 http/2를 지원하려면, https를 반드시 추가해야한다.

- http2를 지원하는 브라우저는 반드시 ALPN(Application-Layer Protocol Negotiation)를 사용해야한다.
  (https://github.com/wonism/TIL/blob/master/back-end/nginx/http2.md)

- jetty로 alpn 설정 시 jdk version과 jetty-alpn-boot version targeting이 맞게 설정되어야한다.
  (http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-starting)


# nginx

- OpenSSL version 1.0.2 부터 HTTP/2 지원
- nginx proxy does not support backend HTTP/2.(only push on) 

- proxy pass를 위한 http/2 지원에 대한 계획은 없다.
- http/2의 이점은 단일연결로 많은 요청을 수행할 수 있다는 점이고, nginx 웹서버와 브라우저간의 단일 연결만 지정한다.
- browser HTTP/2 req -> nginx HTTP/1.1 req (proxy)-> myserver

```bash
server {
  listen 443 ssl http2;
  listen [::]:443 ssl http2;

    
  ssl_certificate /etc/nginx/conf.d/sample.pem;
  ssl_certificate_key /etc/nginx/conf.d/sample.key;
  ssl_session_timeout 1d;
  ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
  ...
  
 }
 
 
  location / {
    proxy_pass https://localhost:8081/;
    proxy_redirect off;

    proxy_http_version 1.1;
    ...
    }
```

### reference

- https://http2.github.io/
- https://hpbn.co/http2/
- https://tools.ietf.org/html/rfc7540
- https://developers.google.com/web/fundamentals/performance/http2/    
- https://tools.ietf.org/html/rfc7540#section-5.1.1
