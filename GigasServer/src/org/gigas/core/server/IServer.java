package org.gigas.core.server;

/**
 * 服务器接口
 * 
 * @author hank
 * 
 */
public interface IServer {
	/**
	 * 初始化服务器
	 */
	public void startServer();

	/**
	 * 停止服务器
	 */
	public void stopServer();

	/**
	 * 是否已经启动
	 * 
	 * @return
	 */
	public boolean isStart();
}
