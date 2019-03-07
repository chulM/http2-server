package com.chulm.http2.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http2.*;

//not used
public class Http2ResponseHandler extends InboundHttp2ToHttpAdapter {


    public Http2ResponseHandler(Http2Connection connection, int maxContentLength, boolean validateHttpHeaders, boolean propagateSettings) {
        super(connection, maxContentLength, validateHttpHeaders, propagateSettings);
    }

    @Override
    public void onStreamRemoved(Http2Stream stream) {
        super.onStreamRemoved(stream);
    }

    @Override
    protected void fireChannelRead(ChannelHandlerContext ctx, FullHttpMessage msg, boolean release, Http2Stream stream) {
        System.out.println("fireChannelRead");
        super.fireChannelRead(ctx, msg, release, stream);
    }

    @Override
    protected FullHttpMessage newMessage(Http2Stream stream, Http2Headers headers, boolean validateHttpHeaders, ByteBufAllocator alloc) throws Http2Exception {
        System.out.println("newMessage");
        return super.newMessage(stream, headers, validateHttpHeaders, alloc);
    }

    @Override
    protected FullHttpMessage processHeadersBegin(ChannelHandlerContext ctx, Http2Stream stream, Http2Headers headers, boolean endOfStream, boolean allowAppend, boolean appendToTrailer) throws Http2Exception {
        System.out.println("processHeadersBegin");
        return super.processHeadersBegin(ctx, stream, headers, endOfStream, allowAppend, appendToTrailer);
    }

    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onDataRead");
        return super.onDataRead(ctx, streamId, data, padding, endOfStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream) throws Http2Exception {

        System.out.println("OnHeadersRead -------------> StreamID :" + streamId + ", Headers :" + headers.toString() + ", Padding :" + padding + ", endOfStream :" + endOfStream);
        super.onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onHeadersRead");
        super.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream);
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
        System.out.println("onSettingsRead");
        super.onSettingsRead(ctx, settings);
    }

    @Override
    protected void onRstStreamRead(Http2Stream stream, FullHttpMessage msg) {
        System.out.println("onRstStreamRead");

        super.onRstStreamRead(stream, msg);
    }
}
