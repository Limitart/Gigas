package org.gigas.core.client.channelInitializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import org.gigas.core.client.BaseClient;
import org.gigas.core.client.handler.StringMessageHandler;

/**
 * String类简单消息channel处理链初始化
 * 
 * @author hank
 * 
 */
public class StringChannelInitializer extends ChannelInitializer<Channel> {
	private BaseClient client;

	public StringChannelInitializer(BaseClient client) {
		this.client = client;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast("frameDecoder", new LineBasedFrameDecoder(Integer.MAX_VALUE));
		ch.pipeline().addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
		ch.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
		ch.pipeline().addLast(new StringMessageHandler(client));
	}
}
