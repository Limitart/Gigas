package org.gigas.core.client.thread;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.client.BaseClient;
import org.gigas.core.client.handler.ihandler.IByteMessageHandler;
import org.gigas.core.client.message.ByteMessage;
import org.gigas.core.client.message.dictionary.ByteMessageDictionary;
import org.gigas.core.client.thread.ithread.IThread;
import org.gigas.core.exception.MessageException;

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
	private BaseClient client;

	public ByteMessageBasedMessageHandleThread(String name, BaseClient client) {
		this.setName(name);
		this.client = client;
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
					ByteMessageDictionary messageDictionary = (ByteMessageDictionary) client.getMessageDictionary();
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
