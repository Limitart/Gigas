package org.gigas.core.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.codec.ProtoBufCustomedDecoder;
import org.gigas.core.server.message.ProtoBufMessageAbstract;

import com.google.protobuf.AbstractMessageLite.Builder;
import com.google.protobuf.MessageLite;

/**
 * protobuf消息Hanlder
 * 
 * @author hank
 * 
 */
@Sharable
public class ProtoBufBasedServerHandler extends ChannelInboundHandlerAdapter {

	private static Logger log = LogManager.getLogger(ProtoBufBasedServerHandler.class);
	private ByteBuf tempBuf = ByteBufAllocator.DEFAULT.buffer();

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
		// header(包含ID自身的包体长度)+ID+message
		if (!(msg instanceof ByteBuf)) {// 不是ByteBuf实例，返回
			return;
		}
		ByteBuf buffer = (ByteBuf) msg;
		tempBuf.writeBytes(buffer);
		try {
			int readableBytes = tempBuf.readableBytes();// 可读的字节数
			if (readableBytes < 4) {// 包头的长度不够
				return;
			}
			tempBuf.markReaderIndex();// 标记当前readindex
			int length = tempBuf.readInt();
			int afterHeadLength = tempBuf.readableBytes();// 去除包头后的长度
			if (afterHeadLength < length) {
				tempBuf.resetReaderIndex();// 重置当前readindex
				return;
			}
			final long id = tempBuf.readLong();
			ByteBuf body = ByteBufAllocator.DEFAULT.buffer(length - 8);
			tempBuf.readBytes(body);
			@SuppressWarnings("rawtypes")
			Builder messageBuilder = BaseServer.getInstance().getMessageDictionary().getMessage(id);
			if (messageBuilder == null) {// 没有找到对应的消息类
				log.error("id:" + id + " not exist!");
				tempBuf.discardReadBytes();// 丢弃已读取字节
				return;
			}
			ProtoBufCustomedDecoder protobufDecoder = new ProtoBufCustomedDecoder(messageBuilder.getDefaultInstanceForType());
			MessageLite excuteDecode = protobufDecoder.excuteDecode(id, ctx, body);// 执行解码
			ProtoBufMessageAbstract protoBufMessageAbstract = new ProtoBufMessageAbstract() {
				@Override
				public long getId() {
					return id;
				}
			};
			protoBufMessageAbstract.setChannel(ctx.channel());
			protoBufMessageAbstract.setMessage(excuteDecode);
			// 得到的消息派发
			BaseServer.getInstance().addTask(protoBufMessageAbstract);
			tempBuf.discardReadBytes();// 丢弃已读取字节
		} catch (Exception e) {
			log.error(e, e);

		}

	}

	/**
	 * 消息读取完毕
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		log.info("channelReadComplete");
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
