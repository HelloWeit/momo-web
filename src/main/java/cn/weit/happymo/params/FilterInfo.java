package cn.weit.happymo.params;

import cn.weit.happymo.filter.AbstractMoMoFilter;
import lombok.Data;

/**
 * @author weitong
 */
@Data
public class FilterInfo {
	private String url;
	private Class<?> clazz;
	private AbstractMoMoFilter abstractMoMoFilter;
}
