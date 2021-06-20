package cz.josefczech.springboottestjms.jmscaptor;

public class ImportJmsCaptorData {

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
