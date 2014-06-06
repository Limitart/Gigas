package org.gigas.core.server.message;

import io.netty.channel.Channel;

import com.google.protobuf.MessageLite;

/**
 * protobuf封装消息虚拟类
 * 
 * @author hank
 * 
 */
public abstract class ProtoBufPackage implements IMessage {
	protected Channel channel;
	protected Class<? extends MessageLite> Clazz;

	public abstract Class<? extends MessageLite> getClazz();

	/**
	 * 构建MessageLite实例
	 * @return
	 */
	public abstract MessageLite build();

	/**
	 * 客户端channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * 客户端channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * 设置消息
	 * 
	 * @param message
	 */
	// public void setMessage(MessageLite message) {
	// this.protobuf = message;
	// }

	/**
	 * 获取消息
	 * 
	 * @return
	 */
	// public MessageLite getMessage() {
	// return this.protobuf;
	// }
}
