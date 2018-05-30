package cn.weit.happymo.session;

import cn.weit.happymo.enums.SessionTime;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weitong
 */
public class SessionManger {
	private volatile static SessionManger instance;

	private final Map<SessionTime, SessionHolder> sessionHolderMap = new ConcurrentHashMap<>();

	private SessionManger(){}

	public static SessionManger Instance() {
		if(instance == null){
			synchronized (SessionManger.class) {
				if(instance == null){
					instance = new SessionManger();
				}
			}
		}
		return instance;
	}

	public void init(int workerNum) {
		Arrays.stream(SessionTime.values())
				.filter(s -> !s.equals(SessionTime.ERROR))
				.forEach(s ->sessionHolderMap.put(s, new SessionHolder()
						.withExpireTime(s.getTime())
						.withWorkerNum(workerNum)
						.builder()));
	}

	public void setCookie(FullHttpRequest request, FullHttpResponse response, String name, String value) {
		setCookie(request, response, name, value, 1, false, false);
	}

	public void setCookie(FullHttpRequest request, FullHttpResponse response, String name, String value,
						  long maxAge, boolean isHttpOnly, boolean isSecure) {
		Cookie cookie = new DefaultCookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		cookie.setHttpOnly(isHttpOnly);
		cookie.setSecure(isSecure);
		cookie.setDomain(getDomain(request));

		response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie));
	}

	public Object getSessionValue(String cookieValue, SessionTime sessionTime) {
		return Optional.ofNullable(sessionHolderMap.get(sessionTime)).map(s -> s.get(cookieValue)).orElse(null);
	}

	public boolean equals(String cookieValue) {
		return sessionHolderMap.values().stream().map(s -> s.get(cookieValue)).findFirst().isPresent();
	}

	public void setSession(String key, Object value, SessionTime sessionTime) {
		SessionHolder sessionHolder = sessionHolderMap.get(sessionTime);
		if (sessionHolder!=null) {
			sessionHolder.set(key, value);
		}
	}

	private String getDomain(FullHttpRequest request) {
		String url = request.headers().get("host");
		String[] split = url.split(":");
		return split[0];

	}

}
