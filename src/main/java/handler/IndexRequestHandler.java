package handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class IndexRequestHandler extends  AbstractRequestHandler {

    @Override
    public void handleHeaderFrame(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, Http2ConnectionEncoder encoder) {

        System.out.println("IndexRequestHandler request received on stream " + streamId);

        //send a response header first followed by data frames.
        Http2Headers responseHeaders = new DefaultHttp2Headers();
        responseHeaders.status(HttpResponseStatus.OK.codeAsText());

        encoder.writeHeaders(ctx, streamId, responseHeaders, 0, false, ctx.newPromise());
        //send data frame every 5 seconds after the initial delay of 1 second
        ctx.channel().eventLoop().scheduleAtFixedRate(
                new DataFrameSender(ctx, streamId, encoder, getConnection()),
                1000, 5000, TimeUnit.MILLISECONDS);

    }

    @Override
    public void handleDataFrame(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) {



    }

    /**
     * Sends a data frame with 'endOfStream' set to false.
     */
    private static class DataFrameSender implements Runnable {

        ChannelHandlerContext ctx;
        int streamId;
        Http2ConnectionEncoder encoder;
        Http2Connection connection;

        public DataFrameSender(ChannelHandlerContext ctx,
                               int streamId,
                               Http2ConnectionEncoder encoder,
                               Http2Connection connection) {
            this.ctx = ctx;
            this.streamId = streamId;
            this.encoder = encoder;
            this.connection = connection;

        }

        @Override
        public void run() {
            System.out.println("index triggered - Thread - " + Thread.currentThread().getName());
            ChannelPromise channelPromise = ctx.newPromise();
            channelPromise.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println("Operation complete callback - " + future.isSuccess() +
                            " Thread - " + Thread.currentThread().getName());
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                    }
                }
            }
        );

            ByteBuf message = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Server Message : " + new Date().toString(), CharsetUtil.UTF_8));
            encoder.writeData(ctx, streamId, message, 0, false, channelPromise);
            try {
                encoder.flowController().writePendingBytes();
                ctx.flush();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
