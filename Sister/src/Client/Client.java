
package Client;

import TCP.TCPClient;
import UDP.UDPClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import org.json.simple.parser.ParseException;

public class Client {
    private TCPClient tcpClient;
    private UDPClient udpClient;
    private InetAddress IPAddress;
    private int port;
    private String role;
    private int isAlive;
    private int playerId;
    
    public Client (String IPAddress, int port, int serverPort) throws IOException {
        this.tcpClient = new TCPClient(IPAddress,serverPort);
        this.udpClient = new UDPClient(IPAddress,port);
    }
    
    public void start() throws IOException, ParseException{
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        //startTCPClient();
        startUDPClient();
        while(true){
            String sentence = inFromUser.readLine();
            String[] words = sentence.split(" ");
            if(words.length > 1){
                sentence = sentence.substring(words[0].length()+1);
                if(!sentence.equals("")){
                    if(words[0].equals("toOthers")){
                        udpClient.call(sentence);
                    }else if(words[0].equals("toServer")){
                        tcpClient.call(sentence);
                    }
                }
            }
        }
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
    
    public int getIsAlive () {
        return this.isAlive;
    }
    
    public int getPlayerId () {
        return this.playerId;
    }
    
    public void setIsAlive (int _isAlive) {
        this.isAlive = _isAlive;
    }
}
