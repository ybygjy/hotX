package com.github.airst.classLoader;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Description: HotXClassLoader
 * User: qige
 * Date: 15/11/30
 * Time: 下午11:01
 */
public class HotXClassLoader extends URLClassLoader {

    public HotXClassLoader(URL[] path, ClassLoader loader) {
        super(path, loader);
    }

    @Override
    protected Class<?> findClass(final String var1) throws ClassNotFoundException {
        try {
            return getParent().loadClass(var1);
        } catch (ClassNotFoundException e) {
            return super.findClass(var1);
        }
    }

    public static synchronized ClassLoader get(String path, ClassLoader loader) throws Exception {
        HotXClassLoader hotXClassLoader = new HotXClassLoader(new URL[]{new URL("file:" + path)}, loader);
        try {
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(hotXClassLoader, new URL("file:" + path.replace("hotX-core.jar", "hotX-lib.jar")));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return hotXClassLoader;
    }

}
