package org.gigas.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.config.ServerConfig;
import org.gigas.core.server.handler.StringBasedServerHandler;

/**
 * 字符串消息服务器原型
 * 
 * @author hank
 * 
 */
public class StringBasedServer implements IServer {
	private static Logger log = LogManager.getLogger(StringBasedServer.class);
	private boolean stop = false;
	private ServerConfig serverConfig = new ServerConfig();

	public StringBasedServer(int port) {
		serverConfig.setPort(port);
	}

	public void run() {
		while (!stop) {
			try {
				log.info("String server is ok!");
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	public void initServer() {
		EventLoopGroup group = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.group(group);
		bootstrap.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast(new StringBasedServerHandler());
			}
		});
		try {
			log.info("waiting for connection......");
			ChannelFuture cf = bootstrap.bind(serverConfig.getPort()).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error(e, e);
		} finally {
			group.shutdownGracefully();
		}
	}

	public void stopServer() {
		this.stop = true;
	}

	public static void main(String[] args) {
		new StringBasedServer(8888).initServer();
	}
}
