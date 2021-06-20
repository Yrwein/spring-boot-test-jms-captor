package cz.josefczech.springboot.test.jmscaptor.config;

import cz.josefczech.springboot.test.jmscaptor.ImportJmsCaptor;

/**
 * DTO for {@link ImportJmsCaptor} fields so we don't have to deal with some proxies later.
 */
class ImportJmsCaptorData {

    private final String containerFactory;

    private final String destination;

    public ImportJmsCaptorData(String containerFactory, String destination) {
        this.containerFactory = containerFactory;
        this.destination = destination;
    }

    public String getContainerFactory() {
        return containerFactory;
    }

    public String getDestination() {
        return destination;
    }
}
