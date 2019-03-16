[![dependencies](https://img.shields.io/badge/netty-4.1.25-blue.svg)]()
[![dependencies](https://img.shields.io/badge/alpn_boot-8.1.3.v20181017-blue.svg)]()

# netty-http2
- http2 codec with netty framework
- jdk 1.8 or Later
- TLS 1.2 or Later

### Requirement
- https://netty.io/wiki/requirements-for-4.x.html  

# Demo

### netty-server
- https://netty.io/4.1/xref/io/netty/example/http2/helloworld/server/package-summary.html

### netty-client
- https://netty.io/4.1/xref/io/netty/example/http2/helloworld/client/package-summary.html

### Javascript
- https://github.com/http2/http2-test


# reference

- https://http2.github.io/
- https://hpbn.co/http2/
- https://tools.ietf.org/html/rfc7540
- https://developers.google.com/web/fundamentals/performance/http2/    
- https://tools.ietf.org/html/rfc7540#section-5.1.1


# Caution

- Web Browser로 접속 시 반드시 https를 사용해야 한다.
- 따라서 http/2를 지원하려면, https를 반드시 추가해야한다.

- http2를 지원하는 브라우저는 반드시 ALPN(Application-Layer Protocol Negotiation)를 사용해야한다.
  (https://github.com/wonism/TIL/blob/master/back-end/nginx/http2.md)

- jetty-alpn를 통해 classpath에 alpn 설정을 해야한다.
- jdk version과 jetty-alpn-boot version targeting이 맞게 설정되어야한다.
  (http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-starting)

