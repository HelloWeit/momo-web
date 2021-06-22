package cn.weit.happymo.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author weitong
 */
public abstract class AbstractMoMoFilter {

	/**
	 * filter初始化
	 */
	public abstract void init();

	/**
	 * 前置处理
	 * @param request 请求
	 * @param response 响应
	 */
	public abstract boolean before(FullHttpRequest request, FullHttpResponse response);

	/**
	 * 后置处理
	 * @param request 请求
	 * @param response 响应
	 * */
	public abstract boolean after(FullHttpRequest request, FullHttpResponse response);
}
