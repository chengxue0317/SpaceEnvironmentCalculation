package cn.piesat.sec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 *
 * @author piesat
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    @Value("${picture.path.magnetic_global}")
    private String picturePathMagneticGlobal;
    @Value("${picture.url.magnetic_global}")
    private String pictureUrlMagneticGlobal;
    @Value("${spring.jackson.date-format}")
    private String pattern;

    @Value("${picture.path.global_radiation_env}")
    private String picturePathGlobalRadiationEnv;
    @Value("${picture.url.global_radiation_env}")
    private String pictureUrlGlobalRadiationEnv;

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
//        config.addAllowedOrigin("*");
        config.addAllowedOriginPattern("*");
//        config.setAllowedOriginPatterns(Arrays.asList("*"));
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源访问路径和存放路径配置
        //registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        // swagger访问配置
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/");
        //通过image访问本地的图片
        registry.addResourceHandler(pictureUrlMagneticGlobal+"/**").addResourceLocations("file:"+picturePathMagneticGlobal);
        registry.addResourceHandler(pictureUrlGlobalRadiationEnv+"/**").addResourceLocations("file:"+picturePathGlobalRadiationEnv);
    }

}
