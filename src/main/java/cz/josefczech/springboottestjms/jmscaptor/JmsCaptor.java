package cz.josefczech.springboottestjms.jmscaptor;

import java.util.ArrayList;
import java.util.List;

public class JmsCaptor<T> {

    private final List<T> messages = new ArrayList<>();

    public List<T> getMessages() {
        return messages;
    }

    public void clearMessages() {
        messages.clear();
    }

    public void addMessage(T body) {
        messages.add(body);
    }
}
