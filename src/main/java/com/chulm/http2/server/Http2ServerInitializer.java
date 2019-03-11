package com.chulm.http2.server;

import com.chulm.http2.handler.Http2ConnectionAndFrameHandler;
import com.chulm.http2.handler.Http2OrHttpHandler;
import com.chulm.http2.handler.HttpSimpleHandler;
import com.chulm.http2.handler.MyHttp2HandlerBuilder;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.codec.http2.*;
import io.netty.util.AsciiString;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * Sets up the Netty pipeline for the example server. Depending on the endpoint config, sets up the
 * pipeline for NPN or cleartext HTTP upgrade to HTTP/2.
 */
@ChannelHandler.Sharable
public class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final int maxHttpContentLength;
    private boolean useSsl = false;

    private final UpgradeCodecFactory upgradeCodecFactory = new UpgradeCodecFactory() {
        @Override
        public UpgradeCodec newUpgradeCodec(CharSequence protocol) {
            if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
                return new Http2ServerUpgradeCodec(new MyHttp2HandlerBuilder().build());
            } else {
                return null;
            }
        }
    };

    public Http2ServerInitializer(boolean useSsl) {
        this(useSsl, 16 * 1024);
    }

    public Http2ServerInitializer(boolean useSsl, int maxHttpContentLength) {
        if (maxHttpContentLength < 0) {
            throw new IllegalArgumentException("maxHttpContentLength (expected >= 0): " + maxHttpContentLength);
        }
        this.maxHttpContentLength = maxHttpContentLength;
        this.useSsl = useSsl;
    }

    @Override
    public void initChannel(SocketChannel ch) {

        if (useSsl) {
            configureSsl(ch);
        } else {
            configureClearText(ch);
        }
    }

    /**
     * Configure the pipeline for TLS NPN negotiation to HTTP/2.
     */
    private void configureSsl(SocketChannel ch) {
        try {
            ch.pipeline().addLast(SslContextProvider.getSelfSignedSslContext().newHandler(ch.alloc()));
            ch.pipeline().addLast(new Http2OrHttpHandler());
        } catch (SSLException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configure the pipeline for a cleartext upgrade from HTTP to HTTP/2.0
     */
    private void configureClearText(SocketChannel ch) {
        final ChannelPipeline p = ch.pipeline();
        final HttpServerCodec serverCodec = new HttpServerCodec();
        final HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(serverCodec, upgradeCodecFactory);
        final CleartextHttp2ServerUpgradeHandler cleartextHttp2ServerUpgradeHandler =
                                new CleartextHttp2ServerUpgradeHandler(serverCodec, upgradeHandler, new MyHttp2HandlerBuilder().build());

        p.addLast(cleartextHttp2ServerUpgradeHandler);
        p.addLast(new HttpSimpleHandler());
        p.addLast(new UserEventLogger());
    }

    /**
     * Class that logs any User Events triggered on this channel.
     */
    private static class UserEventLogger extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            System.out.println("User Event Triggered: " + evt);
            ctx.fireUserEventTriggered(evt);
        }
    }
}