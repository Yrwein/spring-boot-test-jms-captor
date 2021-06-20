package cz.josefczech.springboottestjms.jmscaptor.testinstanceconfig;

import cz.josefczech.springboottestjms.jmscaptor.JmsCaptor;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * JmsCaptorRegistry is Spring bean holding fields and their JmsCaptors.
 * The bean is created by {@link JmsCaptorContextCustomizer} for {@link JmsCaptorTestExecutionListener}.
 */
class JmsCaptorRegistry {

    private final Map<Field, JmsCaptor<?>> fieldToJmsCaptorMap;

    public JmsCaptorRegistry(Map<Field, JmsCaptor<?>> fieldToJmsCaptorMap) {
        this.fieldToJmsCaptorMap = fieldToJmsCaptorMap;
    }

    public Map<Field, JmsCaptor<?>> getFieldToJmsCaptorMap() {
        return fieldToJmsCaptorMap;
    }
}
