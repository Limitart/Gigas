package org.gigas.core.server.thread;

import io.netty.channel.Channel;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;

/**
 * String消息处理线程
 * 
 * @author hank
 * 
 */
public class StringBasedMessageHandleThread extends Thread implements IHandleThread<String> {
	private static Logger log = LogManager.getLogger(StringBasedMessageHandleThread.class);
	private LinkedBlockingQueue<String> handleQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;

	public StringBasedMessageHandleThread(String threadName) {
		this.setName(threadName);
	}

	@Override
	public void run() {
		stop = false;
		while (!stop || !handleQueue.isEmpty()) {
			try {
				String poll = handleQueue.poll();
				if (poll == null) {
					synchronized (this) {
						wait();
					}
				} else {
					log.debug("handle message->" + poll);
					for (Channel temp : BaseServer.getInstance().getSessions()) {
						temp.writeAndFlush(poll + "\r\n");
					}
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	@Override
	public void addTask(String t) {
		try {
			handleQueue.add(t);
			synchronized (this) {
				notify();
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@Override
	public void stopThread(boolean immediately) {
		stop = true;
		if (immediately) {
			handleQueue.clear();
		}
		try {
			synchronized (this) {
				notify();
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
}
