package cz.josefczech.springboot.test.jmscaptor.config;

import cz.josefczech.springboot.test.jmscaptor.JmsCaptor;
import org.springframework.context.ApplicationContext;
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
