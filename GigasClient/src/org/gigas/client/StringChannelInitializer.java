package org.gigas.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * channel处理链初始化
 * 
 * @author Administrator
 * 
 */
public class StringChannelInitializer extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast("frameDecoder", new LineBasedFrameDecoder(Integer.MAX_VALUE));
		ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
		ch.pipeline().addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
		ch.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
		ch.pipeline().addLast(new StringBasedServerHandler());
	}
}
