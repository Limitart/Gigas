/**
 * 
 */
package org.gigas.core.server.ChannelInitializer;

/**
 * 服务器解析协议枚举
 * 
 * @author hank
 * 
 */
public enum ChannelInitializerEnum {
	/**
	 * google protobuf
	 */
	GOOGLE_PROTOCOL_BUFFER, 
	/**
	 * 字节消息自定义
	 */
	BYTE_CUSTOMED, 
	/**
	 * 字符串消息自定义
	 */
	STRING_CUSTOMED;
	
}
