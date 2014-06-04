package org.gigas.core.server.codec;

import java.util.ArrayList;
import java.util.List;

import org.gigas.core.server.exception.MessageException;

import com.google.protobuf.MessageLite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * protobuf解析器
 * 
 * @author hank
 * 
 */
public class ProtoBufCustomedDecoder extends ProtobufDecoder {
	public ProtoBufCustomedDecoder(MessageLite prototype) {
		super(prototype);
	}

	public MessageLite excuteDecode(long id, ChannelHandlerContext ctx, ByteBuf msg) throws MessageException {
		List<Object> out = new ArrayList<>();
		try {
			decode(ctx, msg, out);
		} catch (Exception e) {
			throw new MessageException("id:" + id + "decode error!");
		}
		if (out.size() > 0) {
			return (MessageLite) out.get(0);
		}
		throw new MessageException("id:" + id + "decode error!");
	}
}
