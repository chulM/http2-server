package com.chulm.http2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * Ref{@link=https://medium.com/@chanakadkb/lets-make-server-push-enabled-http-2-server-with-netty-1e54134dc90b}
 */
public class Http2nettyServer {

    private String host = "localhost";
    private int port = 8080;

    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;

    private ServerBootstrap serverBootstrap;

    private ChannelFuture cf;

    private Boolean useSsl = false;

    private Http2ServerInitializer serverInitializer;

    public Http2nettyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void init() {

        parentGroup = new NioEventLoopGroup(1);
        childGroup = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverInitializer = new Http2ServerInitializer(useSsl);

        serverBootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 200)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(serverInitializer);

    }


    public void bind() {

        init();
        try {
            cf = serverBootstrap.bind(new InetSocketAddress(host, port)).sync();

            //Binding Done
//            cf.isDone();

            if (cf.isSuccess()) {
                System.out.println("Server Binding Address =" + cf.channel().localAddress());
                cf.channel().closeFuture().sync();
            } else {
                shutdown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            shutdown();
        }
    }

    public void shutdown() {
        cf.channel().close();
        childGroup.shutdownGracefully();
        parentGroup.shutdownGracefully();
    }

    public Boolean getUseSsl() {
        return useSsl;
    }

    public void setUseSsl(Boolean useSsl) {
        this.useSsl = useSsl;
    }

}
