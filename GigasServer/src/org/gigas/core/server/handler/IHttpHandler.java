package org.gigas.core.server.handler;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 * httpHanlder
 * 
 * @author hank
 * 
 */
public interface IHttpHandler {
	public void doHttp(Channel session, Map<String, List<String>> request);
}
