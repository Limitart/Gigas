package org.gigas.core.client.config;

/**
 * 客户端配置
 * 
 * @author hank
 * 
 */
public class ClientConfig {
	private String ip;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
