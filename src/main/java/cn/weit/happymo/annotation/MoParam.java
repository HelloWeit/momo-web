package cn.weit.happymo.annotation;

import java.lang.annotation.*;

/**
 * @author weitong
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoParam {
	boolean required() default true;
}
