package com.github.airst.BTools;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Description: InnerUtil
 * User: qige
 * Date: 15/7/24
 * Time: 上午10:31
 */
public class InnerUtil {

    public static void  runTestMethod(Class<?> aClass, String[] p) throws Exception {
        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        Object bean = autowireCapableBeanFactory.autowire(aClass, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
        Method method = aClass.getMethod("test", String[].class);
        if(Modifier.isStatic(method.getModifiers())) {
            method.invoke(null, (Object)p);
        } else {
            method.invoke(bean, (Object)p);
        }
    }

}
