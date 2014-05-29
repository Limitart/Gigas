package org.gigas.core.server;

/**
 * 服务器接口
 * 
 * @author hank
 * 
 */
public interface IServer extends Runnable {
	/**
	 * 初始化服务器
	 */
	public void initServer();
	
	/**
	 * 停止服务器
	 */
	public void stopServer();
}
