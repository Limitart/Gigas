package org.gigas.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NetUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.exception.MessageException;
import org.gigas.core.exception.ServerException;
import org.gigas.core.server.channelInitializer.ByteChannelIntializer;
import org.gigas.core.server.channelInitializer.HttpChannelInitializer;
import org.gigas.core.server.channelInitializer.ProtoBufChannelInitializer;
import org.gigas.core.server.channelInitializer.StringChannelInitializer;
import org.gigas.core.server.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.server.config.ServerConfig;
import org.gigas.core.server.handler.ihandler.IHttpHandler;
import org.gigas.core.server.message.dictionary.IMessageDictionary;
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
	@SuppressWarnings("rawtypes")
	private IMessageDictionary messageDictionary;
	private EventLoopGroup acceptorGroup;
	private EventLoopGroup clientGroup;
	private ServerBootstrap bootstrap;
	private ChannelFuture channelFuture;
	// 服务器实例
	// private static BaseServer instance;
	// private static Object lock = new Object();
	// 服务器解析协议类型
	private ChannelInitializerEnum protocolEnum;
	// 消息处理线程
	private IThread handleThread;
	// 消息发送线程
	private IThread senderThread;
	// Http服务器Handler
	private IHttpHandler httpHandler;
	// 是否是ssl
	private boolean isSSL = false;

	/**
	 * 得到服务器实例
	 * 
	 * @param protocolEnum
	 *            解析协议类型
	 * @return
	 * @throws ServerException
	 * @throws UnsupportedEncodingException
	 */
	public static BaseServer getNewInstance(ChannelInitializerEnum protocolEnum) throws ServerException, UnsupportedEncodingException {
		// if (instance == null) {
		// synchronized (lock) {
		// if (instance == null) {
		// instance = new BaseServer(protocolEnum);
		// }
		// }
		// }
		// return instance;
		return new BaseServer(protocolEnum);
	}

	//
	// public static BaseServer getInstance() throws ServerException {
	// if (instance == null) {
	// throw (new
	// ServerException("please call getInstance(ChannelInitializerEnum) first,then you can user this method!"));
	// }
	// return instance;
	// }

	/**
	 * 初始化服务器
	 * 
	 * @param port
	 * @throws ServerException
	 * @throws UnsupportedEncodingException
	 */
	protected BaseServer(ChannelInitializerEnum enumeration) throws ServerException, UnsupportedEncodingException {
		protocolEnum = enumeration;
		initServerConfig();
		acceptorGroup = new NioEventLoopGroup(GROUPSIZE);
		clientGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(acceptorGroup, clientGroup);
		if (enumeration.equals(ChannelInitializerEnum.STRING_CUSTOMED)) {
			bootstrap.childHandler(new StringChannelInitializer(this));
		} else if (enumeration.equals(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER)) {
			bootstrap.childHandler(new ProtoBufChannelInitializer(this));
		} else if (enumeration.equals(ChannelInitializerEnum.BYTE_CUSTOMED)) {
			bootstrap.childHandler(new ByteChannelIntializer(this));
		} else if (enumeration.equals(ChannelInitializerEnum.HTTP)) {
			bootstrap.childHandler(new HttpChannelInitializer(this));
		}
	}

	/**
	 * 初始化服务器配置信息
	 * 
	 * @throws ServerException
	 * @throws UnsupportedEncodingException
	 */
	private void initServerConfig() throws ServerException, UnsupportedEncodingException {
		// String ipreg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
		// "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
		// "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
		// "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		try {
			InputStream file = new FileInputStream("./config/serverconfig.xml");
			document = builder.build(file);
		} catch (Exception e) {
			throw new ServerException("can not find ./config/serverconfig.xml");
		}
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
				if (StringUtils.isEmpty(sucurityStr)) {
					continue;
				}
				byte[] bytes;
				bytes = sucurityStr.getBytes("UTF-8");
				this.serverConfig.setSecurityBytes(bytes);
			} else if ("serverid".equalsIgnoreCase(name)) {
				try {
					int serverid = Integer.parseInt(temp.getValue());
					this.serverConfig.setServerId(serverid);
				} catch (Exception e) {
					throw new ServerException("serverid error!");
				}
			} else if ("ip-allow".equalsIgnoreCase(name)) {
				List<Element> children = temp.getChildren();
				for (Element elt : children) {
					String value = elt.getValue();
					if (!NetUtil.isValidIpV4Address(value) && !StringUtils.isEmpty(value)) {
						throw new ServerException("ip-allow not matches the reg!");
					}
					if (!StringUtils.isEmpty(value)) {
						this.serverConfig.getIps().add(value);
					}
				}
			}
		}
	}

	@Override
	public void stopServer() {
		handleThread.stopThread(false);
		clientGroup.shutdownGracefully();
		acceptorGroup.shutdownGracefully();
		stop = true;
		log.info("MainServer Stop!");
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
		// if (instance == null) {
		// throw (new
		// ServerException("please call getInstance(ChannelInitializerEnum) first"));
		// }
		if (!isStart()) {
			throw (new ServerException("please start server first"));
		}
		return channelMap;
	}

	@Override
	public void startServer() throws MessageException {
		if (protocolEnum.equals(ChannelInitializerEnum.STRING_CUSTOMED)) {
			((Thread) (handleThread = new StringBasedMessageHandleThread("StringMessageHandle", this))).start();
		} else if (protocolEnum.equals(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER)) {
			((Thread) (handleThread = new ProtoBufBasedMessageHandleThread("ProtoBufMessageHandle", this))).start();
			((Thread) (senderThread = new ProtoBufBasedMessageSenderThread("ProtoBufMessageSender", this))).start();
			if (messageDictionary == null) {
				throw new MessageException("messageDictionary is not set!please call setMessageDictionary first!");
			}
			messageDictionary.registerAllMessage();
		} else if (protocolEnum.equals(ChannelInitializerEnum.BYTE_CUSTOMED)) {
		} else if (protocolEnum.equals(ChannelInitializerEnum.HTTP)) {
			if (httpHandler == null) {
				throw new MessageException("http Handler is not set!");
			}
		}
		// 服务器关闭线程
		// Thread shutDownHookThread = new Thread(new Runnable() {
		// @Override
		// public void run() {
		// stopServer();
		// }
		// });
		// Runtime.getRuntime().addShutdownHook(shutDownHookThread);
		// 服务器主线程
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
					senderThread.stopThread(true);
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

	@SuppressWarnings("rawtypes")
	public IMessageDictionary getMessageDictionary() {
		return messageDictionary;
	}

	@SuppressWarnings("rawtypes")
	public void setMessageDictionary(IMessageDictionary messageDictionary) {
		this.messageDictionary = messageDictionary;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setHttpHandler(IHttpHandler httpHandler) {
		this.httpHandler = httpHandler;
	}

	public IHttpHandler getHttpHandler() {
		return httpHandler;
	}

	public boolean isSSL() {
		return isSSL;
	}

	public void setSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}

	public static void main(String[] args) {

	}
}
