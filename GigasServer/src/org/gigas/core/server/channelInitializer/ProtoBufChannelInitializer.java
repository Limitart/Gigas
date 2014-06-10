package org.gigas.core.server.channelInitializer;

import org.gigas.core.server.BaseServer;
import org.gigas.core.server.handler.ProtoBufMessageHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * ProtoBuf类Channel处理链初始化
 * 
 * @author hank
 * 
 */
public class ProtoBufChannelInitializer extends ChannelInitializer<Channel> {
	private BaseServer server;

	public ProtoBufChannelInitializer(BaseServer whichserver) {
		this.server = whichserver;
	}

	@Override
	protected void initChannel(Channel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new ProtoBufMessageHandler(server));
	}
}
