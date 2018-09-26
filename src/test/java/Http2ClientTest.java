import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;

public class Http2ClientTest {
    static String host = "localhost";
    static int port = 20000;

    public static void main(String[] args) {


        EventLoopGroup eventLoopGroup = null;
        Bootstrap bootstrap = null;

        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final Http2Connection http2Connection = new DefaultHttp2Connection(false);
                        HttpToHttp2ConnectionHandler httpToHttp2ConnectionHandlerBuilder = new HttpToHttp2ConnectionHandlerBuilder()
                                .frameListener(new DelegatingDecompressorFrameListener(
                                        http2Connection,
                                        new InboundHttp2ToHttpAdapterBuilder(http2Connection)
                                                .maxContentLength(Integer.MAX_VALUE)
                                                .propagateSettings(true)
                                                .build()))
                                .connection(http2Connection)
                                .build();

                        ch.pipeline().addLast(httpToHttp2ConnectionHandlerBuilder);
                        ch.pipeline().addLast(new httpResponseHandler());
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
                "https://" + host + ":" + port + "/",
                Unpooled.copiedBuffer("test".getBytes()),
                new DefaultHttpHeaders(),
                httpHeaders
        );

        cf.channel().writeAndFlush(request);
    }

    public static void writeByHttp2(ChannelFuture cf) {

    }


}


class httpResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    EventLoopGroup eventLoopGroup = null;
    String device = null;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        System.out.println(msg.toString());
        System.out.println(msg.status().code());
        System.out.println(msg.content());

    }


    public void operationComplete(ChannelFuture future) throws Exception {
    }
}
