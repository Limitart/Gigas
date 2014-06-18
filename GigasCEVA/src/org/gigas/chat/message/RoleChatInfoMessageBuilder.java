// Generated by the gigasGenerator.  DO NOT EDIT!

package org.gigas.chat.message;

import org.gigas.chat.message.proto.ChatMessageFactory;
import org.gigas.chat.message.proto.ChatMessageFactory.RoleChatInfo.Builder;
import org.gigas.core.client.message.ProtoBufPackage;

import com.google.protobuf.MessageLite;

public class RoleChatInfoMessageBuilder extends ProtoBufPackage {

	private long roleId;// 角色ID

	private String name;// 角色名

	private int level;// 等级

	private boolean sex;// 性别

	/**
	 * 角色IDsetter
	 */
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	/**
	 * 角色IDgetter
	 */
	public long getRoleId() {
		return this.roleId;
	}

	/**
	 * 角色名setter
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 角色名getter
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 等级setter
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 等级getter
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * 性别setter
	 */
	public void setSex(boolean sex) {
		this.sex = sex;
	}

	/**
	 * 性别getter
	 */
	public boolean getSex() {
		return this.sex;
	}

	@Override
	public ChatMessageFactory.RoleChatInfo build() {
		Builder builder = ChatMessageFactory.RoleChatInfo.newBuilder();
		builder.setRoleId(this.roleId);
		builder.setName(this.name);
		builder.setLevel(this.level);
		builder.setSex(this.sex);
		return builder.build();
	}

	@Override
	public int getId() {
		return 1002;
	}

	@Override
	public Class<? extends MessageLite> getClazz() {
		return ChatMessageFactory.RoleChatInfo.class;
	}

}