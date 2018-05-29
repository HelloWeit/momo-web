package cn.weit.happymo.params;

import lombok.Data;

import java.util.Map;

/**
 * @author weitong
 */
@Data
public class ControllerInfo {
	private Map<String, MethodInfo> methodInfos;
	private String className;
	private String uri;
	private Class<?> clazz;

	public MethodInfo getMethodInfo(String url) {
		return methodInfos.get(methodInfos.keySet().stream().filter(x->url.startsWith(x)).findFirst().get());
	}

}
