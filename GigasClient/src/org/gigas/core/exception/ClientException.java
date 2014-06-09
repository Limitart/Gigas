package org.gigas.core.exception;

/**
 * 客户端异常
 * 
 * @author hank
 * 
 */
public class ClientException extends Exception {

	private static final long serialVersionUID = 2111285105044463661L;

	public ClientException() {
	}

	public ClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientException(String message) {
		super(message);
	}

	public ClientException(Throwable cause) {
		super(cause);
	}
}
