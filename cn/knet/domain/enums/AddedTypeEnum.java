package cn.knet.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum AddedTypeEnum {
	
	NAMECARD("NAMECARD","名片"),
	URL("URL","转发"),
	SHOW("SHOW","秀场"),
	FAMOUS("FAMOUS","名人"),
	PROFILE("PROFILE","简介"),
	EQXIU("EQXIU","易企秀"),
	CHUANGKIT("CHUANGKIT","创客贴"),
	SCENICSPOT("SCENICSPOT","景点"),
	POEMS("POEMS","古诗文")
	;
	private static final Map<String, String> MAPPING = new LinkedHashMap<String, String>();

	private static final Map<String, String> INVERSE_MAPPING = new LinkedHashMap<String, String>();
	
	private AddedTypeEnum(String value, String text) {
		this.value = value;
		this.text = text;
	}

	@ToJson
	String value;
	@ToJson
	String text;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}


	static {
		for (AddedTypeEnum em : AddedTypeEnum.values()) {
			MAPPING.put(em.getText(), em.getValue());
			INVERSE_MAPPING.put(em.getValue(), em.getText());
		}
	}
	/**
	 * @return
	 */
	public static Map<String, String> mapping() {
		return MAPPING;
	}

	/**
	 * @return
	 */
	public static Map<String, String> inverseMapping() {
		return INVERSE_MAPPING;
	}

	@JsonValue
	public Map<String, Object> jsonValue() throws IllegalArgumentException,
			IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = getClass().getDeclaredFields();
		for (Field f : fields) {
			ToJson toJson = f.getAnnotation(ToJson.class);
			if (toJson != null) {
				f.setAccessible(true);
				Object v = f.get(this);
				map.put(f.getName(), v);
			}
		}
		return map;
	}
}
