package org.gigas.utils;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.exception.ServerException;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.message.ProtoBufPackage;

import com.google.protobuf.MessageLite;

/**
 * 消息工具类
 * 
 * @author hank
 * 
 */
public class ChannelUtil {

	private static Logger log = LogManager.getLogger(ChannelUtil.class);

	/**
	 * 发送消息
	 * 
	 * @param channel
	 *            通道
	 * @param message
	 *            消息
	 * @param immediately
	 *            立即发送(或加入缓存)
	 * @throws ServerException
	 */
	public static void sendMessage_Protobuf(Channel channel, ProtoBufPackage message, boolean immediately) throws ServerException {
		if (message == null) {
			throw new NullPointerException("message is null!");
		}
		MessageLite msg = message.build();
		if (msg == null) {
			throw new NullPointerException("can not build a message,please override method:build()!");
		}
		if (immediately) {
			byte[] secirityBytes;
			secirityBytes = BaseServer.getInstance().getServerConfig().getSecurityBytes();
			byte[] byteArray = msg.toByteArray();
			ByteBuf buf = channel.alloc().directBuffer();
			buf.writeBytes(byteArray);
			ByteBuf result = channel.alloc().directBuffer();
			result.writeBytes(secirityBytes);
			result.writeInt(Long.SIZE / Byte.SIZE + buf.readableBytes());
			result.writeInt(message.getId());
			result.writeBytes(buf);
			channel.writeAndFlush(result);
			buf.release();
		} else {
			message.addSendChannel(channel);
			BaseServer.getInstance().addSenderTask(message);
		}
	}

	/**
	 * 向所有连接发送消息
	 * 
	 * @param message
	 * @param immediately
	 *            是否立即(或加入缓存)
	 */
	public static void sendMessageToAll_Protobuf(ProtoBufPackage message, boolean immediately) {
		if (message == null) {
			throw new NullPointerException("message is null!");
		}
		MessageLite msg = message.build();
		if (msg == null) {
			throw new NullPointerException("can not build a message,please override method:build()!");
		}
		try {
			sendMessageToSome_Protobuf(new LinkedList<Channel>(BaseServer.getInstance().getSessions()), message, immediately);
		} catch (ServerException e) {
			log.error(e, e);
		}
	}

	/**
	 * 向一组连接发送消息
	 * 
	 * @param channels
	 * @param message
	 * @param immediately
	 *            是否立即(或加入缓存)
	 */
	public static void sendMessageToSome_Protobuf(List<Channel> channels, ProtoBufPackage message, boolean immediately) {
		if (message == null) {
			throw new NullPointerException("message is null!");
		}
		MessageLite msg = message.build();
		if (msg == null) {
			throw new NullPointerException("can not build a message,please override method:build()!");
		}
		try {
			if (immediately) {
				for (Channel channel : channels) {
					byte[] secirityBytes;
					secirityBytes = BaseServer.getInstance().getServerConfig().getSecurityBytes();
					byte[] byteArray = msg.toByteArray();
					ByteBuf buf = channel.alloc().directBuffer();
					buf.writeBytes(byteArray);
					ByteBuf result = channel.alloc().directBuffer();
					result.writeBytes(secirityBytes);
					result.writeInt(Long.SIZE / Byte.SIZE + buf.readableBytes());
					result.writeLong(message.getId());
					result.writeBytes(buf);
					channel.writeAndFlush(result);
					buf.release();
				}
			} else {
				message.addSendChannelAll(channels);
				BaseServer.getInstance().addSenderTask(message);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
}
