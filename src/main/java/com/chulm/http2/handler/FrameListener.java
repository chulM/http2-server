package com.chulm.http2.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.*;

public class FrameListener extends Http2EventAdapter {


    private Http2ConnectionEncoder encoder;
    private Http2Connection connection;

    public void setEncoder(Http2ConnectionEncoder encoder) {
        this.encoder = encoder;
    }

    public void setConnection(Http2Connection connection) {
        this.connection = connection;
    }

    public Http2ConnectionEncoder getEncoder() {
        return encoder;
    }

    public Http2Connection getConnection() {
        return connection;
    }

    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {

        int size = data.readableBytes();
        byte[] byteMessage = new byte[size];
        data.readBytes(byteMessage);

        String str = new String(byteMessage);
        System.out.println("On Data read : " + str);
        System.out.println("onDataRead -> StreamID :" + streamId + ", padding :" + padding + ", end of Stream : " + endOfStream);

        return super.onDataRead(ctx, streamId, data, padding, endOfStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onHeadersRead -> StreamID :" + streamId + ", padding :" + padding + ", end of Stream : " + endOfStream);
//        Http2HeadersFrame frame = new DefaultHttp2HeadersFrame(headers, endOfStream, padding);
//        ctx.fireChannelRead(frame);

        CharSequence path = headers.get(Http2Headers.PseudoHeaderName.PATH.value()).toString();
        RequestHandler handler = RequestHandlerFactory.getInstance().getRequestHandler(path.toString(), getConnection());
        handler.handleHeaderFrame(ctx, streamId, headers, padding, endOfStream, getEncoder());
    }

    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream) throws Http2Exception {
//        Http2HeadersFrame frame=new  DefaultHttp2HeadersFrame(headers,endStream,padding).setStreamId(streamId);
//        ctx.fireChannelRead(frame);
        System.out.println("onHeadersRead ");
        onHeadersRead(ctx,streamId,headers,padding,endStream);
    }


    @Override
    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive) throws Http2Exception {
        System.out.println("onPriorityRead ");
    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
        System.out.println("onRstStreamRead -> StreamID :" + streamId);
    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {
        System.out.println("onSettingsAckRead ");
    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
        System.out.println("onSettingsRead ");
    }

    @Override
    public void onPingRead(ChannelHandlerContext ctx, long data) throws Http2Exception {
        System.out.println("onPingRead ");
    }

    @Override
    public void onPingAckRead(ChannelHandlerContext ctx, long data) throws Http2Exception {
        System.out.println("onPingAckRead ");
    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {
        System.out.println("onPushPromiseRead ");
    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {
        System.out.println("onGoAwayRead ");
    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) throws Http2Exception {
        System.out.println("onWindowUpdateRead ");
    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception {
        System.out.println("onUnknownFrame Read ");
    }
}
