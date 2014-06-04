package org.gigas.core.server.thread;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.exception.MessageException;
import org.gigas.core.server.exception.ServerException;
import org.gigas.core.server.handler.IHandler;
import org.gigas.core.server.message.ProtoBufMessageAbstract;
import org.gigas.core.server.message.dictionary.ProtoBufDictionary;

/**
 * ProtoBuf消息处理线程
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedMessageHandleThread extends Thread implements IHandleThread<ProtoBufMessageAbstract> {
	private Logger log = LogManager.getLogger(ProtoBufBasedMessageHandleThread.class);
	private LinkedBlockingQueue<ProtoBufMessageAbstract> handleQueue = new LinkedBlockingQueue<>();
	private boolean stop = true;

	@Override
	public void run() {
		stop = false;
		while (!stop || !handleQueue.isEmpty()) {
			ProtoBufMessageAbstract poll = handleQueue.poll();
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
					ProtoBufDictionary messageDictionary = BaseServer.getInstance().getMessageDictionary();
					if (messageDictionary != null) {
						IHandler handler = messageDictionary.getHandler(poll.getId());
						handler.handleMessage(poll.getMessage());
					}
				} catch (ServerException e) {
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
	public void addTask(ProtoBufMessageAbstract t) {
		handleQueue.add(t);
		synchronized (this) {
			notify();
		}
	}
}
