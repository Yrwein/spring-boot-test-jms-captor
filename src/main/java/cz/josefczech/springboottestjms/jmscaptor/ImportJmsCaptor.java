package cz.josefczech.springboottestjms.jmscaptor;

import org.springframework.jms.annotation.JmsListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p></p>Test field marked with this annotation will be automatically filled with {@code JmsCaptor<YourMessageType> } instance.
 * The JmsCaptor instance is connected to JMS listener for the specified queue
 * and will automatically receive messages.</p>
 *
 * <p></p>
 *
 * <p>Example:</p>
 *
 * <p></p>
 *
 * <pre>
 * &#64;SpringBootTest
 * public class JmsCaptorTest {
 *
 *     &#64;ImportJmsCaptor(destination = "test_queue")
 *     private JmsCaptor<TestMessage> jmsCaptor;
 *
 *     &#64;Autowired
 *     private JmsTemplate jmsTemplate;
 *
 *     &#64;Test
 *     public void publicAndCaptureMessage() {
 *         jmsTemplate.convertAndSend("test_queue", new TestMessage("foobar"));
 *
 *         await().atMost(1, TimeUnit.SECONDS).until(() ->
 *                 jmsCaptor.getMessages().size() != 0
 *         );
 *
 *         assertEquals("foobar", jmsCaptor.getMessages().get(0).getValue());
 *     }
 * }
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportJmsCaptor {

    /**
     * @see JmsListener#containerFactory()
     */
    String containerFactory() default "";

    /**
     * @see JmsListener#destination() ()
     */
    String destination();
}
