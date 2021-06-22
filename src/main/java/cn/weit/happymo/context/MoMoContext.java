package cn.weit.happymo.context;

import cn.weit.happymo.params.ControllerInfo;
import cn.weit.happymo.params.FilterInfo;
import cn.weit.happymo.scanner.PackageScanner;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author weitong
 */
@Slf4j
public class MoMoContext {
	private final List<FilterInfo> filters;

	private final Map<String, ControllerInfo> controllerInfoMap;

	public MoMoContext() {
		PackageScanner packageScanner = new PackageScanner();
		packageScanner.scan();
		this.filters = packageScanner.getFilters();
		this.controllerInfoMap = packageScanner.getControllerInfoMap();
	}

	public ControllerInfo getControllerInfo(String url) {
		return controllerInfoMap.get(controllerInfoMap.keySet().stream().filter(url::startsWith).findFirst().orElse(""));
	}

	public List<FilterInfo> getFilters(String url) {
		return filters.stream().filter(e -> url.contains(e.getUrl())).collect(Collectors.toList());
	}

}
