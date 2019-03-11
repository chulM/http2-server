package com.chulm.http2.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpSimpleHandler extends SimpleChannelInboundHandler<HttpMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
        // If this handler is hit then no upgrade has been attempted and the client is just talking HTTP.
        System.out.println("Directly talking: " + msg.protocolVersion() + " (no upgrade was attempted)");
        ChannelPipeline pipeline = ctx.pipeline();
        ChannelHandlerContext thisCtx = pipeline.context(this);
        pipeline.addAfter(thisCtx.name(), null, new HttpResponseHandler("Direct. No Upgrade Attempted."));
        pipeline.replace(this, null, new HttpObjectAggregator(16 *1024));
        ctx.fireChannelRead(msg);
    }
}
