package org.gigas.core.server.thread;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.exception.MessageException;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.handler.ihandler.IByteMessageHandler;
import org.gigas.core.server.message.ByteMessage;
import org.gigas.core.server.message.dictionary.ByteMessageDictionary;

/**
 * ByteMessage消息处理线程
 * 
 * @author hank
 * 
 */
public class ByteMessageBasedMessageHandleThread extends Thread implements IThread {
	private static Logger log = LogManager.getLogger(ByteMessageBasedMessageHandleThread.class);
	private LinkedBlockingQueue<ByteMessage> handleQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;
	private BaseServer server;

	public ByteMessageBasedMessageHandleThread(String name, BaseServer server) {
		this.setName(name);
		this.server = server;
	}

	@Override
	public void run() {
		stop = false;
		while (!stop || !handleQueue.isEmpty()) {
			ByteMessage poll = handleQueue.poll();
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
					ByteMessageDictionary messageDictionary = (ByteMessageDictionary) server.getMessageDictionary();
					if (messageDictionary != null) {
						IByteMessageHandler handler = messageDictionary.getHandler(poll.getId());
						handler.handleMessage(poll);
					}
				} catch (InstantiationException e) {
					log.error(e, e);
				} catch (IllegalAccessException e) {
					log.error(e, e);
				} catch (MessageException e) {
					log.error(e, e);
				}
			}
		}
	}

	@Override
	public void stopThread(boolean immediately) {
		stop = true;
		if (immediately) {
			handleQueue.clear();
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
		handleQueue.add(message);
		synchronized (this) {
			notify();
		}
	}
}
