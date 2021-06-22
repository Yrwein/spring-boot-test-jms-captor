package cz.josefczech.springboot.test.jmscaptor.config;

import cz.josefczech.springboot.test.jmscaptor.JmsCaptor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * JmsCaptorTestExecutionListener is responsible for setting JmsCaptor fields on test instances
 * and cleaning up the captor before every run.
 */
class JmsCaptorTestExecutionListener extends AbstractTestExecutionListener {

    /**
     * Start previously stopped listeners if context is cached and reused.
     * @param testContext Spring test context
     */
    @Override
    public void beforeTestClass(TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        try {
            JmsListenerEndpointRegistry listenerRegistry = applicationContext.getBean(JmsListenerEndpointRegistry.class);
            listenerRegistry.start();
        } catch (NoSuchBeanDefinitionException e) {
            // ignore (this test does not need JMS)
        }
    }

    /**
     * Stop previously started listeners (so they do not affect other tests).
     * @param testContext Spring test context
     */
    @Override
    public void afterTestClass(TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        try {
            JmsListenerEndpointRegistry listenerRegistry = applicationContext.getBean(JmsListenerEndpointRegistry.class);
            listenerRegistry.stop();
        } catch (NoSuchBeanDefinitionException e) {
            // ignore (this test does not need JMS)
        }
    }

    /**
     * The method prepares test instance by setting JmsCaptor to all fields.
     * @param testContext Spring test context
     */
    @Override
    public void prepareTestInstance(TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        if (!applicationContext.containsBean(JmsCaptorRegistry.class.getName())) {
            return;
        }
        JmsCaptorRegistry jmsCaptorRegistry = applicationContext.getBean(JmsCaptorRegistry.class);
        Map<Field, JmsCaptor<?>> fieldToAnnotationMap = jmsCaptorRegistry.getFieldToJmsCaptorMap();

        for (Map.Entry<Field, JmsCaptor<?>> entry : fieldToAnnotationMap.entrySet()) {
            Field field = entry.getKey();
            JmsCaptor<?> jmsCaptor = entry.getValue();
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, testContext.getTestInstance(), jmsCaptor);
        }
    }

    /**
     * JmsCaptors in test instance fields are cleared of messages before every test run.
     * @param testContext Spring test context
     */
    @Override
    public void beforeTestMethod(TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        if (!applicationContext.containsBean(JmsCaptorRegistry.class.getName())) {
            return;
        }
        JmsCaptorRegistry jmsCaptorRegistry = applicationContext.getBean(JmsCaptorRegistry.class);
        Map<Field, JmsCaptor<?>> fieldToAnnotationMap = jmsCaptorRegistry.getFieldToJmsCaptorMap();

        for (Map.Entry<Field, JmsCaptor<?>> entry : fieldToAnnotationMap.entrySet()) {
            JmsCaptor<?> jmsCaptor = entry.getValue();
            jmsCaptor.clearMessages();
        }
    }
}
