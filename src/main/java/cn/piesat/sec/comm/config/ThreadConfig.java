//package cn.piesat.sec.comm.config;
//
//import cn.piesat.sec.comm.properties.TheadProperties;
//import lombok.Data;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Data
//@EnableAsync
//@Configuration
//public class ThreadConfig {
//    @Autowired
//    private TheadProperties threadProperties;
//
//    @Bean("asyncExecutor")
//    public Executor asyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        // 配置核心线程数量
//        executor.setCorePoolSize(threadProperties.getCorePoolSize());
//        // 配置最大线程数
//        executor.setMaxPoolSize(threadProperties.getMaxPoolSize());
//        // 配置队列容量
//        executor.setQueueCapacity(threadProperties.getQueueCapacity());
//        // 配置空闲线程存活时间
//        executor.setKeepAliveSeconds(threadProperties.getKeepAliveSeconds());
//        // 是否允许核心线程超时
//        executor.setAllowCoreThreadTimeOut(threadProperties.isAllowCoreThreadTimeout());
//        // 设置拒绝策略，直接在execute方法的调用线程中运行被拒绝的任务
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        // 执行初始化
//        executor.initialize();
//        return executor;
//    }
//}
