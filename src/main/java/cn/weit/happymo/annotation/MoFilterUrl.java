package cn.weit.happymo.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoFilterUrl {
	boolean required() default true;
}
