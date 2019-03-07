package com.chulm.http2.handler;

//우리는 각 클라이언트 요청을 요청의 URL로 처리합니다.
// 서버 푸시와 서버 푸시를 포함하여 응답 할 두 개의 URL을 정의합니다.
// URL이 "/index.html"인 클라이언트 요청이 있으면 서버는 index.html 데이터를 보내고 URL이 "/main.css"이면 main.css 파일을 보냅니다.
// URL이 "/ all"이면 main.css 파일을 서버 푸시로 index.html에 응답합니다.
//
//서버 푸시를 보내기 전에 push-promise 프레임을 클라이언트에 보내고 main.css 파일을 다음과 같이 serverpush로 보낼 것이라고 응답합니다.


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Headers;

public interface RequestHandler {

    abstract void handleHeaderFrame(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding,
                           boolean endStream, Http2ConnectionEncoder encoder);

    abstract void handleDataFrame(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream, Http2ConnectionEncoder encoder);

    abstract void setConnection(Http2Connection connection);

    abstract Http2Connection getConnection();
}