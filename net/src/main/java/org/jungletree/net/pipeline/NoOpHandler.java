package org.jungletree.net.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ChannelHandler.Sharable
public final class NoOpHandler extends ChannelHandlerAdapter {
    public static final NoOpHandler INSTANCE = new NoOpHandler();
}
