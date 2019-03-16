package com.chulm.http2.handler;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;


public class Http2ConnectionAndFrameHandler extends Http2ConnectionHandler implements Http2FrameListener {

    public Http2ConnectionAndFrameHandler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        super(decoder, encoder, initialSettings);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    private static Http2Headers http1HeadersToHttp2Headers(FullHttpRequest request) {
        CharSequence host = request.headers().get(HttpHeaderNames.HOST);
        Http2Headers http2Headers = new DefaultHttp2Headers()
                .method(HttpMethod.GET.asciiName())
                .path(request.uri())
                .scheme(HttpScheme.HTTP.name());
        if (host != null) {
            http2Headers.authority(host);
        }
        return http2Headers;
    }

    /**
     * Handles the cleartext HTTP upgrade event. If an upgrade occurred, sends a simple response via HTTP/2
     * on stream 1 (the stream specifically reserved for cleartext HTTP upgrade).
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HttpServerUpgradeHandler.UpgradeEvent) {
            HttpServerUpgradeHandler.UpgradeEvent upgradeEvent =
                    (HttpServerUpgradeHandler.UpgradeEvent) evt;
            onHeadersRead(ctx, 1, http1HeadersToHttp2Headers(upgradeEvent.upgradeRequest()), 0, true);
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * If receive a frame with end-of-stream set, send a pre-canned response.
     */
    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onDataRead()");
        int processed = data.readableBytes() + padding;
        ByteBuf message = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Server Response : Version - HTTP/2", CharsetUtil.UTF_8));
        encoder().writeData(ctx,streamId, message, padding, endOfStream, ctx.newPromise());
        return processed;
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onHeaderReads(2)");

        Http2Headers responseHeader = new DefaultHttp2Headers().status(OK.codeAsText());
        encoder().writeHeaders(ctx,streamId,responseHeader,padding, false, ctx.newPromise());

        if(headers.path().toString().equalsIgnoreCase("/favicon.ico")){
            ByteBuf message = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("NoImages", CharsetUtil.UTF_8));
            encoder().writeData(ctx,streamId, message, padding, endOfStream, ctx.newPromise());
        }
        if(headers.path().toString().equalsIgnoreCase("/")){
            ByteBuf message = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Server Response : Version - HTTP/2", CharsetUtil.UTF_8));
            encoder().writeData(ctx,streamId, message, padding, endOfStream, ctx.newPromise());
        }
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onHeaderReads(1)");
        onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }

    @Override
    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive) throws Http2Exception {
        System.out.println("onPriorityRead()");
    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
        System.out.println("onRstStreamRead()");
    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {
        System.out.println("onSettingsAckRead()");
    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
        System.out.println("onSettingsRead()");
    }

    @Override
    public void onPingRead(ChannelHandlerContext ctx, long data) throws Http2Exception {
        System.out.println("onPingRead()");
    }

    @Override
    public void onPingAckRead(ChannelHandlerContext ctx, long data) throws Http2Exception {
        System.out.println("onPingAckRead()");
    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {
        System.out.println("onPushPromiseRead()");
    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {
        System.out.println("onGoAwayRead()");
    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) throws Http2Exception {
        System.out.println("onWindowUpdateRead()");
    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception {
        System.out.println("onUnknownFrame()");
    }
}
