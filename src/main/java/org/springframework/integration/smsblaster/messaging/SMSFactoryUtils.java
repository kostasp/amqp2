package org.springframework.integration.smsblaster.messaging;

import com.google.common.io.CharStreams;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMSFactoryUtils {
    public static SMSFactory getModule(String moduleName, List<String> args) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.springframework.integration.smsblaster.modules." + moduleName);
        } catch (ClassNotFoundException e) {
            return null;
        }
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors == null) {
            System.err.printf("Module %s does not have public constructors%n", moduleName);
            return null;
        }



        if (constructors.length != 1) {
            System.err.printf("Module %s has more than one constructors%n", moduleName);
            return null;
        }
        Constructor<?> constructor = constructors[0];
        Class<?>[] parameters = constructor.getParameterTypes();
        if (parameters.length != 1) {
            System.err.printf("Module %s does not have a suitable constructor(List<String>)%n", moduleName);
            return null;
        }
        SMSFactory factory = null;
        try {
            factory = (SMSFactory) constructor.newInstance(args);
        } catch (Exception e) {
            System.out.printf("Module %s: Failed to instantiate: %s%n", moduleName, e.getMessage());
            return null;
        }
        return factory;
    }

    public static Map<String, CompiledTemplate> createMVELTemplates(List<String> args) {
        Map<String, CompiledTemplate> templates = new HashMap<String, CompiledTemplate>();
        if (args == null) {
            return templates;
        }
        for (String arg : args) {
            int pos = arg.indexOf('=');
            if (pos > 0) {
                String param = arg.substring(0, pos);
                String tmpl = arg.substring(pos + 1);
                if (tmpl.charAt(0) == '#') {
                    FileReader fin = null;
                    File fname = null;
                    try {
                        fname = new File(tmpl.substring(1));
                        fin = new FileReader(fname);
                        tmpl = CharStreams.toString(new FileReader(new File(tmpl.substring(1))));
                    } catch (IOException e) {
                        System.err.printf("Failed to create MVEL template for file '%s': %s%n",
                                fname.getAbsoluteFile(), e.getMessage());
                    } finally {
                        if (fin != null) {
                            try {
                                fin.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }
                try {
                    templates.put(param, TemplateCompiler.compileTemplate(tmpl));
                } catch (Exception e) {
                    System.err.printf("Failed to create MVEL template for '%s': %s%n", tmpl, e.getMessage());
                }
            }
        }

        return templates;
    }
}
