
package Client;

import TCP.TCPClient;
import UDP.UDPClient;
import java.io.IOException;
import java.net.InetAddress;

public class Client {
    private TCPClient tcpClient;
    private UDPClient udpClient;
    private InetAddress IPAddress;
    private int port;

    
    public Client (String IPAddress, int port) throws IOException {
        this.tcpClient = new TCPClient(IPAddress,port);
        this.udpClient = new UDPClient(IPAddress,port);
    }
    
    public TCPClient getTCPClient () {
        return this.tcpClient;
    }
    
    public UDPClient getUDPClient () {
        return this.udpClient;
    }
}
