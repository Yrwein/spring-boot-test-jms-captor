# JmsCaptor

JmsCaptor is a small utility to capture published JMS messages in Spring Boot integration tests.

Please note: This is my weekend project for learning more about Spring Boot internals.
It's functional, but I do not use it at any commercial project.

## Usage

```java
@SpringBootTest
public class JmsCaptorTest {

    // Use ImportJmsCaptor on field to inject JmsCaptor object
    // (you can also customize containerFactory).
    @ImportJmsCaptor(destination = "test_queue")
    private JmsCaptor<TestMessage> jmsCaptor;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void publicAndCaptureMessage() {
        jmsTemplate.convertAndSend("test_queue", new TestMessage("foobar"));

        // JmsCaptor object will receive published messages as soon as its listener will consume them from JMS.
        await().atMost(1, TimeUnit.SECONDS).until(() ->
                jmsCaptor.getMessages().size() != 0
        );

        assertEquals("foobar", jmsCaptor.getMessages().get(0).getValue());
    }
}

```
