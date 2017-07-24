//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hjzgg.stateless.common.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

public class Resources {
    private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();
    private static Charset charset;

    Resources() {
    }

    public static ClassLoader getDefaultClassLoader() {
        return classLoaderWrapper.defaultClassLoader;
    }

    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        classLoaderWrapper.defaultClassLoader = defaultClassLoader;
    }

    public static URL getResourceURL(String resource) throws IOException {
        return getResourceURL((ClassLoader)null, resource);
    }

    public static URL getResourceURL(ClassLoader loader, String resource) throws IOException {
        URL url = classLoaderWrapper.getResourceAsURL(resource, loader);
        if(url == null) {
            throw new IOException("Could not find resource " + resource);
        } else {
            return url;
        }
    }

    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream((ClassLoader)null, resource);
    }

    public static InputStream getResourceAsStream(Class<?> clazz, String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, clazz);
        if(in == null) {
            throw new IOException("Could not find resource " + resource);
        } else {
            return in;
        }
    }

    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
        if(in == null) {
            throw new IOException("Could not find resource " + resource);
        } else {
            return in;
        }
    }

    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(resource);
        props.load(in);
        in.close();
        return props;
    }

    public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(loader, resource);
        props.load(in);
        in.close();
        return props;
    }

    public static Reader getResourceAsReader(String resource) throws IOException {
        InputStreamReader reader;
        if(charset == null) {
            reader = new InputStreamReader(getResourceAsStream(resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(resource), charset);
        }

        return reader;
    }

    public static Reader getResourceAsReader(ClassLoader loader, String resource) throws IOException {
        InputStreamReader reader;
        if(charset == null) {
            reader = new InputStreamReader(getResourceAsStream(loader, resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(loader, resource), charset);
        }

        return reader;
    }

    public static File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceURL(resource).getFile());
    }

    public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
        return new File(getResourceURL(loader, resource).getFile());
    }

    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }

    public static Reader getUrlAsReader(String urlString) throws IOException {
        InputStreamReader reader;
        if(charset == null) {
            reader = new InputStreamReader(getUrlAsStream(urlString));
        } else {
            reader = new InputStreamReader(getUrlAsStream(urlString), charset);
        }

        return reader;
    }

    public static Properties getUrlAsProperties(String urlString) throws IOException {
        Properties props = new Properties();
        InputStream in = getUrlAsStream(urlString);
        props.load(in);
        in.close();
        return props;
    }

    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(className);
    }

    public static Charset getCharset() {
        return charset;
    }

    public static void setCharset(Charset charset) {
        charset = charset;
    }


//############################ 华丽分割线 通过 spring 工具类 #################################
    public static File getFileWithResourceUtils(String resource) throws FileNotFoundException {
        return ResourceUtils.getFile(resource);
    }

    public Resource getResourceWithPathMatchingResourcePatternResolver(String resource) {
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        return pathMatchingResourcePatternResolver.getResource(resource);
    }

    public Resource[] getResourcesWithPathMatchingResourcePatternResolver(String resource) throws IOException {
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        return pathMatchingResourcePatternResolver.getResources(resource);
    }

    public InputStream getInputStreamWithClassPathResource(String resource, Class<?> clazz) throws IOException {
        if (clazz != null) {
            return new ClassPathResource(resource, clazz).getInputStream();
        } else {
            return new ClassPathResource(resource).getInputStream();
        }
    }

    public static void main(String[] args) {
        try {
            PathMatchingResourcePatternResolver p = new PathMatchingResourcePatternResolver();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getResources("classpath:com/**/Resources.class")[0].getInputStream()));
            String str = null;
            while ((str = br.readLine()) != null) {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClassLoaderWrapper {
    ClassLoader defaultClassLoader;
    ClassLoader systemClassLoader;

    ClassLoaderWrapper() {
        try {
            this.systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException var2) {
            ;
        }

    }

    public URL getResourceAsURL(String resource) {
        return this.getResourceAsURL(resource, this.getClassLoaders((ClassLoader)null));
    }

    public URL getResourceAsURL(String resource, ClassLoader classLoader) {
        return this.getResourceAsURL(resource, this.getClassLoaders(classLoader));
    }

    public InputStream getResourceAsStream(String resource) {
        return this.getResourceAsStream(resource, this.getClassLoaders((ClassLoader)null));
    }

    public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
        return this.getResourceAsStream(resource, this.getClassLoaders(classLoader));
    }

    public Class<?> classForName(String name) throws ClassNotFoundException {
        return this.classForName(name, this.getClassLoaders((ClassLoader)null));
    }

    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return this.classForName(name, this.getClassLoaders(classLoader));
    }

    InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
        ClassLoader[] arr$ = classLoader;
        int len$ = classLoader.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            ClassLoader cl = arr$[i$];
            if(null != cl) {
                InputStream returnValue = cl.getResourceAsStream(resource);
                if(null == returnValue) {
                    returnValue = cl.getResourceAsStream("/" + resource);
                }

                if(null != returnValue) {
                    return returnValue;
                }
            }
        }

        return null;
    }

    URL getResourceAsURL(String resource, ClassLoader[] classLoader) {
        ClassLoader[] arr$ = classLoader;
        int len$ = classLoader.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            ClassLoader cl = arr$[i$];
            if(null != cl) {
                URL url = cl.getResource(resource);
                if(null == url) {
                    url = cl.getResource("/" + resource);
                }

                if(null != url) {
                    return url;
                }
            }
        }

        return null;
    }

    Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {
        ClassLoader[] arr$ = classLoader;
        int len$ = classLoader.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            ClassLoader cl = arr$[i$];
            if(null != cl) {
                try {
                    Class<?> c = Class.forName(name, true, cl);
                    if(null != c) {
                        return c;
                    }
                } catch (ClassNotFoundException var8) {
                    ;
                }
            }
        }

        throw new ClassNotFoundException("Cannot find class: " + name);
    }

    ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[]{classLoader, this.defaultClassLoader, Thread.currentThread().getContextClassLoader(), this.getClass().getClassLoader(), this.systemClassLoader};
    }

    public InputStream getResourceAsStream(String resource, Class<?> clazz) {
        return clazz.getResourceAsStream(resource);
    }
}
