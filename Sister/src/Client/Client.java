
package Client;

import Sender.UnreliableSender;
import TCP.TCPClient;
import UDP.UDPClient;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
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
    private int proposalNumber = 0;
    
    /* UDP */
    private InetAddress udpIPAddress;
    private int udpPort;
    private ArrayList<InetAddress> udpTargetIPAddress;
    private ArrayList<Integer> udpTargetPort;
    private DatagramSocket datagramSocket;
    private Runnable udpSender;
            
    /* TCP */
    private String tcpIPAddress;
    private int tcpPort;
    private Socket tcpSocket;
    private int others;
    private Runnable otherClientsChecker;
   
    public Client (String udpIPAddress, int udpPort, String tcpIPAddress,int tcpPort) throws IOException {
//        this.tcpClient = new TCPClient(IPAddress,serverPort);
//        this.udpClient = new UDPClient(IPAddress,port);
        this.udpPort = udpPort ;
        this.udpIPAddress = InetAddress.getByName(udpIPAddress);
        this.tcpPort = tcpPort;
        this.tcpIPAddress = tcpIPAddress;
        this.tcpSocket = new Socket(tcpIPAddress, tcpPort);
        this.datagramSocket = new DatagramSocket();
        this.udpTargetIPAddress = new ArrayList<InetAddress>();
        this.udpTargetPort = new ArrayList<Integer>();
        otherClientsChecker = new Runnable(){
            public void run(){
                try {
                    checkOtherClients();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        new Thread(otherClientsChecker).start();
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
                        sentence = sentence.substring(words[1].length()+1);
                        callUdp(sentence,Integer.parseInt(words[1]));
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
    
    public void checkOtherClients() throws IOException, ParseException, InterruptedException {
        JSONObject jsonResponse;
        //get other client//
        addReceiver();
        do{
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("method","client_address");
            sendTcp(jsonRequest);
            jsonResponse = receiveTcp();
            ArrayList<JSONObject> jr = (ArrayList) jsonResponse.get("clients");
            if(udpTargetIPAddress.size() == 0){
                for(int i = 0; i < jr.size(); i++){
                    udpTargetIPAddress.add(InetAddress.getByName(jr.get(i).get("address").toString()));
                    udpTargetPort.add(Integer.parseInt(jr.get(i).get("port").toString()));
                }
            }else if(jr.size() > udpTargetIPAddress.size()){
                int i = jr.size() - 1;
                udpTargetIPAddress.add(InetAddress.getByName(jr.get(i).get("address").toString()));
                udpTargetPort.add(Integer.parseInt(jr.get(i).get("port").toString()));
            }
            sleep(1000);
        }while(true);
    }
    
    /****************  UDP Method   ****************/    
    public void addReceiver () {
        Runnable receiver = new Runnable(){
            int index;
            public void run(){
                try {
                    index = udpTargetPort.size()-1;
                    receive();
                } catch (IOException ex) {
                    Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            public void receive() throws IOException{
                int i = index;
                System.out.println(i);
                System.out.println(udpPort);
                DatagramSocket serverSocket = new DatagramSocket(udpPort);
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
    
    public void callUdp (String sentence, int playerId) throws IOException {
        UnreliableSender unreliableSender = new UnreliableSender(this.datagramSocket);
        if (sentence.equals("quit"))
        {
            this.datagramSocket.close();
        }
        System.out.println(playerId+" "+udpTargetIPAddress.get(playerId)+" "+udpTargetPort.get(playerId));
        switch (sentence) {
            case "prepare_proposal" :
                paxosPrepareProposal(playerId);
                break;
        }    
    }
    
    public void sendUdp (JSONObject jsonRequest, InetAddress targetAddress, int targetPort, UnreliableSender unreliableSender) throws IOException {
        byte[] sendData = jsonRequest.toString().getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, targetPort);
        unreliableSender.send(sendPacket);        
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
//        System.out.println(jsonRequest);
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
    
    public void paxosPrepareProposal (int playerId) throws IOException{
        UnreliableSender unreliableSender = new UnreliableSender(this.datagramSocket);
        proposalNumber++;
        JSONObject jsonRequest = new JSONObject();
        int[] proposalID = {proposalNumber, playerId};
        jsonRequest.put("method", "prepare_proposal");
        jsonRequest.put("proposal_id", proposalID);
        this.sendUdp(jsonRequest, udpTargetIPAddress.get(playerId), udpTargetPort.get(playerId), unreliableSender);
    }
    
    public void clientAcceptProposal(int playerId) throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","accepted_proposal");
        jsonRequest.put("kpu_id", playerId);
        jsonRequest.put("description","KPU is selected");
        sendTcp(jsonRequest);
        JSONObject jsonResponse = receiveTcp();
        System.out.println(jsonResponse);
    }
    

    public void killWerewolf(int player_id) throws IOException, ParseException{
        UnreliableSender unreliableSender = new UnreliableSender(this.datagramSocket);
        org.json.JSONObject jsonRequest = new org.json.JSONObject();
        jsonRequest.put("method", "vote_werewolf");
        jsonRequest.put("player_id", player_id);
        //sendUdp(jsonRequest, targetIPAddress, targetPort, unreliableSender);
        System.out.println(jsonRequest);
        org.json.JSONObject jsonResponse = new org.json.JSONObject();
        //jsonResponse = receiveUdp();
        System.out.println(jsonResponse);
    }
    
    public void killCivilian(int player_id) throws IOException, ParseException{
        UnreliableSender unreliableSender = new UnreliableSender(this.datagramSocket);
        org.json.JSONObject jsonRequest = new org.json.JSONObject();
        jsonRequest.put("method", "vote_civilian");
        jsonRequest.put("player_id", player_id);
        //sendUdp(jsonRequest, udpTargetIPAddress, targetPort, unreliableSender);
        System.out.println(jsonRequest);
        org.json.JSONObject jsonResponse = new org.json.JSONObject();
        //jsonResponse = receive();
        System.out.println(jsonResponse);
    }
}
