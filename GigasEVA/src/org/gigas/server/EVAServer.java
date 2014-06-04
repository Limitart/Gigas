package org.gigas.server;

import org.gigas.core.server.BaseServer;
import org.gigas.core.server.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.server.exception.MessageException;
import org.gigas.core.server.exception.ServerException;
import org.gigas.message.dictionary.MessageDictionary;

/**
 * 初号机服务器
 * 
 * @author hank
 * 
 */
public class EVAServer implements Runnable {
	private BaseServer messageServer;

	@Override
	public void run() {
		try {
			messageServer = BaseServer.getInstance(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER);
			messageServer.setMessageDictionary(new MessageDictionary());
			messageServer.startServer();
		} catch (ServerException | MessageException e) {
			e.printStackTrace();
		}
	}

	public BaseServer getMessageServer() {
		return messageServer;
	}

}
