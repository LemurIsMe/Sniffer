package DatabaseConnectivity;

//Internet paketleri icin obje
public class Packet {
    private int id;
    private String sourceIp;
    private String destIp;
    private int sourcePort;
    private int destPort;
    private String protocol;
    private String timestamp;

    public Packet(int id, String sourceIp, String destIp, int sourcePort, int destPort, String protocol, String timestamp) {
        this.id = id;
        this.sourceIp = sourceIp;
        this.destIp = destIp;
        this.sourcePort = sourcePort;
        this.destPort = destPort;
        this.protocol = protocol;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getDestIp() {
        return destIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public int getDestPort() {
        return destPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

