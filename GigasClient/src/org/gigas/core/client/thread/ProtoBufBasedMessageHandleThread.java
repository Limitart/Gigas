package org.gigas.core.client.thread;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.client.BaseClient;
import org.gigas.core.client.handler.IHandler;
import org.gigas.core.client.message.ProtoBufPackage;
import org.gigas.core.client.message.dictionary.ProtoBufDictionary;
import org.gigas.core.exception.ClientException;
import org.gigas.core.exception.MessageException;

/**
 * ProtoBuf消息处理线程
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedMessageHandleThread extends Thread implements IThread {
	private static Logger log = LogManager.getLogger(ProtoBufBasedMessageHandleThread.class);
	private LinkedBlockingQueue<ProtoBufPackage> handleQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;

	public ProtoBufBasedMessageHandleThread(String name) {
		this.setName(name);
	}

	@Override
	public void run() {
		stop = false;
		while (!stop || !handleQueue.isEmpty()) {
			ProtoBufPackage poll = handleQueue.poll();
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
					ProtoBufDictionary messageDictionary = BaseClient.getInstance().getMessageDictionary();
					if (messageDictionary != null) {
						IHandler handler = messageDictionary.getHandler(poll.getId());
						handler.setChannel(poll.getSrcChannel());
						handler.setMessageId(poll.getId());
						handler.handleMessage(poll.build());
					}
				} catch (ClientException e) {
					log.error(e, e);
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
		if (!(t instanceof ProtoBufPackage)) {
			return;
		}
		ProtoBufPackage message = (ProtoBufPackage) t;
		handleQueue.add(message);
		synchronized (this) {
			notify();
		}
	}
}
