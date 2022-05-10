package cn.knet.seal.services;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Service
public class MyClassService {

	@Autowired
	Properties properties;
	
	public  List<Map<String, String>> getScheduledList(String path) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ ClassUtils.convertClassNameToResourcePath("cn.knet.seal") + path;
		
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		try {
			Resource[] resources = resourcePatternResolver.getResources(pattern);
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
			for (Resource resource : resources) {
				if (resource.isReadable()) {

					MetadataReader reader = readerFactory.getMetadataReader(resource);
					if (reader.getAnnotationMetadata().hasAnnotation("org.springframework.stereotype.Service")) {
						String className = reader.getClassMetadata().getClassName();
						Method[] ms = Class.forName(className).getDeclaredMethods();
						for (Method m : ms) {
							Scheduled an = m.getAnnotation(Scheduled.class);
							if (an != null) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("className", className);
								map.put("methodName", m.getName());
								String[] cron=an.cron().replace("${", "").replace("}", "").split(":");
								if(cron.length==2){
									map.put("cron", properties.getProperty(cron[0],cron[1]));
								}else{
									map.put("cron", properties.getProperty(cron[0]));
								}
								
								list.add(map);
							}
						}
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
