package Sniffing;


import DatabaseConnectivity.DatabaseGlobals;
import DatabaseConnectivity.DatabaseQueries;
import Frame.FrameGlobals;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SnifferMain extends Thread{


    static StringBuilder errorBuffer = new StringBuilder(); // For any error messages
//    public static String listAndPickDevice(){
//        Scanner keyboard = new Scanner(System.in);
//
//        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
//
//        //getdevicelist
//
//
//        int r = Pcap.findAllDevs(alldevs, errorBuffer);
//        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
//            System.err.printf("Can't read list of devices, error is %s", errorBuffer.toString());
//            return "no device";
//        }
//
//        System.out.println("NIC's detected on your system");
//
//        int deviceNumber = 0;
//        for(PcapIf device : alldevs){
//            String description = (device.getDescription() != null) ? device.getDescription() : "there is no description for this device";
//            System.out.printf(" %d : %s device location:  [%s]\n\n", deviceNumber++, description, device.getName());
//        }
//
//
//        do {
//            System.out.println("Select the NIC to track.");
//            DatabaseGlobals.deviceToUse = keyboard.nextInt();
//            System.out.println(DatabaseGlobals.deviceToUse);
//        }
//        while (DatabaseGlobals.deviceToUse > alldevs.size());
//
//        PcapIf device = alldevs.get(DatabaseGlobals.deviceToUse);
//
//        DatabaseCreate.create();
//
//        System.out.println(device.getName());
//
//        return device.getName();
//    }

    public static int sniffer() throws SQLException {



        Connection connection = DriverManager.getConnection(DatabaseGlobals.DB_URL,
                DatabaseGlobals.DB_USER, DatabaseGlobals.DB_PASSWORD);




        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;        // 10 seconds in millis

        Pcap startCapture = Pcap.openLive(FrameGlobals.selectedLocation, snaplen, flags, timeout, errorBuffer);


        if(startCapture == null){ System.out.println("Error: " + errorBuffer.toString()); return -1;}

        PcapPacketHandler handlePackets = new PcapPacketHandler() {


            Udp udp = new Udp();
            Tcp tcp = new Tcp();
            Ip4 ip = new Ip4();

            byte[] sIP = new byte[4];
            byte[] dIP = new byte[4];
            Integer sourcePort;
            Integer destinationPort;
            String protocol;
            int i=0;




            @Override
            public void nextPacket(PcapPacket packet, Object o) {

                if (packet.hasHeader(ip)) {
                    String sourceIp = FormatUtils.ip(ip.source());
                    String destinationIp = FormatUtils.ip(ip.destination());


                    if (packet.hasHeader(tcp)) {

                         sourcePort = tcp.source();
                         destinationPort = tcp.destination();
                         protocol = "TCP";
                        // TCP icin
                    } else if (packet.hasHeader(udp)) {
                         sourcePort = udp.source();
                         destinationPort = udp.destination();
                         protocol = "UDP";
                        // UDP icin
                    }

                    System.out.println(" " + ++i + "packet caught" );

                    System.out.println();

                    try {
                        DatabaseQueries.insertPacketProperties(connection, sourceIp, destinationIp,
                                sourcePort, destinationPort, protocol);



                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        };
        startCapture.loop(100, handlePackets, "text");
        System.out.println("bitti.");
        return -1;
    }

}
