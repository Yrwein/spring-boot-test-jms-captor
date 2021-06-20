package cz.josefczech.springboottestjms.jmscaptor;

import org.springframework.jms.annotation.JmsListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
