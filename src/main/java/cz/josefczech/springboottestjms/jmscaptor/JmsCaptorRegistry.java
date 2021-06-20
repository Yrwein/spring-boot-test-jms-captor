package cz.josefczech.springboottestjms.jmscaptor;

import java.lang.reflect.Field;
import java.util.Map;

public class JmsCaptorRegistry {

    private final Map<Field, JmsCaptor<?>> fieldToJmsCaptorMap;

    public JmsCaptorRegistry(Map<Field, JmsCaptor<?>> fieldToJmsCaptorMap) {
        this.fieldToJmsCaptorMap = fieldToJmsCaptorMap;
    }

    public Map<Field, JmsCaptor<?>> getFieldToJmsCaptorMap() {
        return fieldToJmsCaptorMap;
    }
}
