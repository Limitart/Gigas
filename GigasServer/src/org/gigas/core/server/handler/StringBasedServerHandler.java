package org.gigas.core.server.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 字符消息Hanlder
 * 
 * @author hank
 * 
 */
public class StringBasedServerHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger log = LogManager.getLogger(StringBasedServerHandler.class);

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
		log.info(msg);
		ByteBuf result = (ByteBuf) msg;
		log.info("readableBytes->" + result.readableBytes());
		log.info("capacity->" + result.capacity());
		log.info("isReadable->" + result.isReadable());
		log.info("isWritable->" + result.isWritable());
		log.info("readerIndex->" + result.readerIndex());
		byte[] bytes = new byte[result.readableBytes()];
		result.readBytes(bytes);
		String str = new String(bytes);
		ctx.write(str);
		log.info("received->" + str);
		result.release();
	}

	/**
	 * 消息读取完毕
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		log.info("channelReadComplete");
		ctx.flush();
	}

	/**
	 * 用户连接进来了
	 */
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		log.info(ctx.channel().remoteAddress() + "connected!");
	}

	/**
	 * 用户断开连接
	 */
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.info(ctx.channel().remoteAddress() + "disconnected!");
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
