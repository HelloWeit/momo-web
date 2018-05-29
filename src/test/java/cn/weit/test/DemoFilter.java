package cn.weit.test;

import cn.weit.happymo.annotation.MoFilter;
import cn.weit.happymo.annotation.MoFilterUrl;
import cn.weit.happymo.filter.AbstractMoMoFilter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@MoFilter(url = "/hello/mo")
@Slf4j
public class DemoFilter extends AbstractMoMoFilter {

	@MoFilterUrl
	private String filterUrl;

	@Override
	public void init() {
		log.info("init s:{}", filterUrl);
	}

	@Override
	public boolean before(FullHttpRequest request, FullHttpResponse response) {
		if (request.uri().startsWith(filterUrl)) {
			log.info("forbidden {}", request.uri());
			return false;
		}
		log.info("allow {}", request.uri());
		return true;
	}

	@Override
	public void after(FullHttpRequest request, FullHttpResponse response) {
		log.info("after");
	}
}
