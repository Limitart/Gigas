package org.gigas.core.server.thread;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.message.ProtoBufMessageAbstract;
import org.gigas.core.utils.MessageUtil;

/**
 * ProtoBuf消息发送线程
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedMessageSenderThread extends Thread implements IHandleThread {
	private static Logger log = LogManager.getLogger(ProtoBufBasedMessageSenderThread.class);
	private LinkedBlockingQueue<ProtoBufMessageAbstract> senderQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;

	public ProtoBufBasedMessageSenderThread(String name) {
		this.setName(name);
	}

	@Override
	public void run() {
		stop = false;
		while (!stop || !senderQueue.isEmpty()) {
			ProtoBufMessageAbstract poll = senderQueue.poll();
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
		if (!(t instanceof ProtoBufMessageAbstract)) {
			return;
		}
		ProtoBufMessageAbstract message = (ProtoBufMessageAbstract) t;
		senderQueue.add(message);
		synchronized (this) {
			notify();
		}
	}
}
