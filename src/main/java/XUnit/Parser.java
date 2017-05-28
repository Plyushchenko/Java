package XUnit;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** Command line args parser*/
class Parser {

    @NotNull private static final String CLASS_EXTENSION = ".class";
    private static final int CLASS_EXTENSION_LENGTH = CLASS_EXTENSION.length();
    @NotNull private final String[] args;

    Parser(@NotNull String[] args) {
        this.args = args;
    }

    @NotNull
    List<Class> buildListOfClassesToTest() throws IOException, ClassNotFoundException {
        List<Class> classesToTest = new ArrayList<>();
        for (String pathAsString : args) {
            Enumeration<JarEntry> entries = new JarFile(pathAsString).entries();
            URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{
                    buildUrlFromPathAsString(pathAsString)
            });
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                String jarEntryName = jarEntry.getName();
                if (!jarEntryName.endsWith(CLASS_EXTENSION)) {
                    continue;
                }
                String className = buildClassNameFromJarEntryName(jarEntryName);
                classesToTest.add(urlClassLoader.loadClass(className));
            }
        }
        return classesToTest;
    }

    @NotNull
    private String buildClassNameFromJarEntryName(@NotNull String jarEntryName) {
        return jarEntryName
                .substring(0, jarEntryName.length() - CLASS_EXTENSION_LENGTH)
                .replace('/', '.');
    }

    @NotNull
    private URL buildUrlFromPathAsString(@NotNull String pathAsString)
            throws MalformedURLException {
        return new URL("jar:file:" + pathAsString + "!/");
    }

}
