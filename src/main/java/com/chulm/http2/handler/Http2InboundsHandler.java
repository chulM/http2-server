package com.chulm.http2.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Iterator;

//not used
public class Http2InboundsHandler extends InboundHttp2ToHttpAdapter {

    private Http2ConnectionEncoder encoder;

    private Http2Connection connection;

    public Http2InboundsHandler(Http2Connection connection, int maxContentLength, boolean validateHttpHeaders, boolean propagateSettings) {
        super(connection, maxContentLength, validateHttpHeaders, propagateSettings);
    }

    public void setConnection(Http2Connection connection) {
        this.connection = connection;
    }

    public void setEncoder(Http2ConnectionEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void onStreamRemoved(Http2Stream stream) {
        super.onStreamRemoved(stream);
    }


    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onDataRead");

        byte[] msgByte = new byte[data.readableBytes()];
        data.readBytes(msgByte);
        String message = new String(msgByte);

        if(encoder.connection().stream(streamId).state()==Http2Stream.State.OPEN){
            sendToEchoMsg(ctx,streamId,message,padding,endOfStream);
        }
        return super.onDataRead(ctx, streamId, data, padding, endOfStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("OnHeadersRead -------------> StreamID :" + streamId + ", Headers :" + headers.toString() + ", Padding :" + padding + ", endOfStream :" + endOfStream);
        if(!(headers.method().toString().equals("POST") && headers.path().toString().equalsIgnoreCase("/index"))){
            encoder.writeGoAway(ctx,streamId,413, null,ctx.newPromise());
        }
        super.onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onHeadersRead");
        super.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream);
        onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
        System.out.println("onRstStreamRead");
        super.onRstStreamRead(ctx, streamId, errorCode);
    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {
        System.out.println("onPushPromiseRead");
        super.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
        System.err.println("onSettings: "  + ctx.channel().remoteAddress().toString() +", settings:" + settings.toString());
        super.onSettingsRead(ctx, settings);
    }


    public void sendToEchoMsg(ChannelHandlerContext ctx, int streamId, String message, int padding, boolean endOfStream) {
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

         Http2Headers responseHeaders = new DefaultHttp2Headers();
         responseHeaders.status(HttpResponseStatus.OK.codeAsText());
         ChannelFuture future = encoder.writeHeaders(ctx, streamId, responseHeaders, 0, false, ctx.newPromise());

         future.addListener(new ChannelFutureListener() {
             @Override
             public void operationComplete(ChannelFuture future) throws Exception {
                 System.out.println(future.isSuccess());
                 if(!future.isSuccess()){
                     future.cause().printStackTrace();
                 }
             }
         });

        ByteBuf respByteBuf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
        encoder.writeData(ctx, streamId, respByteBuf, 0, false, channelPromise);
        try {
            encoder.flowController().writePendingBytes();
            ctx.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    @Override
    protected void fireChannelRead(ChannelHandlerContext ctx, FullHttpMessage msg, boolean release, Http2Stream stream) {
        System.out.println("fireChannelRead");
        super.fireChannelRead(ctx, msg, release, stream);
    }

    @Override
    protected FullHttpMessage newMessage(Http2Stream stream, Http2Headers headers, boolean validateHttpHeaders, ByteBufAllocator alloc) throws Http2Exception {
        System.out.println("newMessage()");
        return super.newMessage(stream, headers, validateHttpHeaders, alloc);
    }

    @Override
    protected FullHttpMessage processHeadersBegin(ChannelHandlerContext ctx, Http2Stream stream, Http2Headers headers, boolean endOfStream, boolean allowAppend, boolean appendToTrailer) throws Http2Exception {
        System.out.println("processHeadersBegin()");
        return super.processHeadersBegin(ctx, stream, headers, endOfStream, allowAppend, appendToTrailer);
    }

    @Override
    protected void onRstStreamRead(Http2Stream stream, FullHttpMessage msg) {
        System.out.println("onRstStreamRead()");
        super.onRstStreamRead(stream, msg);
    }
}
