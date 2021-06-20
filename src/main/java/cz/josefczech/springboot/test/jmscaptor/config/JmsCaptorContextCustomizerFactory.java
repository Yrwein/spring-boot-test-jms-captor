package cz.josefczech.springboot.test.jmscaptor.config;

import cz.josefczech.springboot.test.jmscaptor.ImportJmsCaptor;
import cz.josefczech.springboot.test.jmscaptor.exception.OnlyOneImportJmsCaptorAllowedException;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The customizer factory creates instances of {@link JmsCaptorContextCustomizer}
 * if any field on given test class contains {@link ImportJmsCaptor} annotation.
 */
class JmsCaptorContextCustomizerFactory implements ContextCustomizerFactory {

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
                throw new OnlyOneImportJmsCaptorAllowedException(
                        "Field " + testClass.getSimpleName() + "#" + field.getName()
                                + " has too many ImportJmsCaptor annotations; only one is allowed: " + annotations.size());
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
