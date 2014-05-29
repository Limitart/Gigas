package org.gigas.core.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ProtoBuf服务器原型
 * 
 * @author hank
 * 
 */
public class ProtoBufBasedServer implements IServer {
	private static Logger log = LogManager.getLogger(ProtoBufBasedServer.class);
	private boolean stop = false;

	public void run() {
		while (!stop) {
			try {
				log.info("ProtoBuf server is ok!");
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	public void initServer() {
		// TODO Auto-generated method stub

	}

	public void stopServer() {
		this.stop = true;
	}

}
