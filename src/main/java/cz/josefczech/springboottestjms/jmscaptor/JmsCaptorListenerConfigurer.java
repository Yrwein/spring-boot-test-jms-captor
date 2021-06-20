package cz.josefczech.springboottestjms.jmscaptor;

import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.listener.MessageListenerContainer;

public class JmsCaptorListenerConfigurer implements JmsListenerConfigurer {

    private final SimpleJmsListenerEndpoint endpoint;

    private final JmsListenerContainerFactory<MessageListenerContainer> containerFactory;

    public JmsCaptorListenerConfigurer(
            SimpleJmsListenerEndpoint endpoint,
            JmsListenerContainerFactory<MessageListenerContainer> containerFactory
    ) {
        this.endpoint = endpoint;
        this.containerFactory = containerFactory;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        registrar.registerEndpoint(endpoint, containerFactory);
    }
}
