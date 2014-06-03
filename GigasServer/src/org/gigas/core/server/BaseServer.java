package org.gigas.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.channelInitializer.ByteChannelIntializer;
import org.gigas.core.server.channelInitializer.ProtoBufChannelInitializer;
import org.gigas.core.server.channelInitializer.StringChannelInitializer;
import org.gigas.core.server.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.server.config.ServerConfig;
import org.gigas.core.server.exception.ServerException;
import org.gigas.core.server.thread.IHandleThread;
import org.gigas.core.server.thread.ProtoBufBasedMessageHandleThread;
import org.gigas.core.server.thread.StringBasedMessageHandleThread;

/**
 * 服务器原型
 * 
 * @author hank
 * 
 */
public class BaseServer implements IServer {
	private static Logger log = LogManager.getLogger(BaseServer.class);
	private static final int GROUPSIZE = Runtime.getRuntime().availableProcessors() * 2; // 默认线程组个数
	private ServerConfig serverConfig = new ServerConfig();
	private boolean stop = true;
	private HashSet<Channel> channelMap = new HashSet<>();
	private EventLoopGroup acceptorGroup;
	private EventLoopGroup clientGroup;
	private ServerBootstrap bootstrap;
	private ChannelFuture channelFuture;
	private static BaseServer instance;
	private static Object lock = new Object();
	private ChannelInitializerEnum protocolEnum;
	@SuppressWarnings("rawtypes")
	private IHandleThread handleThread;

	/**
	 * 得到服务器实例
	 * 
	 * @param port
	 *            端口
	 * @param protocolEnum
	 *            解析协议类型
	 * @return
	 */
	public static BaseServer getInstance(int port, ChannelInitializerEnum protocolEnum) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new BaseServer(port, protocolEnum);
				}
			}
		}
		return instance;
	}

	public static BaseServer getInstance() throws ServerException {
		if (instance == null) {
			throw (new ServerException("please call getInstance(int port) first"));
		}
		return instance;
	}

	/**
	 * 初始化服务器
	 * 
	 * @param port
	 */
	private BaseServer(int port, ChannelInitializerEnum enumeration) {
		protocolEnum = enumeration;
		serverConfig.setPort(port);
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
			throw (new ServerException("please call getInstance(int port) first"));
		}
		if (!isStart()) {
			throw (new ServerException("please start server first"));
		}
		return channelMap;
	}

	@Override
	public void startServer() {
		// 服务器其他线程
		if (protocolEnum.equals(ChannelInitializerEnum.STRING_CUSTOMED)) {
			handleThread = new StringBasedMessageHandleThread("StringMessageHandle");
		} else if (protocolEnum.equals(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER)) {
			handleThread = new ProtoBufBasedMessageHandleThread();
		} else if (protocolEnum.equals(ChannelInitializerEnum.BYTE_CUSTOMED)) {
		}
		((Thread) handleThread).start();
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
	 * 添加任务
	 * 
	 * @param task
	 */
	@SuppressWarnings("unchecked")
	public void addTask(Object task) {
		handleThread.addTask(task);
	}

	public static void main(String[] args) {
		BaseServer.getInstance(8888, ChannelInitializerEnum.STRING_CUSTOMED).startServer();
	}
}
