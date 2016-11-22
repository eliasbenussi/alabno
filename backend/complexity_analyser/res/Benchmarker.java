import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Benchmarker {
    public static void main(String[] args) throws Exception {
        ClassPool objClassPool = ClassPool.getDefault();
        CtClass objCtClass = objClassPool.get(args[0]);
        // Adds a static field to keep track what we're benching
        CtField f = new CtField(CtClass.intType, "counter", objCtClass);
        f.setModifiers(Modifier.STATIC);
        objCtClass.addField(f, "0");
        CtMethod[] methods = objCtClass.getDeclaredMethods();
        Method main = null;
        for (CtMethod m : methods) {
            if (!m.toString().contains("public")) {
                String name = String.format("%s", m.getName());
                // Injects prints before and after each method to get the execution time
                String before = String.format("{ System.out.println(\"NO \" + counter + \" BEFORE %s => \" + System.nanoTime()); }", name);
                String after = String.format("{ System.out.println(\"NO \" + counter + \" AFTER %s => \" + System.nanoTime()); counter++;}", name);
                m.insertBefore(before);
                m.insertAfter(after);
            }
        }
        Object objClass = objCtClass.toClass().newInstance();
        for (Method m : objClass.getClass().getDeclaredMethods()) {
            if (m.getName().contains("main")) {
                main = m;
                break;
            }
        }
        // Finds and executes main
        main.invoke(objClass.getClass(), new String[1]);
    }
}