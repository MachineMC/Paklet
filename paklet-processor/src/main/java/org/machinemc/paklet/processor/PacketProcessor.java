package org.machinemc.paklet.processor;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.serializers.DefaultSerializer;
import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * Annotation processor for Paklet.
 * <p>
 * Saves information about defined packets, default serializers and their supported types.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class PacketProcessor extends AbstractProcessor {

    private FileObject file;

    private final JsonObject json = new JsonObject();

    private final JsonArray defaultSerializers = new JsonArray();
    private final JsonArray packets = new JsonArray();

    {
        json.add("defaultSerializers", defaultSerializers);
        json.add("packets", packets);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
                Packet.class.getName(),
                DefaultSerializer.class.getName()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        roundEnv.getElementsAnnotatedWith(DefaultSerializer.class).stream()
                .filter(PacketProcessor::isValidType)
                .forEach(element -> defaultSerializers.add(ProcessingUtils.getInternalName((TypeElement) element)));

        roundEnv.getElementsAnnotatedWith(Packet.class).stream()
                .filter(PacketProcessor::isValidType)
                .filter(element -> element instanceof TypeElement)
                .forEach(element -> packets.add(ProcessingUtils.getInternalName((TypeElement) element)));

        if (file != null) return true;

        try {
            file = processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    "paklet-packet-data.json"
            );
            try (Writer writer = new BufferedWriter(file.openWriter())) {
                new Gson().toJson(json, writer);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return true;
    }

    /**
     * Checks whether provided element is a class that is not interface.
     *
     * @param element element to check
     * @return whether the element is a class
     */
    private static boolean isValidType(Element element) {
        if (!(element instanceof TypeElement typeElement)) return false;
        ElementKind kind = typeElement.getKind();
        if (kind.isInterface()) return false;
        return kind.isClass();
    }

}
