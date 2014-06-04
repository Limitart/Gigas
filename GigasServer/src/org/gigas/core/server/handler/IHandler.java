package org.gigas.core.server.handler;

import com.google.protobuf.MessageLite;

/**
 * protobuf处理器接口
 * 
 * @author hank
 * 
 */
public interface IHandler {
	public void handleMessage(MessageLite message);
}
