package Sniffing;

import Frame.NICSelector;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

public class RetrieveNICInfo {

    static StringBuilder errorBuffer = new StringBuilder(); // For any error messages

    public static List<NICSelector.NICCard> listNICs(){
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs


        int r = Pcap.findAllDevs(alldevs, errorBuffer);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errorBuffer.toString());
        }

        int deviceNumber = 0;
        List<NICSelector.NICCard> nicCards = new ArrayList<>();
        for(PcapIf device : alldevs){
            String description = (device.getDescription() != null) ? device.getDescription() : "there is no description for this device";
            //System.out.printf(" %d : %s device location:  [%s]\n\n", deviceNumber++, description, device.getName());
            NICSelector.NICCard card1 = new NICSelector.NICCard(++deviceNumber, description, device.getName());
            nicCards.add(card1);
        }
        return nicCards;

    }


}
