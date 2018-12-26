import server.Http2nettyServer;

public class RunHttp2WebServer {

    public static void main(String[] args){
        /**
         * Test to Not TLS Server....
         *
         * Use SSl For ...
         * java -Xbootclasspath/p:/home/{path-to-jar}/alpn-boot.jar -jar server-v0.1-1.0.0.jar
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
