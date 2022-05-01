package cn.knet.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public enum AuditApplayStatusEnum {

    AUDITING("AUDITING", "待审核"),RPAUDITING("RPAUDITING", "申诉待审核"), AUDIT("AUDIT", "已审核");

    @ToJson
    private String value;
    @ToJson
    private String text;

    AuditApplayStatusEnum(final String value, final String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
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
