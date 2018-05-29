package cn.weit.test;

import cn.weit.happymo.Mo;
import cn.weit.happymo.annotion.MoController;
import cn.weit.happymo.annotion.MoParam;
import cn.weit.happymo.annotion.MoRequestMapping;
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
		return "Hello MoMo "+value;
	}


	public static void main(String[] args) {
		Mo.moServerBuilder()
				.withHost("127.0.0.1", 8080)
				.withBossNum(1)
				.withWorkerNum(4)
				.start();
	}
}
