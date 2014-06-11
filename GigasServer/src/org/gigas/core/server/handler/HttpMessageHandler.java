package org.gigas.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gigas.core.server.BaseServer;

/**
 * http消息Hanlder
 * 
 * @author hank
 * 
 */

public class HttpMessageHandler extends SimpleChannelInboundHandler<HttpObject> {
	private BaseServer server;
	private static Logger log = LogManager.getLogger(HttpMessageHandler.class);
	private HttpPostRequestDecoder multipartDecoder;
	private DefaultHttpRequest request;

	public HttpMessageHandler(BaseServer whichserver) {
		this.server = whichserver;
	}

	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// log.info("channelActive");
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	}

	/**
	 * 消息读取完毕
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// log.info("channelReadComplete");
	}

	/**
	 * 用户连接进来了
	 */
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// log.info(ctx.channel().remoteAddress() + "connected!");
		// TODO IP-ALLOW
	}

	/**
	 * 用户断开连接
	 */
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// log.info(ctx.channel().remoteAddress() + "disconnected!");
	}

	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		log.info("channelWritabilityChanged");
	}

	/**
	 * 捕获到异常
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(cause, cause);
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		log.info("userEventTriggered");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof DefaultHttpRequest) {
			request = (DefaultHttpRequest) msg;//如果是DefaultHttpRequest实例
		}
		if (request != null && request.getMethod().equals(HttpMethod.GET)) {// GET方式传输
			if (!ctx.channel().isActive()) {
				return;
			}
			QueryStringDecoder decoder = new QueryStringDecoder(request.getUri(), CharsetUtil.UTF_8);
			Map<String, List<String>> parameters = decoder.parameters();
			if (parameters != null && !parameters.isEmpty()) {
				server.getHttpHandler().doHttp(ctx.channel(), parameters);
				request = null;
			}
			ctx.close();
		} else if (request != null && (request.getMethod().equals(HttpMethod.POST) || request.getMethod().equals(HttpMethod.PUT))) {// POST方式传输
			if (multipartDecoder == null) {
				multipartDecoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE), request, CharsetUtil.UTF_8);
			}
			if (multipartDecoder != null && (msg instanceof HttpContent)) {//如果传过来的是内容信息，则offer进decoder解析
				multipartDecoder.offer((HttpContent) msg);
				Map<String, List<String>> params = new HashMap<String, List<String>>();
				List<InterfaceHttpData> bodyHttpDatas = multipartDecoder.getBodyHttpDatas();
				for (InterfaceHttpData data : bodyHttpDatas) {//遍历内容
					if (InterfaceHttpData.HttpDataType.Attribute == data.getHttpDataType()) {
						MixedAttribute attribute = (MixedAttribute) data;
						attribute.setCharset(CharsetUtil.UTF_8);
						String value = attribute.getValue();
						LinkedList<String> linkedList = new LinkedList<String>();
						linkedList.add(value);
						params.put(attribute.getName(), linkedList);
					} else if (InterfaceHttpData.HttpDataType.FileUpload == data.getHttpDataType()) {
						// FileUpload fileUpload = (FileUpload) data;
						// fileUpload.getFile();
					} else if (InterfaceHttpData.HttpDataType.InternalAttribute == data.getHttpDataType()) {

					}
				}
				server.getHttpHandler().doHttp(ctx.channel(), params);
//				multipartDecoder.cleanFiles();
				multipartDecoder.destroy();
				multipartDecoder = null;
				request = null;
				ctx.close();
			}
		}
	}
}
