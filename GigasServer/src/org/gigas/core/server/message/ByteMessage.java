package org.gigas.core.server.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * byte消息虚拟类
 * 
 * @author hank
 * 
 */
public abstract class ByteMessage implements IMessage {

	protected List<Channel> sendChannelList = new LinkedList<Channel>();
	protected Channel srcChannel;
	protected Class<? extends ByteMessage> Clazz;

	// abstract method
	public abstract boolean _writeAll(ByteBuf buf);

	public abstract boolean _readAll(ByteBuf buf);

	public abstract Class<? extends ByteMessage> _getClazz();

	// impl method
	protected void _putString(ByteBuf buf, String value) {
		if (value == null || StringUtils.isEmpty(value)) {
			buf.writeInt(0);
		}
		byte[] bytes = value.getBytes(CharsetUtil.UTF_8);
		int length = bytes.length;
		buf.writeInt(length);
		buf.writeBytes(bytes);

	}

	protected void _putChar(ByteBuf buf, char value) {
		buf.writeChar(value);
	}

	protected void _putByte(ByteBuf buf, byte value) {
		buf.writeByte(value);
	}

	// protected void _putBytes(ByteBuf buf, byte[] value) {
	// buf.writeBytes(value);
	// }

	protected void _putShort(ByteBuf buf, short value) {
		buf.writeShort(value);
	}

	protected void _putInt(ByteBuf buf, int value) {
		buf.writeInt(value);
	}

	protected void _putLong(ByteBuf buf, long value) {
		buf.writeLong(value);
	}

	protected void _putFloat(ByteBuf buf, float value) {
		buf.writeFloat(value);
	}

	protected void _putDouble(ByteBuf buf, double value) {
		buf.writeDouble(value);
	}

	protected void _putByteMessage(ByteBuf buf, ByteMessage message) {
		message._writeAll(buf);
	}

	protected String _getString(ByteBuf buf) {
		int length = buf.readInt();
		if (length < 1) {
			return null;
		}
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		String res = new String(bytes, CharsetUtil.UTF_8);
		return res;
	}

	protected char _getChar(ByteBuf buf) {
		return buf.readChar();
	}

	protected byte _getByte(ByteBuf buf) {
		return buf.readByte();
	}

	// protected byte[] _getBytes(ByteBuf buf) {
	// return null;
	// }

	protected short _getShort(ByteBuf buf) {
		return buf.readShort();
	}

	protected int _getInt(ByteBuf buf) {
		return buf.readInt();
	}

	protected long _getLong(ByteBuf buf) {
		return buf.readLong();
	}

	protected float _getFloat(ByteBuf buf) {
		return buf.readFloat();
	}

	protected double _getDouble(ByteBuf buf) {
		return buf.readDouble();
	}

	protected void _getByteMessage(ByteBuf buf, ByteMessage message) {
		message._readAll(buf);
	}

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

	public Class<? extends ByteMessage> getClazz() {
		return Clazz;
	}

	public void setClazz(Class<? extends ByteMessage> clazz) {
		Clazz = clazz;
	}

}
