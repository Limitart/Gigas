package org.gigas.core.server.channelInitializer;

import org.gigas.core.server.BaseServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * 字节消息自定义解析channel处理链初始化
 * 
 * @author hank
 * 
 */
public class ByteChannelIntializer extends ChannelInitializer<Channel> {
	private BaseServer server;

	public ByteChannelIntializer(BaseServer whichserver) {
		this.server = whichserver;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		server.getClass();
	}

}
