package cn.piesat.sec.comm.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "thread.param")
public class TheadProperties {
    /**
     * 核心线程池数量
     */
    private int corePoolSize;

    /**
     * 最大线程池数量
     */
    private int maxPoolSize;

    /**
     * 线程存活最大时间
     */
    private int keepAliveSeconds;

    /**
     * 线程队列的最大数量
     */
    private int queueCapacity;

    /**
     * 是否允许核心线程超时
     */
    private boolean allowCoreThreadTimeout;
}
