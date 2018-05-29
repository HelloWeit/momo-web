package cn.weit.happymo.context;

import cn.weit.happymo.params.ControllerInfo;
import cn.weit.happymo.params.FilterInfo;
import cn.weit.happymo.utils.PackageScanner;
import lombok.extern.slf4j.Slf4j;

import javax.sql.rowset.Predicate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author weitong
 */
@Slf4j
public class MoMoContext {
	private List<FilterInfo> filters;

	private final Map<String, ControllerInfo> controllerInfoMap;

	private PackageScanner packageScanner;

	public MoMoContext() {
		packageScanner = new PackageScanner();
		packageScanner.scan();
		this.filters = packageScanner.getFilters();
		this.controllerInfoMap = packageScanner.getControllerInfoMap();
	}

	public ControllerInfo getControllerInfo(String url) {
		return controllerInfoMap.get(controllerInfoMap.keySet().stream().filter(k->url.startsWith(k)).findFirst().get());
	}

	public List<FilterInfo> getFilters(String url) {
		return filters.stream().filter(e -> url.contains(e.getUrl())).collect(Collectors.toList());
	}

}
