
package Client;

import Sender.UnreliableSender;
import TCP.TCPClient;
import UDP.UDPClient;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Client {
    private TCPClient tcpClient;
    private UDPClient udpClient;
    private InetAddress IPAddress;
    private int port;
    private String role;
    private int isAlive;
    private int playerId;
    private boolean isProposer;
    private boolean isKPU;
    
    /* UDP */
    private InetAddress udpIPAddress;
    private int udpPort;
    private InetAddress udpTargetIPAddress;
    private int udpTargetPort;
    private DatagramSocket datagramSocket;
    private Runnable udpSender;
            
    /* TCP */
    private String tcpIPAddress;
    private int tcpPort;
    private Socket tcpSocket;

   
    public Client (String udpIPAddress, int udpPort, String tcpIPAddress,int tcpPort) throws IOException {
//        this.tcpClient = new TCPClient(IPAddress,serverPort);
//        this.udpClient = new UDPClient(IPAddress,port);
        this.udpPort = udpPort ;
        this.udpIPAddress = InetAddress.getByName(udpIPAddress);
        this.tcpPort = tcpPort;
        this.tcpIPAddress = tcpIPAddress;
        this.tcpSocket = new Socket(tcpIPAddress, tcpPort);

    }
    
    public void start() throws IOException, ParseException{
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        //startUDPClient();
        while(true){
            String sentence = inFromUser.readLine();
            String[] words = sentence.split(" ");
            if(words.length > 1){
                sentence = sentence.substring(words[0].length()+1);
                if(!sentence.equals("")){
                    if(words[0].equals("toOthers")){
                        callUdp(sentence);
                    }else if(words[0].equals("toServer")){
                        callTcp(sentence);
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
    
    public boolean getIsKPU () {
        return this.isKPU;
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
    
    /****************  UDP Method   ****************/
    public void setUdpTargetIPAddress (String IPAddress) throws UnknownHostException {
        this.udpTargetIPAddress = InetAddress.getByName(IPAddress);
    }
    
    public void setUdpTargetPort (int port) {
        this.udpTargetPort = port;
    }
    
    public void addReceiver (String _IPAdress, final int _port) {
        Runnable receiver = new Runnable(){
            public void run(){
                try {
                    receive(_port);
                } catch (IOException ex) {
                    Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            public void receive(int _port) throws IOException{
                DatagramSocket serverSocket = new DatagramSocket(_port);
                byte[] receiveData = new byte[1024];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("RECEIVED: " + sentence);
                    //parsemessage
                    //ngerubah variabel global
                }     
           } 
        };
        new Thread(receiver).start();
    }
    
    public void callUdp (String sentence) throws IOException {
        UnreliableSender unreliableSender = new UnreliableSender(this.datagramSocket);
        if (sentence.equals("quit"))
        {
            this.datagramSocket.close();
        }

        this.sendUdp(sentence, udpTargetIPAddress, udpTargetPort, unreliableSender);
    }
    
    public void sendUdp (String sentence, InetAddress targetAddress, int targetPort, UnreliableSender unreliableSender) throws IOException {
        byte[] sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, targetPort);
        unreliableSender.send(sendPacket);        
    }
    
    public void receiveUdp () throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receiveData = new byte[1024];
        
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("RECEIVED: " + sentence);
        
        }       
    }
    
    public void closeUdp () {
        this.datagramSocket.close();
    }

    /****************  TCP Method   ****************/    
    public void callTcp (String method) throws IOException, ParseException {   
        if (method.equals("join")){
            joinGame();
        } else if (method.equals("leave")){
            leaveGame();
        } else if (method.equals("ready")){
            readyUp();
        } else if (method.equals("client_address")){
            listClient();
        } else if (method.split(" ")[0].equals("get_other")){
            getOther(method.split(" ")[1]);
        }
    }
    
    public void sendTcp (JSONObject jsonRequest) throws IOException {
        DataOutputStream outToServer = new DataOutputStream(this.tcpSocket.getOutputStream());   
        System.out.println(jsonRequest);
        outToServer.writeBytes(jsonRequest.toString() + "\n");   
    }
    
    public JSONObject receiveTcp () throws IOException, ParseException {
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(this.tcpSocket.getInputStream()));   
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inFromServer.readLine());
        JSONObject jsonResponse = (JSONObject) obj;
        return jsonResponse;
    }

    public void closeTcp() throws IOException {
        this.tcpSocket.close();
    }
    
    public void joinGame() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        Scanner input = new Scanner(System.in);

        System.out.print("Enter your username : ");
        String username = input.nextLine();

        jsonRequest.put("method","join");
        jsonRequest.put("username", username);
        jsonRequest.put("udp_address", this.udpIPAddress.getHostAddress());
        jsonRequest.put("udp_port", this.udpPort);
                        
        sendTcp(jsonRequest);
        JSONObject jsonResponse = receiveTcp();
        System.out.println(jsonResponse);
    }
    
    public void leaveGame() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","leave");
        sendTcp(jsonRequest);
        JSONObject jsonResponse = receiveTcp();
        System.out.println(jsonResponse.toString());
    }
    
    public void readyUp() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","ready");
        sendTcp(jsonRequest);
        JSONObject jsonResponse = receiveTcp();
        System.out.println(jsonResponse);
    }
    
    public void listClient() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","client_address");
        sendTcp(jsonRequest);
        JSONObject jsonResponse = receiveTcp();
        System.out.println(jsonResponse);
    }
    
    public void getOther(String playerId) throws IOException, ParseException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","get_other");
        jsonRequest.put("playerId",playerId);
        sendTcp(jsonRequest);
        JSONObject jsonResponse = receiveTcp();
        System.out.println(jsonResponse);
    }

}
