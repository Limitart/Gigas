package org.gigas.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;

import org.gigas.chat.message.ChatInfoMessageBuilder;
import org.gigas.chat.message.proto.ChatMessageFactory.ChatInfo;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLite;

public class GigasClient {
	public static void main(String[] args) {
		// Socket socket = null;
		// try {
		// socket = new Socket("127.0.0.1", 8888);
		// PrintWriter os = new PrintWriter(socket.getOutputStream());
		// while (true) {
		// String s = "abcd";
		// os.println(s.getBytes("UTF-8"));
		// os.flush();
		// Thread.sleep(1);
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// socket.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		byte[] secirityBytes = null;
		try {
			secirityBytes = "gigassecurity".getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
		Bootstrap boot = new Bootstrap();
		boot.group(nioEventLoopGroup);
		boot.channel(NioSocketChannel.class);
		boot.handler(new ProtoBufBasedClientHandler());
		try {
			ChannelFuture sync = boot.connect("localhost", 8888).sync();
			Channel channel = sync.channel();
			while (channel.isActive()) {
				ChatInfoMessageBuilder msg = new ChatInfoMessageBuilder();
				msg.setContent("hehe");
				msg.setNumber(111111l);
				
				ChatInfo build = msg.build();
				byte[] byteArray = build.toByteArray();
				ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
				buf.writeBytes(byteArray);
				ByteBuf result = ByteBufAllocator.DEFAULT.buffer();
				result.writeBytes(secirityBytes);
				result.writeInt(8 + buf.readableBytes());
				result.writeLong(1001);
				result.writeBytes(buf);
				channel.writeAndFlush(result);
				buf.resetReaderIndex();
				System.out.println("write");
				Thread.sleep(1000);
			}
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
