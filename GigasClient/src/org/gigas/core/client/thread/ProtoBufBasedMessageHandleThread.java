package org.gigas.core.client.thread;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.client.BaseClient;
import org.gigas.core.client.handler.ihandler.IProtobufHandler;
import org.gigas.core.client.message.ProtoBufMessage;
import org.gigas.core.client.message.dictionary.ProtoBufDictionary;
import org.gigas.core.client.thread.ithread.IThread;
import org.gigas.core.exception.MessageException;

/**
 * ProtoBuf消息处理线程
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedMessageHandleThread extends Thread implements IThread {
	private static Logger log = LogManager.getLogger(ProtoBufBasedMessageHandleThread.class);
	private LinkedBlockingQueue<ProtoBufMessage> handleQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;
	private BaseClient client;

	public ProtoBufBasedMessageHandleThread(String name, BaseClient client) {
		this.setName(name);
		this.client = client;
	}

	@Override
	public void run() {
		stop = false;
		while (!stop || !handleQueue.isEmpty()) {
			ProtoBufMessage poll = handleQueue.poll();
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
					ProtoBufDictionary messageDictionary = (ProtoBufDictionary) client.getMessageDictionary();
					if (messageDictionary != null) {
						IProtobufHandler handler = messageDictionary.getHandler(poll.getId());
						handler.setChannel(poll.getSrcChannel());
						handler.setMessageId(poll.getId());
						handler.setClient(client);
						handler.handleMessage(poll.build());
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
		if (!(t instanceof ProtoBufMessage)) {
			return;
		}
		ProtoBufMessage message = (ProtoBufMessage) t;
		handleQueue.add(message);
		synchronized (this) {
			notify();
		}
	}
}
