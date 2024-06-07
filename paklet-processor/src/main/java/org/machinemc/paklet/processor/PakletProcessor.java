package org.machinemc.paklet.processor;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.serialization.DefaultSerializer;
import org.machinemc.paklet.serialization.rule.DefaultSerializationRule;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Annotation processor for Paklet that stores information about each catalogue in
 * its own package and file, and packet data used by the Paklet gradle plugin.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class PakletProcessor extends AbstractProcessor {

    private boolean processed = false;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
                Packet.class.getName(),
                DefaultSerializer.class.getName(),
                DefaultSerializationRule.class.getName()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processed) return true;

        Map<TypeMirror, CatalogueData> data = new HashMap<>();

        List<TypeElement> packets = PakletProcessor.collectValidTypes(roundEnv, Packet.class);
        for (TypeElement packet : packets) {
            Packet annotation = packet.getAnnotation(Packet.class);
            TypeMirror catalogue = ProcessingUtils.mirror(annotation::catalogue);
            data.computeIfAbsent(catalogue, c -> new CatalogueData()).packets.add(ProcessingUtils.getInternalName(packet));
        }

        List<TypeElement> defaultSerializers = PakletProcessor.collectValidTypes(roundEnv, DefaultSerializer.class);
        for (TypeElement serializer : defaultSerializers) {
            DefaultSerializer annotation = serializer.getAnnotation(DefaultSerializer.class);
            TypeMirror catalogue = ProcessingUtils.mirror(annotation::value);
            data.computeIfAbsent(catalogue, c -> new CatalogueData()).serializers.add(ProcessingUtils.getInternalName(serializer));
        }

        List<TypeElement> defaultRules = PakletProcessor.collectValidTypes(roundEnv, DefaultSerializationRule.class);
        for (TypeElement rule : defaultRules) {
            DefaultSerializationRule annotation = rule.getAnnotation(DefaultSerializationRule.class);
            TypeMirror catalogue = ProcessingUtils.mirror(annotation::value);
            data.computeIfAbsent(catalogue, c -> new CatalogueData()).rules.add(ProcessingUtils.getInternalName(rule));
        }

        catalogues: {
            for (TypeMirror catalogue : data.keySet()) {
                CatalogueData catalogueData = data.get(catalogue);
                TypeElement element = (TypeElement) processingEnv.getTypeUtils().asElement(catalogue);
                PackageElement pkg = processingEnv.getElementUtils().getPackageOf(element);
                try {
                    String fileName = element.getQualifiedName().toString().replace(pkg.getQualifiedName() + ".", "");
                    FileObject file = processingEnv.getFiler().createResource(
                            StandardLocation.CLASS_OUTPUT,
                            pkg.getQualifiedName(),
                            fileName + "_catalogue.json"
                    );
                    try (Writer writer = new BufferedWriter(file.openWriter())) {
                        new Gson().toJson(catalogueData.asJSON(), writer);
                    }
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }

        packetData /* for gradle plugin */ : {
            try {
                FileObject file = processingEnv.getFiler().createResource(
                        StandardLocation.CLASS_OUTPUT,
                        "",
                        "paklet-packet-data.json"
                );
                try (Writer writer = new BufferedWriter(file.openWriter())) {

                    JsonObject json = new JsonObject();
                    JsonArray packetsArray = new JsonArray();
                    packets.forEach(element -> packetsArray.add(ProcessingUtils.getInternalName(element)));
                    json.add("packets", packetsArray);

                    new Gson().toJson(json, writer);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        processed = true;
        return true;
    }

    /**
     * Represents data of a catalogue.
     */
    private static class CatalogueData {

        JsonArray packets = new JsonArray();
        JsonArray serializers = new JsonArray();
        JsonArray rules = new JsonArray();

        JsonObject asJSON() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("packets", packets);
            jsonObject.add("serializers", serializers);
            jsonObject.add("rules", rules);
            return jsonObject;
        }

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

    /**
     * Returns all type elements that passed {@link #isValidType(Element)} check
     * and are annotated with given annotation.
     *
     * @param roundEnv round environment
     * @param annotation annotation type
     * @return list of type elements
     */
    private static List<TypeElement> collectValidTypes(RoundEnvironment roundEnv, Class<? extends Annotation> annotation) {
        return roundEnv.getElementsAnnotatedWith(annotation).stream()
                .filter(PakletProcessor::isValidType)
                .filter(element -> element instanceof TypeElement)
                .map(TypeElement.class::cast)
                .toList();
    }

}
