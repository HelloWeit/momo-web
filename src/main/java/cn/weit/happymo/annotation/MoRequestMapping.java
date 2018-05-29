package cn.weit.happymo.annotation;

import cn.weit.happymo.enums.RequestMethod;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoRequestMapping {
	String value() default "";
	RequestMethod method() default RequestMethod.GET;
}
