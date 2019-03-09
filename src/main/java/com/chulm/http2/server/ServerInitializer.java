package com.chulm.http2.server;

import com.chulm.http2.handler.Http2InboundsHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;

//https://netty.io/4.1/xref/io/netty/example/http2/helloworld/server/package-summary.html
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private boolean useSsl;

    public ServerInitializer(boolean useSsl) {
        this.useSsl = useSsl;

    }

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline cp = sc.pipeline();

        /*
         * Http2 Connection Create and register frame listener
         */
        Http2Connection connection = new DefaultHttp2Connection(true);
//        FrameListener frameListener = new FrameListener();
//        frameListener.setConnection(connection);

        Http2InboundsHandler inboundsHandler = new Http2InboundsHandler(connection,Integer.MAX_VALUE,true,true);
        inboundsHandler.setConnection(connection);

        Http2ConnectionHandler connectionHandler = new Http2ConnectionHandlerBuilder()
                .connection(connection)
//                .frameListener(frameListener)
                .frameListener(inboundsHandler)
                .frameLogger(new Http2FrameLogger(LogLevel.INFO, Http2ConnectionHandler.class))
                .build();

        inboundsHandler.setEncoder(connectionHandler.encoder());

        if (useSsl) {
            cp.addLast(SslContextProvider.getSelfSignedSslContext().newHandler(sc.alloc()));
        }

        /* change to FrameListener in connectionHandler */
        cp.addLast(connectionHandler);

    }
}
