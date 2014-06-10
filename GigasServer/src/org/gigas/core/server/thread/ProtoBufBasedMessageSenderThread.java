package org.gigas.core.server.thread;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.message.ProtoBufPackage;

import com.google.protobuf.MessageLite;

/**
 * ProtoBuf消息发送线程
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedMessageSenderThread extends Thread implements IThread {
	private static Logger log = LogManager.getLogger(ProtoBufBasedMessageSenderThread.class);
	private LinkedBlockingQueue<ProtoBufPackage> senderQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;
	private BaseServer server;

	public ProtoBufBasedMessageSenderThread(String name, BaseServer server) {
		this.setName(name);
		this.server = server;
	}

	@Override
	public void run() {
		stop = false;
		while (!stop || !senderQueue.isEmpty()) {
			ProtoBufPackage poll = senderQueue.poll();
			if (poll == null) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						log.error(e, e);
					}
				}
			} else {
				try {
					List<Channel> channelList = poll.getSendChannelList();
					if (channelList != null && channelList.size() > 0) {
						MessageLite build = poll.build();
						if (build == null) {
							continue;
						}
						for (Channel channel : channelList) {
							byte[] secirityBytes;
							secirityBytes = server.getServerConfig().getSecurityBytes();
							byte[] byteArray = build.toByteArray();
							ByteBuf buf = channel.alloc().directBuffer();
							buf.writeBytes(byteArray);
							ByteBuf result = channel.alloc().directBuffer();
							result.writeBytes(secirityBytes);
							result.writeInt(Integer.SIZE / Byte.SIZE + buf.readableBytes());
							result.writeInt(poll.getId());
							result.writeBytes(buf);
							channel.writeAndFlush(result);
							buf.release();
						}
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}
	}

	@Override
	public void stopThread(boolean immediately) {
		stop = true;
		if (immediately) {
			senderQueue.clear();
		}
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void addTask(Object t) {
		if (!(t instanceof ProtoBufPackage)) {
			return;
		}
		ProtoBufPackage message = (ProtoBufPackage) t;
		senderQueue.add(message);
		synchronized (this) {
			notify();
		}
	}
}
