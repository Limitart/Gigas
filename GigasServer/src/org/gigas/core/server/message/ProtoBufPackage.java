package org.gigas.core.server.message;

import java.util.LinkedList;
import java.util.List;

import io.netty.channel.Channel;

import com.google.protobuf.MessageLite;

/**
 * protobuf封装消息虚拟类
 * 
 * @author hank
 * 
 */
public abstract class ProtoBufPackage implements IMessage {
	protected List<Channel> sendChannelList = new LinkedList<Channel>();
	protected Channel srcChannel;
	protected Class<? extends MessageLite> Clazz;

	public abstract Class<? extends MessageLite> getClazz();

	/**
	 * 构建MessageLite实例
	 * 
	 * @return
	 */
	public abstract MessageLite build();

	/**
	 * 添加需要发送的channel
	 */
	public void addSendChannel(Channel channel) {
		sendChannelList.add(channel);
	}

	/**
	 * 添加需要发送的channel列表
	 * 
	 * @param list
	 */
	public void addSendChannelAll(List<Channel> list) {
		sendChannelList.addAll(list);
	}

	/**
	 * 获取需要发送的channel列表
	 * 
	 * @return
	 */
	public List<Channel> getSendChannelList() {
		return this.sendChannelList;
	}

	/**
	 * 得到消息来源的channel(不用主动使用)
	 * 
	 * @return
	 */
	public Channel getSrcChannel() {
		return srcChannel;
	}

	/**
	 * 设置消息来源的channel(不用主动使用)
	 * 
	 * @param srcChannel
	 */
	public void setSrcChannel(Channel srcChannel) {
		this.srcChannel = srcChannel;
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
