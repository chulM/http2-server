package com.chulm.http2;

import com.chulm.http2.server.Http2nettyServer;
import com.chulm.http2.server.SslContextProvider;

public class RunHttp2WebServer {

    // -Xbootclasspath/p:/Users/chulm/Documents/git/netty-http2/src/main/resources/alpn-boot-8.1.13.v20181017.jar

    public static void main(String[] args){
        String host = "localhost";
        int port = 8080;

        String prefix = System.getProperty("user.dir") +"/src/main/resources/";
        String path = prefix + "sample.jks";
        String password = "changeIt";
        SslContextProvider.setCertPath(path);
        SslContextProvider.setCertPassword(password);

        Http2nettyServer server = new Http2nettyServer(host,port);
        server.setUseSsl(true);
        server.bind();
    }
}
