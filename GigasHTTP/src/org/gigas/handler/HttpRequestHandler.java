package org.gigas.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.handler.ihandler.IHttpHandler;

/**
 * Http消息处理
 * 
 * @author hank
 * 
 */
public class HttpRequestHandler implements IHttpHandler {
	private Logger log = LogManager.getLogger(HttpRequestHandler.class);

	@Override
	public void doHttp(Channel session, Map<String, List<String>> request) {
		ByteBuf buffer = session.alloc().directBuffer();
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
		try {
			buffer.writeBytes(("yeah you suceess!!!!" + " your value:" + request.get("x").get(0)).getBytes("UTF-8"));
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			session.writeAndFlush(response);
			session.disconnect();
		}
	}
}
