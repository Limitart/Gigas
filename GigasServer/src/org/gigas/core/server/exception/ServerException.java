package org.gigas.core.server.exception;

/**
 * 服务器异常
 * 
 * @author hank
 * 
 */
public class ServerException extends Exception {

	private static final long serialVersionUID = 2111285105044463661L;

	public ServerException() {
	}

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(String message) {
		super(message);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}
}
