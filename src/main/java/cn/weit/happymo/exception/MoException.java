package cn.weit.happymo.exception;

import cn.weit.happymo.enums.ResultEnum;

/**
 * @author weitong
 */
public class MoException extends RuntimeException {
	private Integer code;

	public MoException(Integer code, String message) {
		super(message);
		this.code = code;
	}

	public MoException(ResultEnum resultEnum) {
		this(resultEnum.getCode(), resultEnum.getMessage());
	}
}
