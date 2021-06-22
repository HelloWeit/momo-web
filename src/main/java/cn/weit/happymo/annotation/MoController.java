package cn.weit.happymo.annotation;

import java.lang.annotation.*;

/**
 * @author weitong
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MoController {
	String url() default "";
}
