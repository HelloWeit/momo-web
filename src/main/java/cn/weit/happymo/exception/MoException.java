package cn.weit.happymo.exception;

import cn.weit.happymo.enums.ResultEnum;

public class MoException extends RuntimeException {
	private Integer code;

	public MoException(Integer code, String message) {
		super(message);
		this.code = code;
	}

	public MoException(ResultEnum resultEnum) {
		super(resultEnum.getMessage());
		this.code = resultEnum.getCode();
	}
}
