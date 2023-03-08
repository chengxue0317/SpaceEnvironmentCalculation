package cn.piesat.sec.controller;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Map;

@Api
@RestController
@RequestMapping("dynamicStatic")
public class StaticResourceDynamicRegistryController {
    @Autowired
    private ApplicationContext applicationContext;

    @ApiOperation(value = "registry")
    @PostMapping(value = "registry", produces = MediaType.APPLICATION_JSON_VALUE)
    public String registry(@ApiParam(defaultValue="/fulizhe") @RequestParam String resourceHandler, //
                           @ApiParam(defaultValue="E:/data/") @RequestParam String resourceLocations) {
        registerHandlersForAdditionalStatisResource(Collections.singletonMap(resourceHandler, resourceLocations));

        return "SUCCESS";
    }

    private void registerHandlersForAdditionalStatisResource(Map<String, String> registerMapping) {
        // 这些值的由来参见:
        final UrlPathHelper mvcUrlPathHelper = applicationContext.getBean("mvcUrlPathHelper", UrlPathHelper.class);
        final ContentNegotiationManager mvcContentNegotiationManager = applicationContext
                .getBean("mvcContentNegotiationManager", ContentNegotiationManager.class);
        final ServletContext servletContext = applicationContext.getBean(ServletContext.class);
        // SimpleUrlHandlerMapping.java
        final HandlerMapping resourceHandlerMapping = applicationContext.getBean("resourceHandlerMapping",
                HandlerMapping.class);

        // 这里存放的是springmvc已经建立好的映射处理
        @SuppressWarnings("unchecked")
        final Map<String, Object> handlerMap = (Map<String, Object>) ReflectUtil.getFieldValue(resourceHandlerMapping,
                "handlerMap");

        final ResourceHandlerRegistry resourceHandlerRegistry = new ResourceHandlerRegistry(applicationContext,
                servletContext, mvcContentNegotiationManager, mvcUrlPathHelper);

        for (Map.Entry<String, String> entry : registerMapping.entrySet()) {
            String urlPath = entry.getKey();
            String resourceLocations = entry.getValue();
            final String urlPathDealed = StrUtil.appendIfMissing(urlPath, "/**");
            final String resourceLocationsDealed = StrUtil.appendIfMissing(resourceLocations, "/");

            // 先移除之前自定义注册过的...
            handlerMap.remove(urlPathDealed);
            // 重新注册
            resourceHandlerRegistry.addResourceHandler(urlPathDealed)
                    .addResourceLocations("file:" + resourceLocationsDealed);
        }

        final Map<String, ?> additionalUrlMap = ReflectUtil
                .<SimpleUrlHandlerMapping>invoke(resourceHandlerRegistry, "getHandlerMapping").getUrlMap();

        ReflectUtil.<Void>invoke(resourceHandlerMapping, "registerHandlers", additionalUrlMap);
    }
}

