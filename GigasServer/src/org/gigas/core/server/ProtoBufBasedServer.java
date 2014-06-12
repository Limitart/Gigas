package org.gigas.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.config.ServerConfig;
import org.gigas.core.server.handler.ProtoBufBasedServerHandler;

import com.google.protobuf.AbstractMessageLite;

/**
 * ProtoBuf服务器原型
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedServer implements IServer {
	private static Logger log = LogManager.getLogger(StringBasedServer.class);
	private boolean stop = false;
	private ServerConfig serverConfig = new ServerConfig();
	@SuppressWarnings("rawtypes")
	private HashMap<Class, AbstractMessageLite> messageCodecList = new HashMap<>();

	public ProtoBufBasedServer(int port) {
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
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
				pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
				for (AbstractMessageLite instance : messageCodecList.values()) {
					log.debug("register->" + instance.getClass().getName());
					pipeline.addLast(instance.getClass().getName() + "Decoder", new ProtobufDecoder(instance.getDefaultInstanceForType()));
				}
				pipeline.addLast("protobufEncoder", new ProtobufEncoder());
				pipeline.addLast(new ProtoBufBasedServerHandler());
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

	/**
	 *  注册protobuf到服务器
	 * @param msgInstance 消息实例
	 */
	public void registerProtoBuf(AbstractMessageLite msgInstance) {
		if (!messageCodecList.containsKey(msgInstance.getClass())) {
			messageCodecList.put(msgInstance.getClass(), msgInstance);
		} else {
			log.error("Duplicate message:" + msgInstance.getClass().getName());
		}
	}

	public static void main(String[] args) {
		new StringBasedServer(8888).initServer();
	}
}
