package cn.piesat.sec.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class WebConf extends WebMvcConfigurationSupport {

    @Value("${picture.path.magnetic_global}")
    private String picturePathMagneticGlobal;
    @Value("${picture.url.magnetic_global}")
    private String pictureUrlMagneticGlobal;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //映射static路径的请求到static目录下
        // 静态资源访问路径和存放路径配置
        //registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        // swagger访问配置
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/");
        //通过image访问本地的图片
        registry.addResourceHandler(pictureUrlMagneticGlobal+"/**").addResourceLocations("file:"+picturePathMagneticGlobal);
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(0,mappingJackson2HttpMessageConverter);
    }

}
