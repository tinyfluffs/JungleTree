package org.jungletree.net.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;

@ChannelHandler.Sharable
public final class NoOpHandler extends ChannelHandlerAdapter {

    public static final NoOpHandler INSTANCE = new NoOpHandler();

    private NoOpHandler() {
    }
}
