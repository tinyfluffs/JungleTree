package org.jungletree.net.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.nio.charset.StandardCharsets;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HttpHandler extends SimpleChannelInboundHandler<Object> {

    HttpCallback callback;
    StringBuilder content = new StringBuilder();

    public HttpHandler(HttpCallback callback) {
        this.callback = callback;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            callback.error(cause);
        } finally {
            content.setLength(0);
            ctx.channel().close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            int responseCode = response.status().code();

            if (responseCode == HttpResponseStatus.NO_CONTENT.code()) {
                done(ctx);
                return;
            }

            if (responseCode != HttpResponseStatus.OK.code()) {
                throw new IllegalStateException("Expected HTTP response 200 OK, got " + responseCode);
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            content.append(httpContent.content().toString(StandardCharsets.UTF_8));

            if (msg instanceof LastHttpContent) {
                done(ctx);
            }
        }
    }

    private void done(ChannelHandlerContext ctx) {
        try {
            callback.done(content.toString());
        } finally {
            content.setLength(0);
            ctx.channel().close();
        }
    }
}
