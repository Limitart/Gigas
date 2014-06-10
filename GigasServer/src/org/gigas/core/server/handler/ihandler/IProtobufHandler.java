package org.gigas.core.server.handler.ihandler;

import org.gigas.core.server.BaseServer;

import io.netty.channel.Channel;

import com.google.protobuf.MessageLite;

/**
 * protobuf处理器接口
 * 
 * @author hank
 * 
 */
public abstract class IProtobufHandler {
	private Channel channel;
	private long messageId;
	private BaseServer server;

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

	public BaseServer getServer() {
		return server;
	}

	public void setServer(BaseServer server) {
		this.server = server;
	}

}
