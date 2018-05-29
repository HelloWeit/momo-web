package cn.weit.happymo.annotion;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoBody {
	boolean required() default true;
}
