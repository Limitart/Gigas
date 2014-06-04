package org.gigas.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.exception.ServerException;
import org.gigas.core.server.message.ProtoBufMessageAbstract;

/**
 * 消息工具类
 * 
 * @author hank
 * 
 */
public class ChannelUtil {
	private static Logger log = LogManager.getLogger(ChannelUtil.class);

	/**
	 * 发送给所有channel
	 * 
	 * @param message
	 */
	public static void sendToAll_Protobuf(ProtoBufMessageAbstract message) {
		if (message == null) {
			return;
		}
		ByteBuf buildByteBuf = MessageUtil.buildByteBuf(message);
		try {
			HashSet<Channel> sessions = BaseServer.getInstance().getSessions();
			for (Channel temp : sessions) {
				temp.writeAndFlush(buildByteBuf);
			}
		} catch (ServerException e) {
			log.error(e, e);
		}
	}

	/**
	 * 发送给指定的channel
	 * 
	 * @param channel
	 * @param message
	 * @param immediately
	 *            是否立即发送(不加入缓存)
	 */
	public static void sendToChannel_Protobuf(Channel channel, ProtoBufMessageAbstract message, boolean immediately) {
		if (message == null) {
			return;
		}
		message.setChannel(channel);
		if (channel.isActive()) {
			if (immediately) {
				ByteBuf buildByteBuf = MessageUtil.buildByteBuf(message);
				channel.writeAndFlush(buildByteBuf);
			} else {
				try {
					BaseServer.getInstance().addSenderTask(message);
				} catch (ServerException e) {
					log.error(e, e);
				}
			}
		}
	}
}
