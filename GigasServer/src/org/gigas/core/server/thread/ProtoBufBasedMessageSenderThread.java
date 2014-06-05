package org.gigas.core.server.thread;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.message.ProtoBufPackage;
import org.gigas.utils.MessageUtil;

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

	public ProtoBufBasedMessageSenderThread(String name) {
		this.setName(name);
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
					Channel channel = poll.getChannel();
					if (channel != null && channel.isActive()) {
						ByteBuf buildByteBuf = MessageUtil.buildByteBuf(poll);
						channel.writeAndFlush(buildByteBuf);
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
