package cz.josefczech.springboottestjms.jmscaptor;

import cz.josefczech.springboottestjms.config.MultipleListenerContainerJmsConfiguration;
import cz.josefczech.springboottestjms.config.SingleListenerContainerJmsConfiguration;
import cz.josefczech.springboottestjms.dto.TestMessage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ImportJmsCaptorTest {

    @Nested
    @Import(SingleListenerContainerJmsConfiguration.class)
    class SingleListenerContainer {

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
    }

    @Nested
    @Import(MultipleListenerContainerJmsConfiguration.class)
    class MultipleListenerContainers {

        @ImportJmsCaptor(destination = "test_topic", containerFactory = "topicListenerContainerFactory")
        private JmsCaptor<TestMessage> topicJmsCaptor;

        @ImportJmsCaptor(destination = "test_topic", containerFactory = "topicListenerContainerFactory")
        private JmsCaptor<TestMessage> topicJmsCaptor2;

        @ImportJmsCaptor(destination = "test_topic", containerFactory = "queueListenerContainerFactory")
        private JmsCaptor<TestMessage> queueJmsCaptor;

        @Autowired
        @Qualifier("topicJmsTemplate")
        private JmsTemplate topicJmsTemplate;

        @Autowired
        @Qualifier("queueJmsTemplate")
        private JmsTemplate queueJmsTemplate;

        @Test
        public void publicAndCaptureMessage_withTopic() {
            topicJmsTemplate.convertAndSend("test_topic", new TestMessage("foobar"));

            await().atMost(1, TimeUnit.SECONDS).until(() ->
                    topicJmsCaptor.getMessages().size() != 0
                            && topicJmsCaptor2.getMessages().size() != 0
            );

            assertEquals("foobar", topicJmsCaptor.getMessages().get(0).getValue());
            assertEquals("foobar", topicJmsCaptor2.getMessages().get(0).getValue());
            assertEquals(0, queueJmsCaptor.getMessages().size());
        }

        @Test
        public void publicAndCaptureMessage_withQueue() {
            queueJmsTemplate.convertAndSend("test_topic", new TestMessage("foobar"));

            await().atMost(1, TimeUnit.SECONDS).until(() ->
                    queueJmsCaptor.getMessages().size() != 0
            );

            assertEquals("foobar", queueJmsCaptor.getMessages().get(0).getValue());
            assertEquals(0, topicJmsCaptor.getMessages().size());
            assertEquals(0, topicJmsCaptor2.getMessages().size());
        }
    }
}
