package com.gameservergroup.gsgcore.plugin.processor;

import org.bukkit.plugin.PluginLoadOrder;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Used inspiration from Lucko's helper repo for plugin yml processing
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.gameservergroup.gsgcore.plugin.processor.GSGPlugin", "com.gameservergroup.gsgcore.plugin.processor.PluginDependency"})
public class PluginYMLProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GSGPlugin.class);
        if (elementsAnnotatedWith.isEmpty() || elementsAnnotatedWith.size() > 1) {
            return false;
        }
        Element element = elementsAnnotatedWith.iterator().next();

        if (element instanceof TypeElement) {
            TypeElement type = ((TypeElement) element);
            Map<String, Object> data = new LinkedHashMap<>();
            GSGPlugin annotation = type.getAnnotation(GSGPlugin.class);

            data.put("name", annotation.name());
            data.put("version", annotation.version());
            data.put("main", type.getQualifiedName().toString());
            data.put("description", annotation.description());
            data.put("website", annotation.website());

            if (annotation.load() != PluginLoadOrder.POSTWORLD) {
                data.put("load", annotation.load().name());
            }

            if (annotation.authors().length == 1) {
                data.put("author", annotation.authors()[0]);
            } else if (annotation.authors().length > 1) {
                data.put("authors", new ArrayList<>(Collections.singletonList(annotation.authors())));
            }

            Dependency[] depends = annotation.depends();
            ArrayList<String> hard = new ArrayList<>(), soft = new ArrayList<>();

            for (Dependency depend : depends) {
                if (depend.softDependency()) {
                    soft.add(depend.pluginName());
                } else {
                    hard.add(depend.pluginName());
                }
            }

            if (!hard.isEmpty()) {
                data.put("depend", hard);
            }

            if (!soft.isEmpty()) {
                data.put("softdepend", soft);
            }

            try {
                try (Writer writer = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml").openWriter(); BufferedWriter bw = new BufferedWriter(writer)) {
                    new Yaml().dump(data, bw);
                    bw.flush();
                }
                return true;
            } catch (IOException e) {
                throw new RuntimeException("Cannot serialize plugin descriptor: " + e.getMessage(), e);
            }
        }
        return false;
    }
}
