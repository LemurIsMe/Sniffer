# LightSniffer
My Network Sniffer project dedicated to the graduation

========Introduction========

The project and it's concept is fairly simple. A lightweight network packet sniffer that retrieves the available Network Interface Cards from a system, lets the user pick one of them and starts to
log certain network packets such as TCP or UDP into a PostgreSQL table, created for the specific NIC card.

========Technologies========

The project contains 3 main parts, which are Database related classes, Sniffer code itself and the GUI related classes.

The DatabaseConnectivity part uses the JDBC external library and has 3 functional classses and one "Packet" structure.

DatabaseCreate is a class that holds the method "create()" to, as you can guess, create a table in the database, which will be used to  hold packet information in the future.

DatabaseGlobals is the POJO class, that contains database log-in credentials and a few other database related variables needed to be passed through different methods and classes.

DatabaseQueries is the class that holds different Database Query actions to be passed to postgreSQL, in order to add, remove or filter the content of the table.

and the Packet class, which is pretty much related to other parts of the program too but included in the
DatabaseConnectivity anyways, defines the packet structure I'm willing to store.


The Frame part is where everything GUI related is stored. It has DisplayNICTraffic, FrameGlobals and NICSelector parts that creates a somehow functional GUI.

DisplayNICTraffic is a deprecated class. It is supposed to be the primary stage that shows the selected card's
traffic but the future development of the program and the utilisation of threads made me design a whole new one,
rather than editing this one heavily.

FrameGlobals is the POJO class that contains variables that will be altered as a result of user's actions.
It contains the name and the location of selected NIC card.

NICSelector is the class that holds the stage that displays the NIC details to the user and grants a
dropdown menu to pick one of the available ones. It also contains the code to open a separate window, which is dedicated to the selected NIC and displays packet data.


The Sniffing part has the classes that, obviously, makes the sniffing possible. 

PacketGlobals is the POJO class that holds the sniffer related variables and passes them through needed classses and methods.

RetrieveNICInfo is the class thats holds necessary methods to detect the devices, their names and locations in order to pass them to the GUI.

SnifferMain is the class that holds the package specific properties and passes them to be saved in database, by the help of jnetpcap external library.


========Launch========

In order to make it work properly, user needs to edit "DatabaseGlobals" POJO class contents so the program will know what database to connect and operate on.

NICSelector is the class that starts the program.

========Future========

For the future of the program, there are several things to do such as reworking on the code to be cleaner and more efficient, getting rid of unused and deprecated parts etc.
thethings to come are adding a better filtering option for the "timestamp" property, a few more functions such as exporting the captured data as a separated file or
displaying the nic traffic before the card selection, in order to make user know which card handles their traffic and which ones are IDLE.
