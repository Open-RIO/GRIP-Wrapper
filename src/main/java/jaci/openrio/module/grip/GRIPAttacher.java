package jaci.openrio.module.grip;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * The GRIP Attacher is a little class that will add the GRIP Jar File into the classpath of your FRC program.
 * As opposed to running over Runtime.getRuntime().exec(), this class instead adds and launches the GRIP jar from the current
 * JVM context. Put simply, this means less JVM overhead (one instance as opposed to two)
 *
 * @author Jaci
 */
public class GRIPAttacher {

    /**
     * Start the GRIP Computer Vision Engine. This will add the GRIP Jar to the classpath and begin loading it in
     * a new Thread. Call this from your main Robot Program. If you are using the GRIP Wrapper Toast Module, this is
     * called for you.
     * @throws GRIPLoadException Thrown if an error is encountered in the loading of GRIP, including missing files,
     *                           and missing class files.
     */
    public static void startGRIP() throws GRIPLoadException {
        File[] files = new File(".").listFiles((dir, name) -> {
            return name.endsWith(".jar") && name.contains("core-");     // Works across versions
        });

        if (files.length == 0)
            throw new GRIPLoadException("No GRIP Core Jar Files were found!");

        File target = files[files.length - 1];
        try {
            addToClasspath(target.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new GRIPLoadException("Malformed GRIP Jar URL: " + e.getMessage());
        }

        try {
            Class clazz = Class.forName("edu.wpi.grip.core.Main");
            Method m = clazz.getDeclaredMethod("main", String[].class);
            Thread t = new Thread(() -> {
                try {
                    m.invoke(new String[0]);
                } catch (IllegalAccessException | InvocationTargetException e) { }
            });
            t.setName("GRIP");
            t.start();
        } catch (ClassNotFoundException e) {
            throw new GRIPLoadException("Corrupt GRIP Core Jar: No Main Class");
        } catch (NoSuchMethodException e) {
            throw new GRIPLoadException("Corrupt GRIP Core Jar: Main Class does not have a Main Method");
        }
    }

    private static void addToClasspath(URL url) throws GRIPLoadException {
        Class sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke((URLClassLoader) ClassLoader.getSystemClassLoader(), url);
        } catch (Throwable t) {
            throw new GRIPLoadException("Could not add GRIP to classpath: " + t.getMessage());
        }
    }

    public static class GRIPLoadException extends Exception {
        public GRIPLoadException(String str) {
            super(str);
        }
    }

}
