//package cn.piesat.sec.utils;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Arrays;
//import java.util.TimeZone;
//@Component
//public class SpringUtil {
//
//    private static ApplicationContext applicationContext;
//    //0-2表示开发，测试，生产环境
//    private static byte environ = 0;
//
////    public static void setApplicationContext(ApplicationContext context) {
////        SpringUtil.applicationContext = context;
////        //获取当前的系统环境
////        Environment evn = applicationContext.getEnvironment();
////        String[] activeProfiles = evn.getActiveProfiles();
////        System.out.println("=============================================="+Arrays.toString(activeProfiles));
////        for (String profile : activeProfiles) {
////            if ("application-dev".equals(profile)){
////                break;
////            } else if ("test".equals(profile)){
////                environ = 1;
////                break;
////            } else if ("application-prod".equals(profile)){
////                environ = 2;
////                break;
////            }
////        }
////        callbackAfterRunning();
////    }
//
//    public static ApplicationContext getApplicationContext() {
//        return applicationContext;
//    }
//    //获取配置文件配置值
//    public static String getEvnProperty(String key){
//        return applicationContext.getEnvironment().getProperty(key);
//    }
//
//    //通过bean名称获取bean
//    public static Object getBeanByName(String name){
//        try {
//            return getApplicationContext().getBean(name);
//        } catch (Exception e){
//            return null;
//        }
//    }
//
//    public static Object getBeanByClassName(String className){
//        try{
//            Class aClass = Class.forName(className);
//            Object obj = getApplicationContext().getBean(aClass);
//            return obj;
//        }catch(Exception ex){
//            return null;
//        }
//    }
//
//    public static <T> T getBean(Class<T> clazz) {
//        if (SpringUtil.applicationContext == null) {
//            return null;
//        }
//        try {
//            return SpringUtil.applicationContext.getBean(clazz);
//        } catch (Exception e){
//            return null;
//        }
//    }
//    //当前是否为开发环境
//    public static boolean isDev() {
//        return environ == 0;
//    }
//    //是否为测试环境
//    public static boolean isTest(){
//        return environ == 1;
//    }
//    //是否为生产环境
//    public static boolean isProd(){
//        return environ == 2;
//    }
//    ///获取当前环境
//    public static byte getEnviron(){
//        return environ;
//    }
//    //获取当前请求
//    public static HttpServletRequest currentRequest(){
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        return servletRequestAttributes.getRequest();
//    }
//
//    private static void callbackAfterRunning(){
//        TimeZone.setDefault( TimeZone.getTimeZone("Asia/Shanghai") );
//    }
//}
