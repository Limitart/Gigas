package org.gigas.core.server.thread;

import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.message.ByteMessage;
import org.gigas.utils.ChannelUtil;

/**
 * ByteMessage消息发送线程
 * 
 * @author hank
 * 
 */
public class ByteMessageBasedMessageSenderThread extends Thread implements IThread {
	private static Logger log = LogManager.getLogger(ByteMessageBasedMessageSenderThread.class);
	private LinkedBlockingQueue<ByteMessage> senderQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;
	private BaseServer server;

	public ByteMessageBasedMessageSenderThread(String name, BaseServer server) {
		this.setName(name);
		this.server = server;
	}

	@Override
	public void run() {
		stop = false;
		while (!stop || !senderQueue.isEmpty()) {
			ByteMessage poll = senderQueue.poll();
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
						for (Channel channel : channelList) {
							ChannelUtil.sendMessage_ByteMessage_immediately(server, channel, poll);
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
		if (!(t instanceof ByteMessage)) {
			return;
		}
		ByteMessage message = (ByteMessage) t;
		senderQueue.add(message);
		synchronized (this) {
			notify();
		}
	}
}
