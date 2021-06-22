package cn.weit.test;

import cn.weit.happymo.Mo;
import cn.weit.happymo.annotation.MoBody;
import cn.weit.happymo.annotation.MoController;
import cn.weit.happymo.annotation.MoParam;
import cn.weit.happymo.annotation.MoRequestMapping;
import cn.weit.happymo.enums.RequestMethod;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weitong
 */
@MoController(url = "/hello")
@Slf4j
public class Demo {

	@MoRequestMapping(value = "/mo", method = RequestMethod.GET)
	public String hello(@MoParam String value) {
		log.info("value:{}", value);
		return "We love "+value;
	}

	@MoRequestMapping(value = "/momo", method = RequestMethod.POST)
	public String hi(@MoBody Toys toys) {
		log.info("toys:{}", toys);
		return "MoMo's favorite toy is " + toys.getToy();
	}


	public static void main(String[] args) {
		Mo.moServerBuilder()
				.withHost("127.0.0.1", 8080)
				.withBossNum(1)
				.withWorkerNum(4)
				.build()
				.start();
	}
}
