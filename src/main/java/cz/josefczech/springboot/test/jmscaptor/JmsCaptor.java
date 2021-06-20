package cz.josefczech.springboot.test.jmscaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Object holding messages from queue specified by {@link ImportJmsCaptor}.
 *
 * @see ImportJmsCaptor See ImportJmsCaptor for usage.
 *
 * @param <T> JMS message type.
 */
public class JmsCaptor<T> {

    private final List<T> messages = Collections.synchronizedList(new ArrayList<>());

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
