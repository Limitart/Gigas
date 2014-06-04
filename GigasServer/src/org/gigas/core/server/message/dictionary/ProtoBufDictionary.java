package org.gigas.core.server.message.dictionary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.exception.MessageException;
import org.gigas.core.server.handler.IHandler;
import org.gigas.core.server.message.ProtoBufMessageAbstract;

import com.google.protobuf.AbstractMessageLite.Builder;
import com.google.protobuf.MessageLite;

/**
 * Protobuf消息字典
 * 
 * @author hank
 * 
 */
public abstract class ProtoBufDictionary {
	private static Logger log = LogManager.getLogger(ProtoBufDictionary.class);
	private HashMap<Long, ProtoBufMessageAbstract> id_messageMap = new HashMap<>();
	private HashMap<Long, Class<? extends IHandler>> id_handlerMap = new HashMap<>();

	/**
	 * 获得消息类
	 * 
	 * @param id
	 * @return
	 * @throws MessageException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("rawtypes")
	public Builder getMessage(long id) throws MessageException, InstantiationException, IllegalAccessException {
		if (!id_messageMap.containsKey(id)) {
			throw new MessageException("id:" + id + " message not exist!");
		}
		ProtoBufMessageAbstract protoBufMessageAbstract = id_messageMap.get(id);
		Class<? extends MessageLite> clazz = protoBufMessageAbstract.getClazz();
		Builder result = null;
		try {
			Method declaredMethod = clazz.getDeclaredMethod("newBuilder");
			result = (Builder) declaredMethod.invoke(null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
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
	public IHandler getHandler(long id) throws MessageException, InstantiationException, IllegalAccessException {
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
	public void register(long id, Class<? extends MessageLite> messageLite, Class<? extends IHandler> handlerClass) {
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
	private void putMessage(final long id, final Class<? extends MessageLite> messageLite) throws MessageException {
		if (id_messageMap.containsKey(id)) {
			throw new MessageException("id:" + id + " duplicate message");
		}
		ProtoBufMessageAbstract protoBufMessageAbstract = new ProtoBufMessageAbstract() {
			@Override
			public long getId() {
				return id;
			}

			@Override
			public Class<? extends MessageLite> getClazz() {
				return messageLite;
			}
		};
		id_messageMap.put(id, protoBufMessageAbstract);
	}

	/**
	 * 添加handler
	 * 
	 * @param id
	 * @param clazz
	 * @throws MessageException
	 */
	private void putHanlder(long id, Class<? extends IHandler> clazz) throws MessageException {
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
