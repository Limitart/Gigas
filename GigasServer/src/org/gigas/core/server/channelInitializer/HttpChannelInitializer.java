package org.gigas.core.server.channelInitializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.gigas.core.server.BaseServer;
import org.gigas.core.server.handler.HttpMessageHandler;

/**
 * Http类Channel处理链初始化
 * 
 * @author hank
 * 
 */
public class HttpChannelInitializer extends ChannelInitializer<Channel> {
	private BaseServer server;

	public HttpChannelInitializer(BaseServer whichserver) {
		this.server = whichserver;
	}

	@Override
	protected void initChannel(Channel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();

		if (server.isSSL()) {
			SSLEngine engine = SSLContext.getDefault().createSSLEngine();
			engine.setUseClientMode(false);
			pipeline.addLast("ssl", new SslHandler(engine));
		}
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("deflater", new HttpContentCompressor());
		pipeline.addLast("handler", new HttpMessageHandler(server));

	}
}
