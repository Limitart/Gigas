package org.gigas.client;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * ProtoBufChannelFactory
 * 
 * @author hank
 * 
 */
public class ProtoBufChannelFactory implements ChannelFactory<ServerChannel> {

	@Override
	public ServerChannel newChannel() {
		ServerChannel channel = new NioServerSocketChannel();
		ChannelPipeline pipeline = channel.pipeline();
		// Encoder
		pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
		return channel;
	}

}
