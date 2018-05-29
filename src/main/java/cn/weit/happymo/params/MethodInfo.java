package cn.weit.happymo.params;


import cn.weit.happymo.enums.RequestMethod;
import lombok.Data;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author weitong
 */
@Data
public class MethodInfo {
	private String uri;
	private RequestMethod method;
	private String name;
	private List<Parameter> parameters;
}
