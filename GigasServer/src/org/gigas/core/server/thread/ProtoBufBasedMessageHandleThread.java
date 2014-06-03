package org.gigas.core.server.thread;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * ProtoBuf消息处理线程
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedMessageHandleThread extends Thread implements IHandleThread {
	private LinkedBlockingQueue handleQueue = new LinkedBlockingQueue<>();

	@Override
	public void run() {

	}

	public void addMessageTask() {

	}

	@Override
	public void addTask(Object t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopThread(boolean immediately) {
		// TODO Auto-generated method stub
		
	}
}
