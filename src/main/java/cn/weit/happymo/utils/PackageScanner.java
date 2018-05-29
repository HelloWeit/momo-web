package cn.weit.happymo.utils;

import cn.weit.happymo.annotion.*;
import cn.weit.happymo.filter.AbstractMoMoFilter;
import cn.weit.happymo.params.ControllerInfo;
import cn.weit.happymo.params.FilterInfo;
import cn.weit.happymo.params.MethodInfo;
import cn.weit.happymo.enums.ResultEnum;
import cn.weit.happymo.exception.MoException;
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
 * @author weitong
 */
@Slf4j

public class PackageScanner {

	private ClassLoader classLoader;

	private Map<Integer, FilterInfo> filterMap = new TreeMap<>();
	@Getter
	private Map<String, ControllerInfo> controllerInfoMap = new HashMap<>();

	public PackageScanner() {
		this.classLoader = getClass().getClassLoader();
	}

	public void scan() {
		scan("");
	}

	public void scan(String... packages) {
		if (packages == null || packages.length==0) {
			throw new MoException(ResultEnum.PARAM_ERROR);
		}
		Arrays.stream(packages).map(b->parse(b)).flatMap(sList -> sList.stream()).forEach(s -> doScan(s));
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
			log.error("doScan",e);
			throw new MoException(ResultEnum.SCAN_ERROR);
		}
	}

	private void scanFilter(Class<?> clazz) {
		try {
			Method method = clazz.getDeclaredMethod("init");
			method.invoke(clazz.newInstance());
			MoFilter moFilter = clazz.getAnnotation(MoFilter.class);
			FilterInfo filterInfo = new FilterInfo();
			filterInfo.setClazz((Class<? extends AbstractMoMoFilter>) clazz);
			filterInfo.setUrl(moFilter.url());
			filterMap.put(moFilter.value(), filterInfo);
		} catch (Exception e) {
			log.error("scan filter error", e);
			throw new MoException(ResultEnum.SCAN_ERROR);
		}
	}

	private void scanController(Class<?> clazz) {
		MoController moController = clazz.getAnnotation(MoController.class);
		ControllerInfo controllerInfo = new ControllerInfo();
		controllerInfo.setUri(moController.url());
		controllerInfo.setClassName(clazz.getName());
		controllerInfo.setClazz(clazz);

		Map<String, MethodInfo> methodInfoMap = new HashMap<>();
		Arrays.stream(clazz.getMethods()).filter(m -> m.isAnnotationPresent(MoRequestMapping.class))
				.forEach(method -> {
					MethodInfo methodInfo = new MethodInfo();
					methodInfo.setParameters(Arrays.stream(method.getParameters())
							.filter(p -> p.isAnnotationPresent(MoParam.class)
									|| p.isAnnotationPresent(MoBody.class))
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
		List<String> classNames = new ArrayList<>();

		String splashPath = base.replace("\\.","/");
		URL url = classLoader.getResource(splashPath);
		if (url == null) {
			log.info("url null, basePackage:{} ", base);
			return classNames;
		}
		File file = new File(url.getPath());
		if (file == null) {
			log.debug("url null, basePackage:{}", base);
			return classNames;
		}
		if (isJarFile(file.getName())) {
			try {
				classNames = readFromJarFile(file, splashPath);
			} catch (Exception e) {
				log.error("parse jar error", e);
				throw new MoException(ResultEnum.PARSE_ERROR);
			}
		} else {
			classNames = readFromDirectory(file, splashPath);
		}

		return classNames;
	}

	private List<String> readFromJarFile(File file, String splashedPackageName) throws IOException {
		JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
		JarEntry entry = jarIn.getNextJarEntry();

		List<String> nameList = new ArrayList<>();
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
		List<String> nameList = new ArrayList<>();

		File[] files = file.listFiles();
		if (files != null) {
			for (File subFile : files) {
				if (subFile.isDirectory()) {
					List<String> subDirectoryList = readFromDirectory(subFile,
							splashedPackageName + "/" + subFile.getName());
					if (subDirectoryList != null) {
						nameList.addAll(subDirectoryList);
					}
				} else if (isClassFile(subFile.getName())) {
					nameList.add(trimExtension(splashedPackageName + "/" + subFile.getName()));
				}
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
			return string.substring(1, pos).replaceAll("/",".");
		}

		return string.replaceAll("/",".");
	}

	public List<FilterInfo> getFilters() {
		return filterMap.values().stream().collect(Collectors.toList());
	}


	public static void main(String[] args) {
		PackageScanner scanner = new PackageScanner();
		scanner.scan();
	}

}
