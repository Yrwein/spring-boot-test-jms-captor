package cz.josefczech.springboot.test.jmscaptor.config;

import cz.josefczech.springboot.test.jmscaptor.JmsCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.jms.JMSException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Context customizer is responsible for registering all bean definitions:
 *
 * <ul>
 *     <li>{@link JmsCaptorListenerConfigurer} so JmsCaptors can listen on queues.</li>
 *     <li>{@link JmsCaptorRegistry} for {@link JmsCaptorTestExecutionListener}
 *     (so JmsCaptors can set to test instance fields).</li>
 * </ul>
 */
class JmsCaptorContextCustomizer implements ContextCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(JmsCaptorContextCustomizer.class);

    private final Map<Field, ImportJmsCaptorData> fieldToAnnotationDataMap;

    public JmsCaptorContextCustomizer(Map<Field, ImportJmsCaptorData> fieldToAnnotationDataMap) {
        this.fieldToAnnotationDataMap = fieldToAnnotationDataMap;
    }

    @Override
    public void customizeContext(
            @NonNull ConfigurableApplicationContext context,
            @NonNull MergedContextConfiguration mergedConfig
    ) {
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context;
        Assert.isInstanceOf(BeanDefinitionRegistry.class, context);

        Map<Field, JmsCaptor<Object>> fieldToJmsCaptorMap = createJmsCaptorForFields();
        registerJmsCaptorRegistry(beanDefinitionRegistry, fieldToJmsCaptorMap);

        for (Map.Entry<Field, ImportJmsCaptorData> entry : fieldToAnnotationDataMap.entrySet()) {
            Field field = entry.getKey();
            ImportJmsCaptorData annotationData = entry.getValue();

            JmsCaptor<Object> jmsCaptor = fieldToJmsCaptorMap.get(field);
            JmsListenerEndpoint endpoint = createListenerEndpoint(annotationData.getDestination(), jmsCaptor, field);

            registerJmsCaptorListenerConfigurer(beanDefinitionRegistry, field, annotationData, endpoint);
        }
    }

    private Map<Field, JmsCaptor<Object>> createJmsCaptorForFields() {
        return fieldToAnnotationDataMap
                .keySet()
                .stream()
                .collect(Collectors.toMap(
                        it -> it,
                        it -> new JmsCaptor<>()
                ));
    }

    private void registerJmsCaptorRegistry(
            BeanDefinitionRegistry beanDefinitionRegistry,
            Map<Field, JmsCaptor<Object>> fieldToJmsCaptorMap
    ) {
        beanDefinitionRegistry.registerBeanDefinition(
                JmsCaptorRegistry.class.getName(),
                BeanDefinitionBuilder
                        .genericBeanDefinition(JmsCaptorRegistry.class)
                        .addConstructorArgValue(fieldToJmsCaptorMap)
                        .getBeanDefinition()
        );
    }

    private JmsListenerEndpoint createListenerEndpoint(String destination, JmsCaptor<Object> jmsCaptor, Field field) {
        @SuppressWarnings("unchecked")
        Class<Object> capturedClass = (Class<Object>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId("jmsCaptorListener" + field.getName());
        endpoint.setDestination(destination);
        endpoint.setMessageListener(
                message -> {
                    try {
                        jmsCaptor.addMessage(message.getBody(capturedClass));
                    } catch (JMSException e) {
                        logger.error("Error while consuming message from {}", destination, e);
                    }
                }

        );
        return endpoint;
    }

    private void registerJmsCaptorListenerConfigurer(
            BeanDefinitionRegistry beanDefinitionRegistry,
            Field field,
            ImportJmsCaptorData annotationData,
            JmsListenerEndpoint endpoint
    ) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(JmsCaptorListenerConfigurer.class)
                .addConstructorArgValue(endpoint);

        if (!ObjectUtils.isEmpty(annotationData.getContainerFactory())) {
            beanDefinitionBuilder.addConstructorArgReference(annotationData.getContainerFactory());
        }

        beanDefinitionRegistry.registerBeanDefinition(
                "jmsCaptorListenerConfigurer" + field.getName(),
                beanDefinitionBuilder.getBeanDefinition()
        );
    }
}
