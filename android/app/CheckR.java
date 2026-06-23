import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class CheckR {
    public static void main(String[] args) throws Exception {
        File rJar = new File("build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/release/processReleaseResources/R.jar");
        URLClassLoader classLoader = new URLClassLoader(new URL[]{rJar.toURI().toURL()});
        Class<?> layoutClass = classLoader.loadClass("com.lumina.app.R$layout");
        System.out.println("Layout fields:");
        for (java.lang.reflect.Field f : layoutClass.getFields()) {
            System.out.println(f.getName());
        }
        Class<?> idClass = classLoader.loadClass("com.lumina.app.R$id");
        System.out.println("Id fields:");
        for (java.lang.reflect.Field f : idClass.getFields()) {
            System.out.println(f.getName());
        }
    }
}
