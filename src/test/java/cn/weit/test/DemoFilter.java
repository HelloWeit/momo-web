package cn.weit.test;

import cn.weit.happymo.annotion.MoFilter;
import cn.weit.happymo.filter.AbstractMoMoFilter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@MoFilter
@Slf4j
public class DemoFilter extends AbstractMoMoFilter {
	private String s = "";

	@Override
	public void init() {
		s = "we love u";
	}

	@Override
	public void before(FullHttpRequest request, FullHttpResponse response) {

		log.info("before");
	}

	@Override
	public void after(FullHttpRequest request, FullHttpResponse response) {
		log.info("after");

	}
}
