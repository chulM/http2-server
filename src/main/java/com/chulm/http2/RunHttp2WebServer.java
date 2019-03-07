package com.chulm.http2;

import com.chulm.http2.server.Http2nettyServer;

public class RunHttp2WebServer {

    public static void main(String[] args){
        /**
         * Test to Not TLS Server....
         *
         * Use SSl For ...
         */
        String host = "localhost";
        int port = 8080;

        String path = "";
        String password = "";

        Http2nettyServer server = new Http2nettyServer(host,port);
        server.setUseSsl(false);
        server.bind();
    }
}
