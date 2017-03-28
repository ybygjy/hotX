package com.github.airst;

import com.github.airst.CTools.StringUtil;
import com.github.airst.CTools.server.Server;
import com.github.airst.database.MySqlExecutor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.sql.DataSource;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Description: HotXBoot
 * User: qige
 * Date: 15/11/5
 * Time: 下午8:13
 */
public class HotXBoot {

    public static Server server = null;

    private static Method resetClassLoaderMethod = null;

    public static synchronized void boot(String appName, Instrumentation inst, Method resetMethod, ClassLoader classLoader, Class applicationClass) throws Exception {
        try {
            resetClassLoaderMethod = resetMethod;
            if (StaticContext.getInst() == null) {

                StaticContext.setInst(inst);
                StaticContext.setClassLoader(classLoader);
                StaticContext.setApplicationClass(applicationClass);

                fetchSpringContext();
                start();
            }
            if (!StringUtil.isBlank(appName) && !StringUtil.isNullStr(appName)) {
                StaticContext.setAppName(appName);
            }
            initDbExecutor();
        } catch (Exception e) {
            shutdown();
            throw e;
        }
    }
    public static void fetchSpringContext() throws Exception {
         if(StaticContext.getApplicationContext() == null) {
             if(StaticContext.getApplicationClass() != null) {
                 Method method = StaticContext.getApplicationClass().getMethod("getApplicationContext");
                 ApplicationContext applicationContext = (ApplicationContext) method.invoke(null);
                 if(applicationContext != null) {
                     StaticContext.setApplicationContext(applicationContext);
                 }
             }
             if(StaticContext.getApplicationContext() == null) {
                 Thread.currentThread().setContextClassLoader(HotXBoot.class.getClassLoader().getParent());
                 StaticContext.setApplicationContext(ContextLoader.getCurrentWebApplicationContext());
             }
        }
    }

    public static void initDbExecutor() throws Exception {
        if(StaticContext.getApplicationContext() == null) {
            return;
        }
        AutowireCapableBeanFactory beanFactory = StaticContext.getApplicationContext().getAutowireCapableBeanFactory();

        @SuppressWarnings("unchecked")
        Map<String, DataSource> dataSourceMap=((ConfigurableListableBeanFactory)beanFactory).getBeansOfType(DataSource.class);

        StaticContext.setDbExecutor(new MySqlExecutor(dataSourceMap));

    }

    private static void start() throws Exception {
        if(server == null) {
            server = new Server(8080);
        }
        server.bind();
        server.service();
    }

    public static synchronized void shutdown() throws Exception{
        if(server != null) {
            server.shutdown();
            server = null;
        }
        StaticContext.dispose();

        resetClassLoaderMethod.invoke(null);
    }
}
