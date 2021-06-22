package cn.weit.happymo.scanner;

import cn.weit.happymo.annotation.*;
import cn.weit.happymo.filter.AbstractMoMoFilter;
import cn.weit.happymo.params.ControllerInfo;
import cn.weit.happymo.params.FilterInfo;
import cn.weit.happymo.params.MethodInfo;
import cn.weit.happymo.enums.ResultEnum;
import cn.weit.happymo.exception.MoException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * 核心方法：扫描所有携带@MoFilter @MoController 注解的类
 * @author weitong
 */
@Slf4j
public class PackageScanner {

	private final ClassLoader classLoader;

	private final Map<Integer, FilterInfo> filterMap = new TreeMap<>();
	@Getter
	private final Map<String, ControllerInfo> controllerInfoMap = new HashMap<>();

	public PackageScanner() {
		this.classLoader = getClass().getClassLoader();
	}

	public void scan() {
		scan("");
	}

	public void scan(String... packages) {
		if (packages == null || packages.length == 0) {
			throw new MoException(ResultEnum.PARAM_ERROR);
		}
		Arrays.stream(packages).map(this::parse).flatMap(Collection::stream).forEach(this::doScan);
	}

	private void doScan(String className) {
		try {
			Class<?> clazz = classLoader.loadClass(className);
			if (AbstractMoMoFilter.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(MoFilter.class)) {
				scanFilter(clazz);
			}
			if (clazz.isAnnotationPresent(MoController.class)) {
				scanController(clazz);
			}

		} catch (Exception e) {
			log.error("doScan", e);
			throw new MoException(ResultEnum.SCAN_ERROR);
		}
	}

	private void scanFilter(Class<?> clazz) throws Exception {
		MoFilter moFilter = clazz.getAnnotation(MoFilter.class);
		Field[] fields = clazz.getDeclaredFields();
		AbstractMoMoFilter abstractMoMoFilter = (AbstractMoMoFilter) clazz.newInstance();
		for (Field field : fields) {
			if (field.isAnnotationPresent(MoFilterUrl.class)) {
				field.setAccessible(true);
				field.set(abstractMoMoFilter, moFilter.url());
				break;
			}
		}
		FilterInfo filterInfo = new FilterInfo();
		filterInfo.setUrl(moFilter.url());
		filterInfo.setClazz(clazz);
		filterInfo.setAbstractMoMoFilter(abstractMoMoFilter);
		filterMap.put(moFilter.value(), filterInfo);

		Method method = clazz.getDeclaredMethod("init");
		method.invoke(abstractMoMoFilter);
	}

	private void scanController(Class<?> clazz) {
		MoController moController = clazz.getAnnotation(MoController.class);
		ControllerInfo controllerInfo = new ControllerInfo();
		controllerInfo.setUri(moController.url());
		controllerInfo.setClassName(clazz.getName());
		controllerInfo.setClazz(clazz);

		Map<String, MethodInfo> methodInfoMap = Maps.newHashMap();
		Arrays.stream(clazz.getMethods()).filter(m -> m.isAnnotationPresent(MoRequestMapping.class))
				.forEach(method -> {
					MethodInfo methodInfo = new MethodInfo();
					methodInfo.setParameters(Arrays.stream(method.getParameters())
							.filter(p -> p.isAnnotationPresent(MoParam.class) || p.isAnnotationPresent(MoBody.class))
							.collect(Collectors.toCollection(ArrayList::new)));
					MoRequestMapping moRequestMapping = method.getAnnotation(MoRequestMapping.class);
					methodInfo.setName(method.getName());
					methodInfo.setUri(moRequestMapping.value());
					methodInfo.setMethod(moRequestMapping.method());
					String ulr = moController.url() + moRequestMapping.value();
					methodInfoMap.put(ulr, methodInfo);
				});
		controllerInfo.setMethodInfos(methodInfoMap);
		controllerInfoMap.put(moController.url(), controllerInfo);
	}

	private List<String> parse(String base) {

		String splashPath = base.replace("\\.", "/");
		URL url = classLoader.getResource(splashPath);
		if (url == null) {
			log.info("url null, basePackage:{} ", base);
			return Lists.newArrayList();
		}
		File file = new File(url.getPath());
		if (!isJarFile(file.getName())) {
			return readFromDirectory(file, splashPath);
		}
		try {
			return readFromJarFile(file, splashPath);
		} catch (Exception e) {
			log.error("parse jar error", e);
			throw new MoException(ResultEnum.PARSE_ERROR);
		}
	}

	private List<String> readFromJarFile(File file, String splashedPackageName) throws IOException {
		JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
		JarEntry entry = jarIn.getNextJarEntry();

		List<String> nameList = Lists.newArrayList();
		while (null != entry) {
			String name = entry.getName();
			if (name.startsWith(splashedPackageName) && isClassFile(name)) {
				nameList.add(trimExtension(name));
			}

			entry = jarIn.getNextJarEntry();
		}
		return nameList;
	}

	private List<String> readFromDirectory(File file, String splashedPackageName) {
		File[] files = file.listFiles();
		if (files == null) {
			return Lists.newArrayList();
		}
		List<String> nameList = Lists.newArrayList();
		for (File subFile : files) {
			if (subFile.isDirectory()) {
				List<String> subDirectoryList = readFromDirectory(subFile,
						splashedPackageName + "/" + subFile.getName());
				nameList.addAll(subDirectoryList);
			} else if (isClassFile(subFile.getName())) {
				nameList.add(trimExtension(splashedPackageName + "/" + subFile.getName()));
			}
		}

		return nameList;
	}

	private boolean isClassFile(String name) {
		return name.endsWith(".class");
	}

	private boolean isJarFile(String name) {
		return name.endsWith(".jar");
	}

	private String trimExtension(String string) {
		int pos = string.lastIndexOf('.');
		if (-1 != pos) {
			return string.substring(1, pos).replaceAll("/", ".");
		}

		return string.replaceAll("/", ".");
	}

	public List<FilterInfo> getFilters() {
		return Lists.newArrayList(filterMap.values());
	}


	public static void main(String[] args) {
		PackageScanner scanner = new PackageScanner();
		scanner.scan();
	}

}
