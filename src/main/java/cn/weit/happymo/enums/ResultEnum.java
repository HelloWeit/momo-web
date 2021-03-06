package cn.weit.happymo.enums;

import lombok.Getter;


/**
 * @author weitong
 */

@Getter
public enum ResultEnum {
	/**
	 *
	 */
	PARAM_ERROR(1, "参数错误"),
	SCAN_ERROR(2, "启动扫描出错"),
	INIT_ERROR(3, "初始化失败"),
	PARSE_ERROR(4, "解析失败"),
	FILTER_ERROR(5, "过滤器执行失败"),
	TYPE_NOT_SUPPORT(6, "暂不支持该类型"),;

	private final Integer code;

	private final String message;

	ResultEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
