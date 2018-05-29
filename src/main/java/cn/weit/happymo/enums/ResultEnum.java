package cn.weit.happymo.enums;

import lombok.Getter;


@Getter
public enum ResultEnum {
    PARAM_ERROR(1, "参数错误"),
    SCAN_ERROR(2, "启动扫描出错"),
    INIT_ERROR(3, "初始化失败"),
    PARSE_ERROR(4, "解析失败"),
    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
