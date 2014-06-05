package org.gigas.message.dictionary;

import org.gigas.chat.handler.ChatInfoHandler;
import org.gigas.chat.message.proto.ChatMessageFactory.ChatInfo;
import org.gigas.core.server.message.dictionary.ProtoBufDictionary;

/**
 * 消息字典
 * 
 * @author hank
 * 
 */
public class MessageDictionary extends ProtoBufDictionary {

	@Override
	public void registerAllMessage() {
		register(1001, ChatInfo.class, ChatInfoHandler.class);
	}

}
