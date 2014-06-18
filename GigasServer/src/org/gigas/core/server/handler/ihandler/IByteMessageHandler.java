package org.gigas.core.server.handler.ihandler;

import org.gigas.core.server.message.ByteMessage;

/**
 * bytemessage处理器接口
 * 
 * @author hank
 * 
 */
public abstract class IByteMessageHandler {

	public abstract void handleMessage(ByteMessage message);

}
