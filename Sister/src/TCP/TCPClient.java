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
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author alberttriadrian
 */
public class TCPClient {
    private String IPAddress;
    private int port;
    private Socket socket;
    
    public TCPClient (String _IPAddress, int _port) throws IOException {
        this.IPAddress = _IPAddress;
        this.port = _port;
        this.socket = new Socket(this.IPAddress, this.port); 
    }
    
    public void start () throws IOException, ParseException {   
        this.leaveGame();
    }
    
    public void send (JSONObject jsonRequest) throws IOException {
        DataOutputStream outToServer = new DataOutputStream(this.socket.getOutputStream());   
        System.out.println("TCP Client sending");
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
    
    public void leaveGame() throws IOException, ParseException{
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method","leave");
        this.send(jsonRequest);
        JSONObject jsonResponse = this.receive();
        System.out.println("Leave game Response : " + jsonResponse.toString());
    }
}
