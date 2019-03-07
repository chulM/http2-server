import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;

import java.util.concurrent.TimeUnit;

/**
 * @ref https://github.com/skssfo/http2/blob/master/src/main/java/sks/samples/http2/netty/client/Client.java
 */
public class Http2ClientTest {
    static String host = "localhost";
    static int port = 8080;

    public static void main(String[] args) {


        EventLoopGroup eventLoopGroup = null;
        Bootstrap bootstrap = null;

        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final Http2Connection http2Connection = new DefaultHttp2Connection(false);
                        HttpToHttp2ConnectionHandler httpToHttp2ConnectionHandlerBuilder = new HttpToHttp2ConnectionHandlerBuilder()
                                .frameListener(new ClientFrameListener())
                                .connection(http2Connection)
                                .build();



                        ch.pipeline().addLast(httpToHttp2ConnectionHandlerBuilder);
                    }
                });

        try {
            ChannelFuture cf = bootstrap.connect(host, port).sync();

            writeByHttp(cf);
//            writeByHttp2(cf);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void writeByHttp(ChannelFuture cf) {

        HttpHeaders httpHeaders = new DefaultHttpHeaders();


        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                "https://" + host + ":" + port + "/index",
                Unpooled.copiedBuffer("test".getBytes()),
                new DefaultHttpHeaders(),
                httpHeaders
        );

        cf.channel().writeAndFlush(request);
    }

    public static void writeByHttp2(ChannelFuture cf) {

    }


}


class SettingsHandler extends SimpleChannelInboundHandler<Http2Settings> {

    private final ChannelPromise promise;

    SettingsHandler(ChannelPromise promise) {
        this.promise = promise;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Settings msg) throws Exception {
        promise.setSuccess();
        //No need for this handler to be in the chain after the initial settings exchange.
        ctx.pipeline().remove(this);
    }

    /**
     * Wait for settings exchange to complete between the client and server.
     * @param timeout
     * @param unit
     * @throws Exception
     */
    public void awaitSettings(long timeout, TimeUnit unit) throws Exception {
        if (!promise.awaitUninterruptibly(timeout, unit)) {
            throw new IllegalStateException("Timed out waiting for settings");
        }
        if (!promise.isSuccess()) {
            throw new RuntimeException(promise.cause());
        }
        System.err.println("Settings exchange complete");
    }
}

class ClientFrameListener extends Http2EventAdapter {


    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream)
            throws Http2Exception {

        System.out.println("ClientFrameListener.onDataRead()");

        int size = data.readableBytes();
        byte[] byteMessage = new byte[size];
        data.readBytes(byteMessage);

        String str = new String(byteMessage);

        System.out.println("stream ID :" + streamId+ ", data = " + str + ", padding = " + padding + ", endofstream =" + endOfStream);
        return super.onDataRead(ctx, streamId, data, padding, endOfStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding,
                              boolean endStream) throws Http2Exception {

        System.out.println("ClientFrameListener.onHeadersRead()");
        super.onHeadersRead(ctx, streamId, headers, padding, endStream);
    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings)
            throws Http2Exception {
        //save the reference to ChannelHandlerContext
        ctx.fireChannelRead(settings);
    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId,
                                  Http2Headers headers, int padding) throws Http2Exception {

        System.out.println("ClientFrameListener.onHeadersRead()");
        super.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
    }
}
