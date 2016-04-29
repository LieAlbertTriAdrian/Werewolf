
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
    private String role;
    
    public Client (String IPAddress, int port, int serverPort) throws IOException {
        this.tcpClient = new TCPClient(IPAddress,serverPort);
        this.udpClient = new UDPClient(IPAddress,port);
    }
    
    public void startTCPClient(){
        tcpClient.start();
    }
    
    public void startUDPClient(){
        udpClient.start();
    }
    
    public TCPClient getTCPClient () {
        return this.tcpClient;
    }
    
    public UDPClient getUDPClient () {
        return this.udpClient;
    }
}
