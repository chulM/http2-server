import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2EventAdapter;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandler;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandlerBuilder;

public class Http2ClientTest {
    static String host = "localhost";
    static int port = 8080;

    public static void main(String[] args) {


        EventLoopGroup eventLoopGroup = null;
        Bootstrap bootstrap = null;

        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();

        final Http2Connection http2Connection = new DefaultHttp2Connection(false);
        HttpToHttp2ConnectionHandler httpToHttp2ConnectionHandlerBuilder = new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(new ClientFrameListener())
                .connection(http2Connection)
                .build();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(httpToHttp2ConnectionHandlerBuilder);
                    }
                });

        try {
            ChannelFuture cf = bootstrap.connect(host, port).sync();

            int sendCount = 1;
            for(int i = 0; i < sendCount; i++) {
                writeByHttp(cf);
                Thread.sleep(1 * 1000);
            }


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
}

class ClientFrameListener extends Http2EventAdapter {

    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("ClientFrameListener.onDataRead()");

        int size = data.readableBytes();
        byte[] byteMessage = new byte[size];
        data.readBytes(byteMessage);

        String str = new String(byteMessage);

        System.out.println("stream ID :" + streamId+ ", data = " + str + ", padding = " + padding + ", endofstream =" + endOfStream);

        return super.onDataRead(ctx, streamId, data, padding, endOfStream);
    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onGoAwayRead(ctx, lastStreamId, errorCode, debugData);
    }

    @Override
    public void onGoAwayReceived(int lastStreamId, long errorCode, ByteBuf debugData) {
        // TODO Auto-generated method stub
        super.onGoAwayReceived(lastStreamId, errorCode, debugData);
    }

    @Override
    public void onGoAwaySent(int lastStreamId, long errorCode, ByteBuf debugData) {
        // TODO Auto-generated method stub
        super.onGoAwaySent(lastStreamId, errorCode, debugData);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream) throws Http2Exception {
        System.out.println("ClientFrameListener.onHeadersRead() 1");
        super.onHeadersRead(ctx, streamId, headers, padding, endStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream) throws Http2Exception {
        System.out.println("ClientFrameListener.onHeadersRead() 2");
        super.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
    }

    @Override
    public void onPingAckRead(ChannelHandlerContext ctx, long data) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onPingAckRead(ctx, data);
    }

    @Override
    public void onPingRead(ChannelHandlerContext ctx, long data) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onPingRead(ctx, data);
    }

    @Override
    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onPriorityRead(ctx, streamId, streamDependency, weight, exclusive);
    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {
        // TODO Auto-generated method stub
        System.err.println("onPushPromiseReads: " + streamId + ", promisedStreamId:" + promisedStreamId);
        super.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onRstStreamRead(ctx, streamId, errorCode);
    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onSettingsAckRead(ctx);
    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onSettingsRead(ctx, settings);
    }

    @Override
    public void onStreamActive(Http2Stream stream) {
        // TODO Auto-generated method stub
        super.onStreamActive(stream);
    }

    @Override
    public void onStreamAdded(Http2Stream stream) {
        // TODO Auto-generated method stub
        super.onStreamAdded(stream);
    }

    @Override
    public void onStreamClosed(Http2Stream stream) {
        // TODO Auto-generated method stub
        super.onStreamClosed(stream);
    }

    @Override
    public void onStreamHalfClosed(Http2Stream stream) {
        // TODO Auto-generated method stub
        super.onStreamHalfClosed(stream);
    }

    @Override
    public void onStreamRemoved(Http2Stream stream) {
        // TODO Auto-generated method stub
        super.onStreamRemoved(stream);
    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onUnknownFrame(ctx, frameType, streamId, flags, payload);
    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) throws Http2Exception {
        // TODO Auto-generated method stub
        super.onWindowUpdateRead(ctx, streamId, windowSizeIncrement);
    }

}
