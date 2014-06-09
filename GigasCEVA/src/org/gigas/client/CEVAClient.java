package org.gigas.client;

import org.gigas.core.client.BaseClient;
import org.gigas.core.client.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.exception.ClientException;
import org.gigas.core.exception.MessageException;
import org.gigas.message.dictionary.MessageDictionary;

/**
 * 初号机客户端
 * 
 * @author hank
 * 
 */
public class CEVAClient implements Runnable {
	private BaseClient messageClient;

	@Override
	public void run() {
		try {
			messageClient = BaseClient.getInstance(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER);
			messageClient.setMessageDictionary(new MessageDictionary());
			messageClient.startClient();
		} catch (ClientException | MessageException e) {
			e.printStackTrace();
		}
	}

	public BaseClient getMessageServer() {
		return messageClient;
	}

}
