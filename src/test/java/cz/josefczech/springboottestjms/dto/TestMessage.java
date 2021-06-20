package cz.josefczech.springboottestjms.dto;

import java.io.Serializable;

public class TestMessage implements Serializable {

    private final String value;

    public TestMessage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
