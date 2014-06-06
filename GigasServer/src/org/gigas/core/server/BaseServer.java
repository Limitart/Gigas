package org.gigas.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.exception.MessageException;
import org.gigas.core.exception.ServerException;
import org.gigas.core.server.channelInitializer.ByteChannelIntializer;
import org.gigas.core.server.channelInitializer.ProtoBufChannelInitializer;
import org.gigas.core.server.channelInitializer.StringChannelInitializer;
import org.gigas.core.server.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.server.config.ServerConfig;
import org.gigas.core.server.message.dictionary.ProtoBufDictionary;
import org.gigas.core.server.thread.IThread;
import org.gigas.core.server.thread.ProtoBufBasedMessageHandleThread;
import org.gigas.core.server.thread.ProtoBufBasedMessageSenderThread;
import org.gigas.core.server.thread.StringBasedMessageHandleThread;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * 服务器原型
 * 
 * @author hank
 * 
 */
public class BaseServer implements IServer {
	private static Logger log = LogManager.getLogger(BaseServer.class);
	private static final int GROUPSIZE = Runtime.getRuntime().availableProcessors() * 2; // 默认线程组个数
	// 服务器配置信息
	private ServerConfig serverConfig = new ServerConfig();
	private boolean stop = true;
	// 客户端连接集合
	private HashSet<Channel> channelMap = new HashSet<>();
	// 服务器消息字典
	private ProtoBufDictionary messageDictionary;
	private EventLoopGroup acceptorGroup;
	private EventLoopGroup clientGroup;
	private ServerBootstrap bootstrap;
	private ChannelFuture channelFuture;
	// 服务器实例
	private static BaseServer instance;
	private static Object lock = new Object();
	// 服务器解析协议类型
	private ChannelInitializerEnum protocolEnum;
	// 消息处理线程
	private IThread handleThread;
	// 消息发送线程
	private IThread senderThread;

	/**
	 * 得到服务器实例
	 * 
	 * @param protocolEnum
	 *            解析协议类型
	 * @return
	 * @throws ServerException
	 */
	public static BaseServer getInstance(ChannelInitializerEnum protocolEnum) throws ServerException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new BaseServer(protocolEnum);
				}
			}
		}
		return instance;
	}

	public static BaseServer getInstance() throws ServerException {
		if (instance == null) {
			throw (new ServerException("please call getInstance(ChannelInitializerEnum) first,then you can user this method!"));
		}
		return instance;
	}

	/**
	 * 初始化服务器
	 * 
	 * @param port
	 * @throws ServerException
	 */
	protected BaseServer(ChannelInitializerEnum enumeration) throws ServerException {
		protocolEnum = enumeration;
		initServerConfig();
		acceptorGroup = new NioEventLoopGroup(GROUPSIZE);
		clientGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(acceptorGroup, clientGroup);
		if (enumeration.equals(ChannelInitializerEnum.STRING_CUSTOMED)) {
			bootstrap.childHandler(new StringChannelInitializer());
		} else if (enumeration.equals(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER)) {
			bootstrap.childHandler(new ProtoBufChannelInitializer());
		} else if (enumeration.equals(ChannelInitializerEnum.BYTE_CUSTOMED)) {
			bootstrap.childHandler(new ByteChannelIntializer());
		}
	}

	/**
	 * 初始化服务器配置信息
	 * 
	 * @throws ServerException
	 */
	private void initServerConfig() throws ServerException {
		try {
			SAXBuilder builder = new SAXBuilder();
			InputStream file = new FileInputStream("./config/serverconfig.xml");
			Document document = builder.build(file);
			Element root = document.getRootElement();
			List<Element> list = root.getChildren();
			for (Element temp : list) {
				String name = temp.getName();
				if ("port".equalsIgnoreCase(name)) {
					try {
						int port = Integer.parseInt(temp.getValue());
						this.serverConfig.setPort(port);
					} catch (Exception e) {
						throw new ServerException("port config error!");
					}
				} else if ("securityinfo".equalsIgnoreCase(name)) {
					String sucurityStr = temp.getValue();
					byte[] bytes = sucurityStr.getBytes("UTF-8");
					this.serverConfig.setSecurityBytes(bytes);
				} else if ("serverid".equalsIgnoreCase(name)) {
					try {
						int serverid = Integer.parseInt(temp.getValue());
						this.serverConfig.setServerId(serverid);
					} catch (Exception e) {
						throw new ServerException("serverid error!");
					}
				}
			}
		} catch (Exception e) {
			throw new ServerException("can not find ./config/serverconfig.xml");
		}
	}

	@Override
	public void stopServer() {
		handleThread.stopThread(false);
		clientGroup.shutdownGracefully();
		acceptorGroup.shutdownGracefully();
		stop = true;
	}

	/**
	 * 注册channel
	 */
	public void registerChannel(Channel channel) {
		channelMap.add(channel);
	}

	/**
	 * 注销channel
	 */
	public void unregisterChannel(Channel channel) {
		channelMap.remove(channel);
	}

	/**
	 * 获得所有会话(连接)
	 * 
	 * @return
	 * @throws ServerException
	 */
	public HashSet<Channel> getSessions() throws ServerException {
		if (instance == null) {
			throw (new ServerException("please call getInstance(ChannelInitializerEnum) first"));
		}
		if (!isStart()) {
			throw (new ServerException("please start server first"));
		}
		return channelMap;
	}

	@Override
	public void startServer() throws MessageException {
		// 服务器其他线程
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
		((Thread) handleThread).start();
		((Thread) senderThread).start();
		// 服务器
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					log.info("waiting for connection......");
					stop = false;
					channelFuture = bootstrap.bind(serverConfig.getPort()).sync();
					channelFuture.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					log.error(e, e);
					handleThread.stopThread(true);
				} finally {
					clientGroup.shutdownGracefully();
					acceptorGroup.shutdownGracefully();
					stop = true;
				}
			}
		};
		Thread mainThread = new Thread(runnable);
		mainThread.setName("MainServer");
		mainThread.start();
	}

	@Override
	public boolean isStart() {
		return !stop;
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

	public ProtoBufDictionary getMessageDictionary() {
		return messageDictionary;
	}

	public void setMessageDictionary(ProtoBufDictionary messageDictionary) {
		this.messageDictionary = messageDictionary;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}
}
