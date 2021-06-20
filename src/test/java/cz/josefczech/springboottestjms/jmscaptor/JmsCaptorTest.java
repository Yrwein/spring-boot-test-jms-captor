package cz.josefczech.springboottestjms.jmscaptor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JmsCaptorTest {

    @ImportJmsCaptor(destination = "test_queue")
    private JmsCaptor<TestMessage> jmsCaptor;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void publicAndCaptureMessage() {
        jmsTemplate.convertAndSend("test_queue", new TestMessage("foobar"));

        await().atMost(1, TimeUnit.SECONDS).until(() ->
                jmsCaptor.getMessages().size() != 0
        );

        assertEquals("foobar", jmsCaptor.getMessages().get(0).getValue());
    }

    private static class TestMessage implements Serializable {

        private final String value;

        TestMessage(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
