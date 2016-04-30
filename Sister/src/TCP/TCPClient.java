/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author alberttriadrian
 */
public class TCPClient extends Thread{
    private String IPAddress;
    private int port;
    private Socket socket;
    public String method;
    public int timeStamp;
    
    public TCPClient (String _IPAddress, int _port) throws IOException {
        this.IPAddress = _IPAddress;
        this.port = _port;
        this.socket = new Socket(this.IPAddress, this.port); 
        this.timeStamp = 0;
    }
    
    public void run () {
//        try {
//            call();
//        } catch (IOException ex) {
//            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public void call (String method) throws IOException, ParseException {   
        if (method.equals("join")){
            Scanner input = new Scanner(System.in);
            System.out.print("Enter your username : ");
            String username = input.nextLine();
            joinGame(username);
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
    
    public void send (JSONObject jsonRequest) throws IOException {
        DataOutputStream outToServer = new DataOutputStream(this.socket.getOutputStream());   
        System.out.println(jsonRequest);
        outToServer.writeBytes(jsonRequest.toString() + "\n");        
    }
    
    public JSONObject receive () throws IOException, ParseException {
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));   
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inFromServer.readLine());
        JSONObject jsonResponse = (JSONObject) obj;
        return jsonResponse;
    }

    public void close() throws IOException {
        this.socket.close();
    }
    
//    public static void main(String argv[]) throws Exception {   
//            String sentence;   
//            String modifiedSentence;    
//            BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
//            Socket clientSocket = new Socket("localhost", 6789);   
//            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
//            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
//            sentence = inFromUser.readLine();   outToServer.writeBytes(sentence + '\n');   
//            modifiedSentence = inFromServer.readLine();   
//            System.out.println("FROM SERVER: " + modifiedSentence);   
//            clientSocket.close();  
//    } 
    public void joinGame(String username) throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","join");
        jsonRequest.put("username", username);
        jsonRequest.put("udp_address", this.IPAddress);
        jsonRequest.put("udp_port", this.port);
                        
        send(jsonRequest);
        JSONObject jsonResponse = receive();
        System.out.println(jsonResponse);
    }
    
    public void leaveGame() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","leave");
        this.send(jsonRequest);
        JSONObject jsonResponse = this.receive();
        System.out.println(jsonResponse.toString());
    }
    
    public void readyUp() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","ready");
        send(jsonRequest);
        JSONObject jsonResponse = receive();
        System.out.println(jsonResponse);
    }
    
    public void listClient() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","client_address");
        send(jsonRequest);
        JSONObject jsonResponse = receive();
        System.out.println(jsonResponse);
    }
    
    public void infoWerewolfKilled(int vote_status, int player_killed, Array vote_result) throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method", "vote_result_werewolf");
        jsonRequest.put("vote_status", vote_status);
        jsonRequest.put("player_killed", player_killed);
        jsonRequest.put("vote_result", vote_result);
        send(jsonRequest);
        JSONObject jsonResponse = receive();
        System.out.println(jsonResponse);
    }
    
    public void infoCivilianKilled(int vote_status, int player_killed, Array vote_result) throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method", "vote_result_civilian");
        jsonRequest.put("vote_status", vote_status);
        jsonRequest.put("player_killed", player_killed);
        jsonRequest.put("vote_result", vote_result);
        send(jsonRequest);
        JSONObject jsonResponse = receive();
        System.out.println(jsonResponse);
    }

    public void getOther(String playerId) throws IOException, ParseException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","get_other");
        jsonRequest.put("playerId",playerId);
        send(jsonRequest);
        JSONObject jsonResponse = receive();
        System.out.println(jsonResponse);
    }
}
