package cn.weit.happymo.service;

import cn.weit.happymo.context.MoMoContext;
import cn.weit.happymo.filter.AbstractMoMoFilter;
import cn.weit.happymo.params.ControllerInfo;
import cn.weit.happymo.params.FilterInfo;
import cn.weit.happymo.params.MethodInfo;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weitong
 */
@Slf4j
public class MoHandler extends ChannelInboundHandlerAdapter {

	private FullHttpRequest request;

	private FullHttpResponse response;

	private MoMoContext moMoContext;

	public MoHandler(MoMoContext moMoContext) {
		this.moMoContext = moMoContext;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		request = (FullHttpRequest) msg;
		response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
		ControllerInfo controllerInfo = moMoContext.getControllerInfo(request.uri());
		List<FilterInfo> filterInfos = moMoContext.getFilters(request.uri());
		doFilters(filterInfos,"before");
		doController(controllerInfo);
		doFilters(filterInfos, "after");
		ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	private void doController(ControllerInfo controllerInfo) throws Exception {
		MethodInfo methodInfo = controllerInfo.getMethodInfo(request.uri());
		String result="";
		Class<?> clazz = controllerInfo.getClazz();
		switch (methodInfo.getMethod()) {
			case GET:
				Map<String, String> params = parseParams(request.uri());

				List<Class<?>> classes = new ArrayList<>();
				List<String> values = new ArrayList<>();
				for (Parameter parameter : methodInfo.getParameters()) {
					classes.add(parameter.getType());
					values.add(params.get(parameter.getName()));
				}
				Class[] clazzType = new Class[classes.size()];
				classes.toArray(clazzType);
				Method method = clazz.getDeclaredMethod(methodInfo.getName(),clazzType);
				String[] paramValues = new String[values.size()];
				values.toArray(paramValues);
				Object object = method.invoke(clazz.newInstance(), paramValues);
				result = (String) object;
				break;
			case PUT:
			case POST:
				ByteBuf content = request.content();
				String jsonStr = content.toString(CharsetUtil.UTF_8);
				Gson gson = new Gson();
				Class<?> classType = methodInfo.getParameters().get(0).getType();
				method = clazz.getDeclaredMethod(methodInfo.getName(), classType);
				object = method.invoke(clazz.newInstance(), gson.fromJson(jsonStr, classType));
				result = (String)object;
				break;
			default:
				break;
		}

		response.content().writeBytes(result.getBytes());
		response.headers().set("Content-Type", "text/plain");
		response.headers().set("Content-Length", response.content().readableBytes());
	}

	private Map<String, String> parseParams(String uri) {
		Map<String, String> params = new HashMap<>();
		int pos = uri.indexOf("?");
		if (pos >= 0) {
			for (String pair : uri.substring(pos + 1).split("&")) {
				String[] kv = pair.split("=");
				if (kv.length == 2) {
					params.put(kv[0], kv[1]);
				}
			}
		}
		return params;
	}
	private void doFilters(List<FilterInfo> filterInfos, String methodName) throws Exception {
		if (filterInfos == null || filterInfos.isEmpty()) {
			return;
		}
		for (FilterInfo filterInfo : filterInfos) {
			Class<? extends AbstractMoMoFilter> clazz = filterInfo.getClazz();
			Method method = clazz.getDeclaredMethod(methodName, FullHttpRequest.class, FullHttpResponse.class);
			method.invoke(clazz.newInstance(), request, response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Netty exceptionCaught", cause);
		FullHttpResponse errorResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		ctx.channel().writeAndFlush(errorResponse).addListener(ChannelFutureListener.CLOSE);
		ctx.close();
	}
}
