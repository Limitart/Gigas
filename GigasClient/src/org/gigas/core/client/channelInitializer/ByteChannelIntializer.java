package org.gigas.core.client.channelInitializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import org.gigas.core.client.BaseClient;
import org.gigas.core.client.handler.ByteMessageHandler;

/**
 * 字节消息自定义解析channel处理链初始化
 * 
 * @author hank
 * 
 */
public class ByteChannelIntializer extends ChannelInitializer<Channel> {
	private BaseClient client;

	public ByteChannelIntializer(BaseClient client) {
		this.client = client;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ByteMessageHandler(client));
	}

}
