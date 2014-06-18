package org.gigas.message.dictionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.chat.handler.ChatInfoHandler;
import org.gigas.chat.message.proto.ChatMessageFactory.ChatInfo;
import org.gigas.core.exception.MessageException;
import org.gigas.core.server.message.dictionary.ProtoBufDictionary;

/**
 * 消息字典
 * 
 * @author hank
 * 
 */
public class MessageDictionary extends ProtoBufDictionary {
	private static Logger log = LogManager.getLogger(MessageDictionary.class);

	@Override
	public void registerAllMessage() {
		try {
			register_proto(1001, ChatInfo.class, ChatInfoHandler.class);
		} catch (MessageException e) {
			log.error(e, e);
		}
	}

}
