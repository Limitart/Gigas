package org.gigas.core.server.handler;

import org.gigas.protocolbuffer.message.car.CarMessageFactory.CarMessage;

import com.google.protobuf.MessageLite;

public class CarHandler implements IHandler {

	@Override
	public void handleMessage(MessageLite message) {
		CarMessage msg = (CarMessage) message;
		System.out.println("receive protobuf:" + msg);
		System.out.println("id:" + msg.getId());
		System.out.println("age:" + msg.getAge());
		System.out.println("name:" + msg.getName());
	}

}
