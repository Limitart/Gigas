package org.gigas.message.dictionary;

import org.gigas.core.server.message.dictionary.ProtoBufDictionary;
import org.gigas.test.handler.CarHandler;
import org.gigas.test.message.CarMessageFactory;

/**
 * 消息字典
 * 
 * @author hank
 * 
 */
public class MessageDictionary extends ProtoBufDictionary {

	@Override
	public void registerAllMessage() {
		register(1000, CarMessageFactory.CarMessage.class, CarHandler.class);
	}

}
