package org.gigas.core.client.message.dictionary.idictionary;

import org.gigas.core.exception.MessageException;

public interface IMessageDictionary<M, H> {
	/**
	 * 获得消息类
	 * 
	 * @param id
	 * @return
	 * @throws MessageException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public M getMessage(final int id) throws Exception;

	/**
	 * 获取hanlder
	 * 
	 * @param id
	 * @return
	 * @throws MessageException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public H getHandler(final int id) throws Exception;

	/**
	 * 注册消息
	 * 
	 * @param id
	 * @param messageClass
	 * @param handlerClass
	 */
	public void register(final int id, final Class<? extends M> messageLite, Class<? extends H> handlerClass);

	/**
	 * 注册所有消息
	 */
	public abstract void registerAllMessage();

}
