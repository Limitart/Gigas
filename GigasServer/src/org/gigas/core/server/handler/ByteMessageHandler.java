package org.gigas.core.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.message.ByteMessage;
import org.gigas.core.server.message.dictionary.ByteMessageDictionary;

/**
 * byte消息Hanlder
 * 
 * @author hank
 * 
 */
@Sharable
public class ByteMessageHandler extends ChannelInboundHandlerAdapter {
	private BaseServer server;
	private static Logger log = LogManager.getLogger(ByteMessageHandler.class);
	private final static AttributeKey<ByteBuf> BUFFERKEY = AttributeKey.valueOf("BUFFERKEY");

	public ByteMessageHandler(BaseServer whichserver) {
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
		// header(包含ID自身的包体长度)+ID+message
		if (!(msg instanceof ByteBuf)) {// 不是ByteBuf实例，返回
			return;
		}
		ByteBuf buffer = (ByteBuf) msg;
		Attribute<ByteBuf> attr = ctx.attr(BUFFERKEY);
		ByteBuf tempBuf = attr.get();
		if (tempBuf == null) {
			return;
		}
		tempBuf.writeBytes(buffer);
		buffer.release();
		try {
			int readableBytes = tempBuf.readableBytes();// 可读的字节数
			if (server.getServerConfig().getSecurityBytes() != null) {
				if (readableBytes < server.getServerConfig().getSecurityBytes().length) {// 包头的长度不够
					return;
				}
				byte[] sb = new byte[server.getServerConfig().getSecurityBytes().length];
				tempBuf.readBytes(sb);
				for (int i = 0; i < sb.length; ++i) {
					if (sb[i] != server.getServerConfig().getSecurityBytes()[i]) {
						log.error("security bytes error! disconneted!");
						ctx.close();
						return;
					}
				}
			}
			if (readableBytes < Integer.SIZE / Byte.SIZE) {// 包头的长度不够
				return;
			}
			tempBuf.markReaderIndex();// 标记当前readindex
			int length = tempBuf.readInt();
			int afterHeadLength = tempBuf.readableBytes();// 去除包头后的长度
			// log.debug("nowLentgh->" + afterHeadLength + " needLentgh->" +
			// length);
			if (afterHeadLength < length) {
				tempBuf.resetReaderIndex();// 重置当前readindex
				return;
			}
			final int id = tempBuf.readInt();
			ByteBuf body = ctx.alloc().directBuffer(length - Integer.SIZE / Byte.SIZE);
			tempBuf.readBytes(body);
			ByteMessage message = ((ByteMessageDictionary) server.getMessageDictionary()).getMessage(id);
			if (message == null) {// 没有找到对应的消息类
				log.error("id:" + id + " not exist!");
				// ctx.close();
				return;
			}
			message._readAll(body);
			// 得到的消息派发
			message.setSrcChannel(ctx.channel());
			server.addHandleTask(message);
			tempBuf.discardReadBytes();// 丢弃已读取字节
		} catch (Exception e) {
			log.error(e, e);
		}

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
		log.info(ctx.channel().remoteAddress() + "connected!");
		Attribute<ByteBuf> attr = ctx.attr(BUFFERKEY);
		attr.set(ctx.alloc().directBuffer());
		server.registerChannel(ctx.channel());
	}

	/**
	 * 用户断开连接
	 */
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.info(ctx.channel().remoteAddress() + "disconnected!");
		Attribute<ByteBuf> attr = ctx.attr(BUFFERKEY);
		ByteBuf byteBuf = attr.getAndRemove();
		byteBuf.release();
		server.unregisterChannel(ctx.channel());
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
