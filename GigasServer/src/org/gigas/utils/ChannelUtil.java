package org.gigas.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

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

	/**
	 * 发送消息
	 * 
	 * @param channel
	 *            通道
	 * @param message
	 *            消息
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
			result.writeInt(8 + buf.readableBytes());
			result.writeLong(message.getId());
			result.writeBytes(buf);
			channel.writeAndFlush(result);
			buf.release();
		} else {
			message.setChannel(channel);
			BaseServer.getInstance().addSenderTask(message);
		}
	}
}
