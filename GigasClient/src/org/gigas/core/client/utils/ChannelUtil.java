package org.gigas.core.client.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.rmi.ServerException;

import org.gigas.core.client.BaseClient;
import org.gigas.core.client.message.ProtoBufPackage;
import org.gigas.core.exception.ClientException;

import com.google.protobuf.MessageLite;

/**
 * 消息工具类
 * 
 * @author hank
 * 
 */
public class ChannelUtil {

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
	 * @throws ClientException
	 */
	public static void sendMessage_Protobuf(ProtoBufPackage message, boolean immediately) throws ClientException {
		if (message == null) {
			throw new NullPointerException("message is null!");
		}
		MessageLite msg = message.build();
		if (msg == null) {
			throw new NullPointerException("can not build a message,please override method:build()!");
		}
		Channel session = BaseClient.getInstance().getSession();
		if (session == null) {
			return;
		}
		if (immediately) {
			byte[] secirityBytes;
			secirityBytes = BaseClient.getInstance().getClientConfig().getSecurityBytes();
			byte[] byteArray = msg.toByteArray();
			ByteBuf buf = session.alloc().directBuffer();
			buf.writeBytes(byteArray);
			ByteBuf result = session.alloc().directBuffer();
			result.writeBytes(secirityBytes);
			result.writeInt(Integer.SIZE / Byte.SIZE + buf.readableBytes());
			result.writeInt(message.getId());
			result.writeBytes(buf);
			session.writeAndFlush(result);
			buf.release();
		} else {
			message.addSendChannel(session);
			BaseClient.getInstance().addSenderTask(message);
		}
	}
}
