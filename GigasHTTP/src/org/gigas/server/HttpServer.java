package org.gigas.server;

import java.io.UnsupportedEncodingException;

import org.gigas.core.exception.MessageException;
import org.gigas.core.exception.ServerException;
import org.gigas.core.server.BaseServer;
import org.gigas.core.server.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.handler.HttpRequestHandler;

public class HttpServer implements Runnable {
	private BaseServer httpServer;

	@Override
	public void run() {
		try {
			httpServer = BaseServer.getNewInstance(ChannelInitializerEnum.HTTP);
			httpServer.setHttpHandler(new HttpRequestHandler());
			httpServer.startServer();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (MessageException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
