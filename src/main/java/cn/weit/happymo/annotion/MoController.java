package cn.weit.happymo.annotion;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MoController {
	String url() default "";
}
