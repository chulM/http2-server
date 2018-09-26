package Main;

import Server.Http2nettyServer;

public class Main {
    public static void main(String[] args){

        String host = "localhost";
        int port = 20000;

        String path = "";
        String password = "";

        Http2nettyServer server = new Http2nettyServer(host,port);
//        server.setUseSsl(path,password,true);
        server.setUseSsl(false);
        server.bind();
    }
}
