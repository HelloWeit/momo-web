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
				.start();
	}
}

@Data
public class Toys {
	private String toy;
	private int num;
}
```
简单验证：  
*Get*请求：浏览器运行`http://localhost:8080/hello/mo?value=momo`, 会显示 `We love momo`.  
*Post*请求：*PostMan*运行`http://localhost:8080/hello/momo`, 同时配置Body里面json格式参数,会显示 `MoMo's favorite toy is xxx`.  
  
添加*Filter*：
```Java
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
```
*@MoFilter* 默认顺序数字为0，数字越小优先执行。  
  
*Session*/*Cookie*设置：  
SessionManger提供四种方法支持*Session*/*Cookie*设置：
- response设置cookie  
 > void setCookie(FullHttpRequest request, FullHttpResponse response, String name, String value)  
 > void setCookie(FullHttpRequest request, FullHttpResponse response, String name, String value,
 						  long maxAge, boolean isHttpOnly, boolean isSecure)  
- 获取session  
 > Object getSessionValue(String cookieValue, SessionTime sessionTime)  
- 比较cookie和session  
 > boolean equals(String cookieValue)  
- 设置session  
 > void setSession(String key, Object value, SessionTime sessionTime)
 
### 4.版本
  - v1.2   支持session、cookie
  - v1.1   完善*Filter*功能，修改部分命名
  - v1.0.1 修复*bug*，优化注解扫描，增加*readme.md*
  - v1.0   支持注解调用，支持常用的*Get*、*Post*请求，支持*Filter*。