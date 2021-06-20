package cz.josefczech.springboottestjms.jmscaptor;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class JmsCaptorTestExecutionListener extends AbstractTestExecutionListener {

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
