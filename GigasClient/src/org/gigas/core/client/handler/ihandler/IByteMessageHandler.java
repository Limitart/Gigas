package org.gigas.core.client.handler.ihandler;

import org.gigas.core.client.message.ByteMessage;


/**
 * bytemessage处理器接口
 * 
 * @author hank
 * 
 */
public abstract class IByteMessageHandler {

	public abstract void handleMessage(ByteMessage message);

}
