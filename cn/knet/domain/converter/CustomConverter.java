package cn.knet.domain.converter;

import org.springframework.core.convert.converter.Converter;

/**
 * 自定义转换器
 * 去掉前后空格
 *
 * @author xuxiannian
 */
public class CustomConverter implements Converter<String, String> {

    @Override
    public String convert(String source) {
        try {
            source = source.trim();
            if (!"".equals(source)) {
                return source;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
