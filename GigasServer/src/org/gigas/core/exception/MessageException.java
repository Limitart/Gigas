package org.gigas.core.exception;

/**
 * 消息异常
 * 
 * @author hank
 * 
 */
public class MessageException extends Exception {

	private static final long serialVersionUID = 2111285105044463661L;

	public MessageException() {
	}

	public MessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageException(String message) {
		super(message);
	}

	public MessageException(Throwable cause) {
		super(cause);
	}
}
