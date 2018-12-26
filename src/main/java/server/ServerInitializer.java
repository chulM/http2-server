package server;

import handler.FrameListener;
import handler.Http2ResponseHandler;
import handler.SslContextHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.logging.LogLevel;

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
        FrameListener frameListener = new FrameListener();
        frameListener.setConnection(connection);

        Http2ConnectionHandler connectionHandler = new Http2ConnectionHandlerBuilder()
                .connection(connection)
                .frameListener(frameListener)
                .frameLogger(new Http2FrameLogger(LogLevel.INFO, Http2ConnectionHandler.class))
                .build();

        frameListener.setEncoder(connectionHandler.encoder());

        if (useSsl) {
//          cp.addLast(new SslContextHandler(certPath, certPassword, sc).getHandler());
            cp.addLast(SslContextHandler.getSelfSignedSslContext().newHandler(sc.alloc()));
        }
        /* change to FrameListener in connectionHandler */
        cp.addLast(connectionHandler);
    }
}
