package org.gigas.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.client.channelInitializer.ByteChannelIntializer;
import org.gigas.core.client.channelInitializer.ProtoBufChannelInitializer;
import org.gigas.core.client.channelInitializer.StringChannelInitializer;
import org.gigas.core.client.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.client.config.ClientConfig;
import org.gigas.core.client.message.dictionary.ProtoBufDictionary;
import org.gigas.core.client.thread.IThread;
import org.gigas.core.client.thread.ProtoBufBasedMessageHandleThread;
import org.gigas.core.client.thread.ProtoBufBasedMessageSenderThread;
import org.gigas.core.client.thread.StringBasedMessageHandleThread;
import org.gigas.core.exception.ClientException;
import org.gigas.core.exception.MessageException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class BaseClient {
	private static Logger log = LogManager.getLogger(BaseClient.class);
	private ClientConfig clientConfig = new ClientConfig();
	// 消息字典
	private ProtoBufDictionary messageDictionary;
	private EventLoopGroup clientGroup;
	private Bootstrap bootstrap;
	private Channel session;
	// 服务器实例
	private static BaseClient instance;
	private static Object lock = new Object();
	// 服务器解析协议类型
	private ChannelInitializerEnum protocolEnum;
	// 消息处理线程
	private IThread handleThread;
	// 消息发送线程
	private IThread senderThread;

	protected BaseClient(ChannelInitializerEnum enumeration) throws ClientException {
		initClient();
		protocolEnum = enumeration;
		clientGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(clientGroup);
		bootstrap.channel(NioSocketChannel.class);
		if (enumeration.equals(ChannelInitializerEnum.STRING_CUSTOMED)) {
			bootstrap.handler(new StringChannelInitializer());
		} else if (enumeration.equals(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER)) {
			bootstrap.handler(new ProtoBufChannelInitializer()).option(ChannelOption.TCP_NODELAY, true);
		} else if (enumeration.equals(ChannelInitializerEnum.BYTE_CUSTOMED)) {
			bootstrap.handler(new ByteChannelIntializer());
		}
	}

	public static BaseClient getInstance(ChannelInitializerEnum protocolEnum) throws ClientException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new BaseClient(protocolEnum);
				}
			}
		}
		return instance;
	}

	public static BaseClient getInstance() throws ClientException {
		if (instance == null) {
			throw (new ClientException("please call getInstance(ChannelInitializerEnum) first,then you can user this method!"));
		}
		return instance;
	}

	public void initClient() throws ClientException {

		try {
			SAXBuilder builder = new SAXBuilder();
			InputStream file = new FileInputStream("./config/clientconfig.xml");
			Document document = builder.build(file);
			Element root = document.getRootElement();
			List<Element> list = root.getChildren();
			for (Element temp : list) {
				String name = temp.getName();
				if ("port".equalsIgnoreCase(name)) {
					try {
						int port = Integer.parseInt(temp.getValue());
						this.clientConfig.setPort(port);
					} catch (Exception e) {
						throw new ClientException("port config error!");
					}
				} else if ("securityinfo".equalsIgnoreCase(name)) {
					String sucurityStr = temp.getValue();
					byte[] bytes = sucurityStr.getBytes("UTF-8");
					this.clientConfig.setSecurityBytes(bytes);
				} else if ("ip".equalsIgnoreCase(name)) {
					String ip = temp.getValue();
					this.clientConfig.setIp(ip);
				}
			}
		} catch (Exception e) {
			throw new ClientException("can not find ./config/clientconfig.xml");
		}

	}

	public void startClient() throws MessageException {
		if (protocolEnum.equals(ChannelInitializerEnum.STRING_CUSTOMED)) {
			handleThread = new StringBasedMessageHandleThread("StringMessageHandle");
		} else if (protocolEnum.equals(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER)) {
			handleThread = new ProtoBufBasedMessageHandleThread("ProtoBufMessageHandle");
			senderThread = new ProtoBufBasedMessageSenderThread("ProtoBufMessageSender");
			if (messageDictionary == null) {
				throw new MessageException("messageDictionary is not set!please call setMessageDictionary first!");
			}
			messageDictionary.registerAllMessage();
		} else if (protocolEnum.equals(ChannelInitializerEnum.BYTE_CUSTOMED)) {
		}
		// 消息处理线程
		((Thread) handleThread).start();
		// 消息发送线程
		((Thread) senderThread).start();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				log.info("to connecting->" + clientConfig.getIp() + ":" + clientConfig.getPort() + "......");
				try {
					bootstrap.connect(clientConfig.getIp(), clientConfig.getPort()).sync();
				} catch (InterruptedException e) {
					log.error(e, e);
				}
			}
		};
		Thread mainThread = new Thread(runnable);
		mainThread.setName("MainClient");
		mainThread.start();
	}

	/**
	 * 添加消息处理任务
	 * 
	 * @param task
	 */
	public void addHandleTask(Object task) {
		handleThread.addTask(task);
	}

	/**
	 * 添加消息发送任务
	 */
	public void addSenderTask(Object tesk) {
		senderThread.addTask(tesk);
	}

	public void setMessageDictionary(ProtoBufDictionary messageDictionary) {
		this.messageDictionary = messageDictionary;
	}

	public ProtoBufDictionary getMessageDictionary() {
		return messageDictionary;
	}

	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public Channel getSession() {
		return session;
	}

	public void setSession(Channel session) {
		this.session = session;
	}

}
