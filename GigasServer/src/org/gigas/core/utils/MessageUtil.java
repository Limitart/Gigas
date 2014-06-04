package org.gigas.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.config.ServerConfig;
import org.gigas.core.server.exception.ServerException;
import org.gigas.core.server.message.ProtoBufMessageAbstract;

/**
 * 消息工具类
 * 
 * @author hank
 * 
 */
public class MessageUtil {

	private static Logger log = LogManager.getLogger(MessageUtil.class);

	/**
	 * 构建bytebuf
	 * 
	 * @param message
	 * @return
	 */
	public static ByteBuf buildByteBuf(ProtoBufMessageAbstract message) {
		if (message != null) {
			ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
			try {
				ServerConfig serverConfig = BaseServer.getInstance().getServerConfig();
				byte[] securityBytes = serverConfig.getSecurityBytes();
				buffer.writeBytes(securityBytes);
				long id = message.getId();
				buffer.writeInt(8 + message.getMessage().getSerializedSize());
				buffer.writeLong(id);
				buffer.writeBytes(message.getMessage().toByteArray());
			} catch (ServerException e) {
				log.error(e, e);
			}
			return buffer;
		}
		return null;
	}
}
