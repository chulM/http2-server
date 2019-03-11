package com.chulm.http2.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;

@ChannelHandler.Sharable
public final class MyHttp2HandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2ConnectionAndFrameHandler, MyHttp2HandlerBuilder> {


    public MyHttp2HandlerBuilder() {
        frameLogger(new Http2FrameLogger(LogLevel.INFO, Http2ConnectionAndFrameHandler.class));

    }

    @Override
    protected Http2ConnectionAndFrameHandler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) throws Exception {
        Http2ConnectionAndFrameHandler handler = new Http2ConnectionAndFrameHandler(decoder, encoder, initialSettings);
        this.frameListener(handler);
        return handler;
    }

    @Override
    public Http2ConnectionAndFrameHandler build() {
        return super.build();
    }


}



