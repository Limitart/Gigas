package org.gigas.core.server.handler;

import io.netty.channel.Channel;

import com.google.protobuf.MessageLite;

/**
 * protobuf处理器接口
 * 
 * @author hank
 * 
 */
public abstract class IHandler {
	private Channel channel;
	private long messageId;

	public abstract void handleMessage(MessageLite message);

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

}
