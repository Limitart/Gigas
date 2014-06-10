package org.gigas.core.server.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;

/**
 * http消息Hanlder
 * 
 * @author hank
 * 
 */
@Sharable
public class HttpMessageHandler extends ChannelInboundHandlerAdapter {
	private BaseServer server;
	private static Logger log = LogManager.getLogger(HttpMessageHandler.class);

	public HttpMessageHandler(BaseServer whichserver) {
		this.server = whichserver;
	}

	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// log.info("channelActive");
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// log.info("channelInactive");
	}

	/**
	 * 读取消息
	 */
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!ctx.channel().isActive())
			return;
		if (!(msg instanceof DefaultHttpRequest))
			return;
		DefaultHttpRequest request = (DefaultHttpRequest) msg;
		if (request.getMethod().equals(HttpMethod.GET)) {// GET方式传输
			QueryStringDecoder decoder = new QueryStringDecoder(request.getUri(), Charset.forName("UTF-8"));
			Map<String, List<String>> parameters = decoder.parameters();
			if (parameters != null && !parameters.isEmpty()) {
				server.getHttpHandler().doHttp(ctx.channel(), parameters);
			}
		} else if (request.getMethod().equals(HttpMethod.POST)) {// POST方式传输
			HttpPostRequestDecoder httpPostRequestDecoder = new HttpPostRequestDecoder(request);
			// TODO POST
		}
		ctx.close();
	}

	/**
	 * 消息读取完毕
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// log.info("channelReadComplete");
	}

	/**
	 * 用户连接进来了
	 */
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// log.info(ctx.channel().remoteAddress() + "connected!");
		// TODO IP-ALLOW
	}

	/**
	 * 用户断开连接
	 */
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// log.info(ctx.channel().remoteAddress() + "disconnected!");
	}

	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		log.info("channelWritabilityChanged");
	}

	/**
	 * 捕获到异常
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// log.error(cause, cause);
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		log.info("userEventTriggered");
	}
}
