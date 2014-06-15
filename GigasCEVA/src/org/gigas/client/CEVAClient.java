package org.gigas.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.gigas.chat.message.ChatInfoMessageBuilder;
import org.gigas.chat.message.RoleChatInfoMessageBuilder;
import org.gigas.core.client.BaseClient;
import org.gigas.core.client.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.client.utils.ChannelUtil;
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
			BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String readLine = strin.readLine();
				ChatInfoMessageBuilder build = new ChatInfoMessageBuilder();
				build.setContent(new String(readLine.getBytes(), "UTF-8"));
				build.setIntegerList(new LinkedList<Integer>());
				build.setNumber(System.currentTimeMillis());
				RoleChatInfoMessageBuilder rb = new RoleChatInfoMessageBuilder();
				rb.setLevel(1);
				rb.setName(messageClient.getSession().localAddress() + "");
				rb.setRoleId(System.currentTimeMillis());
				rb.setSex(true);
				build.setRoleChatInfo(rb.build());
				ChannelUtil.sendMessage_Protobuf(messageClient, build, true);
			}
		} catch (ClientException | MessageException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BaseClient getMessageServer() {
		return messageClient;
	}

}
