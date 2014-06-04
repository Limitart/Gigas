package org.gigas.core.server.config;

/**
 * 服务器配置
 * 
 * @author hank
 * 
 */
public class ServerConfig {
	private int port;
	private byte[] securityBytes;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public byte[] getSecurityBytes() {
		return securityBytes;
	}

	public void setSecurityBytes(byte[] securityBytes) {
		this.securityBytes = securityBytes;
	}

}
