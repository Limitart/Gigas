package org.gigas.core.client.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.client.BaseClient;
import org.gigas.core.client.message.ByteMessage;
import org.gigas.core.client.message.ProtoBufMessage;
import org.gigas.core.exception.ClientException;

import com.google.protobuf.MessageLite;

/**
 * 消息工具类
 * 
 * @author hank
 * 
 */
public class ChannelUtil {

	private static Logger log = LogManager.getLogger(ChannelUtil.class);

	private ChannelUtil() {
	}

	/**
	 * 立即发送消息
	 * 
	 * @param whichserver
	 * @param channel
	 * @param message
	 */
	public static void sendMessage_ByteMessage_immediately(BaseClient whichclient, Channel channel, ByteMessage message) {
		if (message == null) {
			throw new NullPointerException("message is null!");
		}
		byte[] secirityBytes;
		secirityBytes = whichclient.getClientConfig().getSecurityBytes();
		ByteBuf buf = channel.alloc().directBuffer();
		message._writeAll(buf);
		ByteBuf result = channel.alloc().directBuffer();
		if (secirityBytes != null) {
			result.writeBytes(secirityBytes);
		}
		result.writeInt(Integer.SIZE / Byte.SIZE + buf.readableBytes());
		result.writeInt(message.getId());
		result.writeBytes(buf);
		channel.writeAndFlush(result);
		buf.release();
	}

	/**
	 * 发送消息
	 * 
	 * @param channel
	 *            通道
	 * @param message
	 *            消息
	 * @param immediately
	 *            立即发送(或加入缓存)
	 * @throws ClientException
	 */
	public static void sendMessage_ByteMessage(BaseClient whichserver, Channel channel, ByteMessage message, boolean immediately) throws ClientException {

		if (immediately) {
			sendMessage_ByteMessage_immediately(whichserver, channel, message);
		} else {
			message.addSendChannel(channel);
			whichserver.addSenderTask(message);
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
	public static void sendMessageToSome_ByteMessage(BaseClient whichserver, List<Channel> channels, ByteMessage message, boolean immediately) {
		if (message == null) {
			throw new NullPointerException("message is null!");
		}
		try {
			if (immediately) {
				for (Channel channel : channels) {
					sendMessage_ByteMessage_immediately(whichserver, channel, message);
				}
			} else {
				message.addSendChannelAll(channels);
				whichserver.addSenderTask(message);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	/**
	 * 立即发送消息
	 * 
	 * @param whichserver
	 * @param channel
	 * @param message
	 */
	public static void sendMessage_Protobuf_immediately(BaseClient whichclient, Channel channel, ProtoBufMessage message) {
		if (message == null) {
			throw new NullPointerException("message is null!");
		}
		MessageLite msg = message.build();
		if (msg == null) {
			throw new NullPointerException("can not build a message,please override method:build()!");
		}
		byte[] secirityBytes;
		secirityBytes = whichclient.getClientConfig().getSecurityBytes();
		byte[] byteArray = msg.toByteArray();
		ByteBuf buf = channel.alloc().directBuffer();
		buf.writeBytes(byteArray);
		ByteBuf result = channel.alloc().directBuffer();
		if (secirityBytes != null) {
			result.writeBytes(secirityBytes);
		}
		result.writeInt(Integer.SIZE / Byte.SIZE + buf.readableBytes());
		result.writeInt(message.getId());
		result.writeBytes(buf);
		channel.writeAndFlush(result);
		buf.release();
	}

	/**
	 * 发送消息
	 * 
	 * @param channel
	 *            通道
	 * @param message
	 *            消息
	 * @param immediately
	 *            立即发送(或加入缓存)
	 * @throws ClientException
	 */
	public static void sendMessage_Protobuf(BaseClient whichclient, ProtoBufMessage message, boolean immediately) throws ClientException {

		if (immediately) {
			sendMessage_Protobuf_immediately(whichclient, whichclient.getSession(), message);
		} else {
			message.addSendChannel(whichclient.getSession());
			whichclient.addSenderTask(message);
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
	public static void sendMessageToSome_Protobuf(BaseClient whichserver, List<Channel> channels, ProtoBufMessage message, boolean immediately) {
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
					sendMessage_Protobuf_immediately(whichserver, channel, message);
				}
			} else {
				message.addSendChannelAll(channels);
				whichserver.addSenderTask(message);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
}
