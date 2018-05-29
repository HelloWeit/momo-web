# MoMoWeb
### 1.说明
利用Netty实现了一个类似于*SpringMVC*的*web*框架。运行时不需要*tomcat*这些*web*容器，跑起来刚刚的。:smirk:
### 2.运行条件
需要*JDK8*以上，同时需要在编译时候配置 _*"-parameters"*_。
>*JDK7*及以下通过反射是拿不到方法里的具体参数名，会导致无法获取*url*带上来的值。看了*spring*的源码很2很暴力，为版本的兼容性，读取文件后直接在里面找参数名。
我既不暴力也不2，偷个懒利用版本特性直接获取了。:blush:
### 3.示例Demo
利用注解完成：
```Java
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
```
运行`http://localhost:8080/hello/mo?value=love`, 浏览器会显示 `Hello MoMo love`.
添加*Filter*：
```Java
@MoFilter
@Slf4j
public class DemoFilter extends AbstractMoMoFilter {
	private String s;

	@Override
	public void init() {
		s = "we love u";
	}

	@Override
	public void before(FullHttpRequest request, FullHttpResponse response) {

		log.info("before：{}",s);
	}

	@Override
	public void after(FullHttpRequest request, FullHttpResponse response) {
		log.info("after {}", s);

	}
}
```
*@MoFilter* 默认顺序为0，数字越小优先执行。

### 4.版本

  - v1.0.1 修复*bug*，优化注解扫描，增加*readme.md*
  - v1.0   支持注解调用，支持常用的*Get*、*Post*请求，支持*Filter*。