package org.gigas.test.handler;

import org.gigas.core.server.handler.IHandler;
import org.gigas.test.message.CarMessageFactory.CarMessage;

import com.google.protobuf.MessageLite;

public class CarHandler implements IHandler {

	@Override
	public void handleMessage(MessageLite message) {
		CarMessage msg = (CarMessage) message;
		System.out.println("age->" + msg.getAge() + " price->" + msg.getPrice() + " name->" + msg.getName());
	}

}
