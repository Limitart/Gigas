package org.gigas.core.server.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;

/**
 * 字符消息Hanlder
 * 
 * @author hank
 * 
 */
@Sharable
public class StringMessageHandler extends ChannelInboundHandlerAdapter {

	private static Logger log = LogManager.getLogger(StringMessageHandler.class);

	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("channelActive");
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("channelInactive");
	}

	/**
	 * 读取消息
	 */
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String str = (String) msg;
		// decode
		//
		// push to cache
		BaseServer.getInstance().addHandleTask(str);
		// if ("quit".equalsIgnoreCase(str)) {
		// ctx.close();
		// } else if ("stopserver".equalsIgnoreCase(str)) {
		// StringBasedServer.getInstance().stopServer();
		// } else {
		// StringBasedServer instance = StringBasedServer.getInstance();
		// for (Channel temp : instance.getSessions()) {
		// temp.writeAndFlush(str);
		// }
		// }
		// log.info("received->" + str);
	}

	/**
	 * 消息读取完毕
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	}

	/**
	 * 用户连接进来了
	 */
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		log.info(ctx.channel().remoteAddress() + "connected!");
		BaseServer instance = BaseServer.getInstance();
		instance.registerChannel(ctx.channel());
	}

	/**
	 * 用户断开连接
	 */
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.info(ctx.channel().remoteAddress() + "disconnected!");
		BaseServer instance = BaseServer.getInstance();
		instance.unregisterChannel(ctx.channel());
	}

	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		log.info("channelWritabilityChanged");
	}

	/**
	 * 捕获到异常
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(cause, cause);
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		log.info("userEventTriggered");
	}

}
