// Generated by the gigasGenerator.  DO NOT EDIT!

package org.gigas.chat.message;

import java.util.List;

import org.gigas.chat.message.proto.ChatMessageFactory;
import org.gigas.chat.message.proto.ChatMessageFactory.ChatInfo.Builder;
import org.gigas.chat.message.proto.ChatMessageFactory.RoleChatInfo;
import org.gigas.core.client.message.ProtoBufMessage;

import com.google.protobuf.MessageLite;

public class ChatInfoMessageBuilder extends ProtoBufMessage {

	private String content;// 聊天内容

	private long number;// 发送者编号

	private List<Integer> integerList;// 整型数组

	private RoleChatInfo roleChatInfo;// roleChatInfo

	/**
	 * 聊天内容setter
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 聊天内容getter
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * 发送者编号setter
	 */
	public void setNumber(long number) {
		this.number = number;
	}

	/**
	 * 发送者编号getter
	 */
	public long getNumber() {
		return this.number;
	}

	/**
	 * 整型数组setter
	 */
	public void setIntegerList(List<Integer> integerList) {
		this.integerList = integerList;
	}

	/**
	 * 整型数组getter
	 */
	public List<Integer> getIntegerList() {
		return this.integerList;
	}

	/**
	 * roleChatInfosetter
	 */
	public void setRoleChatInfo(RoleChatInfo roleChatInfo) {
		this.roleChatInfo = roleChatInfo;
	}

	/**
	 * roleChatInfogetter
	 */
	public RoleChatInfo getRoleChatInfo() {
		return this.roleChatInfo;
	}

	@Override
	public ChatMessageFactory.ChatInfo build() {
		Builder builder = ChatMessageFactory.ChatInfo.newBuilder();
		builder.setContent(this.content);
		builder.setNumber(this.number);
		builder.addAllIntegerList(this.integerList);
		builder.setRoleChatInfo(this.roleChatInfo);
		return builder.build();
	}

	@Override
	public int getId() {
		return 1001;
	}

	@Override
	public Class<? extends MessageLite> getClazz() {
		return ChatMessageFactory.ChatInfo.class;
	}

}