package cz.josefczech.springboottestjms.jmscaptor;

import cz.josefczech.springboottestjms.jmscaptor.exception.IllegalImportJmsCaptorCount;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JmsCaptorContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(
            @NonNull Class<?> testClass,
            @NonNull List<ContextConfigurationAttributes> configAttributes
    ) {
        final Map<Field, ImportJmsCaptorData> fieldToAnnotationDataMap = new HashMap<>();
        boolean foundJmsCaptors = false;

        Field[] fields = testClass.getDeclaredFields();
        for (Field field : fields) {
            List<ImportJmsCaptorData> annotations = findImportJmsCaptorAnnotationData(field);

            if (annotations.size() >= 2) {
                throw new IllegalImportJmsCaptorCount(
                        "Field " + testClass.getSimpleName() + "#" + field.getName()
                                + " has too many ImportJmsCaptor annotations; only one allowed: " + annotations.size());
            }

            if (annotations.size() == 1) {
                foundJmsCaptors = true;
                fieldToAnnotationDataMap.put(field, annotations.get(0));
            }
        }

        return foundJmsCaptors ? new JmsCaptorContextCustomizer(fieldToAnnotationDataMap) : null;
    }

    private List<ImportJmsCaptorData> findImportJmsCaptorAnnotationData(Field field) {
        return MergedAnnotations
                .from(field, MergedAnnotations.SearchStrategy.SUPERCLASS)
                .stream(ImportJmsCaptor.class)
                .map(MergedAnnotation::synthesize)
                .map(it -> new ImportJmsCaptorData(it.containerFactory(), it.destination()))
                .collect(Collectors.toList());
    }
}
