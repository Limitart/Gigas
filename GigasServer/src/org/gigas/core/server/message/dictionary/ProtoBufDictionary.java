package org.gigas.core.server.message.dictionary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.exception.MessageException;
import org.gigas.core.server.handler.ihandler.IProtobufHandler;
import org.gigas.core.server.message.ProtoBufMessage;
import org.gigas.core.server.message.dictionary.idictionary.IMessageDictionary;

import com.google.protobuf.AbstractMessageLite.Builder;
import com.google.protobuf.MessageLite;

/**
 * Protobuf消息字典
 * 
 * @author hank
 * 
 */
public abstract class ProtoBufDictionary implements IMessageDictionary<Object, IProtobufHandler> {
	private static Logger log = LogManager.getLogger(ProtoBufDictionary.class);
	private HashMap<Integer, ProtoBufMessage> id_messageMap = new HashMap<>();
	private HashMap<Integer, Class<? extends IProtobufHandler>> id_handlerMap = new HashMap<>();

	@Override
	@SuppressWarnings("rawtypes")
	public Builder getMessage(final int id) throws MessageException, InstantiationException, IllegalAccessException {
		if (!id_messageMap.containsKey(id)) {
			throw new MessageException("id:" + id + " message not exist!");
		}
		ProtoBufMessage protoBufMessageAbstract = id_messageMap.get(id);
		Class<? extends MessageLite> clazz = protoBufMessageAbstract.getClazz();
		Builder result = null;
		try {
			Method declaredMethod = clazz.getDeclaredMethod("newBuilder");
			result = (Builder) declaredMethod.invoke(null);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			log.error(e, e);
		} catch (SecurityException e) {
			log.error(e, e);
		} catch (IllegalArgumentException e) {
			log.error(e, e);
		} catch (InvocationTargetException e) {
			log.error(e, e);
		}
		return result;
	}

	@Override
	public IProtobufHandler getHandler(final int id) throws MessageException, InstantiationException, IllegalAccessException {
		if (!id_handlerMap.containsKey(id)) {
			throw new MessageException("id:" + id + " handler not exist!");
		}
		return id_handlerMap.get(id).newInstance();
	}

	/**
	 * 添加消息
	 * 
	 * @param id
	 * @param clazz
	 * @throws MessageException
	 */
	private void putMessage(final int id, final Class<? extends MessageLite> messageLite) throws MessageException {
		if (id_messageMap.containsKey(id)) {
			throw new MessageException("id:" + id + " duplicate message");
		}
		ProtoBufMessage protoBufMessageAbstract = new ProtoBufMessage() {
			@Override
			public int getId() {
				return id;
			}

			@Override
			public Class<? extends MessageLite> getClazz() {
				return messageLite;
			}

			@Override
			public MessageLite build() {
				return null;
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
	private void putHanlder(final int id, final Class<? extends IProtobufHandler> clazz) throws MessageException {
		if (id_handlerMap.containsKey(id)) {
			throw new MessageException("id:" + id + " duplicate handler");
		}
		id_handlerMap.put(id, clazz);
	}

	public void register_proto(final int id, Class<? extends MessageLite> messageLite, Class<? extends IProtobufHandler> handlerClass) throws MessageException {
		putMessage(id, messageLite);
		putHanlder(id, handlerClass);
	}

	@Override
	@Deprecated
	public void register(int id, Class<? extends Object> messageLite, Class<? extends IProtobufHandler> handlerClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public abstract void registerAllMessage();
}
