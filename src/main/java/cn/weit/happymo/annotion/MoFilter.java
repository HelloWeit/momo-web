package cn.weit.happymo.annotion;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoFilter {
	int value() default 0;
	String url() default "";
}
