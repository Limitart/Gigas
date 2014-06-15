package org.gigas.core.client.message.dictionary;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.client.handler.ihandler.IByteMessageHandler;
import org.gigas.core.client.message.ByteMessage;
import org.gigas.core.client.message.dictionary.idictionary.IMessageDictionary;
import org.gigas.core.exception.MessageException;

/**
 * byte消息字典
 * 
 * @author hank
 * 
 */
public abstract class ByteMessageDictionary implements IMessageDictionary<ByteMessage, IByteMessageHandler> {
	private static Logger log = LogManager.getLogger(ByteMessageDictionary.class);
	private HashMap<Integer, Class<? extends ByteMessage>> id_messageMap = new HashMap<>();
	private HashMap<Integer, Class<? extends IByteMessageHandler>> id_handlerMap = new HashMap<>();

	/**
	 * 获得消息类
	 * 
	 * @param id
	 * @return
	 * @throws MessageException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public ByteMessage getMessage(final int id) throws MessageException, InstantiationException, IllegalAccessException {
		if (!id_messageMap.containsKey(id)) {
			throw new MessageException("id:" + id + " message not exist!");
		}
		ByteMessage newInstance = id_messageMap.get(id).newInstance();
		return newInstance;

	}

	/**
	 * 获取hanlder
	 * 
	 * @param id
	 * @return
	 * @throws MessageException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public IByteMessageHandler getHandler(final int id) throws MessageException, InstantiationException, IllegalAccessException {
		if (!id_handlerMap.containsKey(id)) {
			throw new MessageException("id:" + id + " handler not exist!");
		}
		return id_handlerMap.get(id).newInstance();
	}

	/**
	 * 注册消息
	 * 
	 * @param id
	 * @param messageClass
	 * @param handlerClass
	 */
	public void register(final int id, final Class<? extends ByteMessage> messageLite, Class<? extends IByteMessageHandler> handlerClass) {
		try {
			putMessage(id, messageLite);
			putHanlder(id, handlerClass);
		} catch (MessageException e) {
			log.error(e, e);
		}

	}

	/**
	 * 添加消息
	 * 
	 * @param id
	 * @param clazz
	 * @throws MessageException
	 */
	private void putMessage(final int id, final Class<? extends ByteMessage> messageLite) throws MessageException {
		if (id_messageMap.containsKey(id)) {
			throw new MessageException("id:" + id + " duplicate message");
		}
		id_messageMap.put(id, messageLite);
	}

	/**
	 * 添加handler
	 * 
	 * @param id
	 * @param clazz
	 * @throws MessageException
	 */
	private void putHanlder(final int id, final Class<? extends IByteMessageHandler> clazz) throws MessageException {
		if (id_handlerMap.containsKey(id)) {
			throw new MessageException("id:" + id + " duplicate handler");
		}
		id_handlerMap.put(id, clazz);
	}

	/**
	 * 注册所有消息
	 */
	public abstract void registerAllMessage();
}
